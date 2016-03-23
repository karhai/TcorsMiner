package edu.usc.tcors;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

import edu.usc.tcors.utils.TcorsInstagramUtils;
import edu.usc.tcors.utils.TcorsMinerUtils;

/*
 * command line execution: java -cp TcorsMiner.jar edu.usc.tcors.TcorsInstagramScraper
 */

public class TcorsInstagramScraper {
	
	final String latest_id_sql = "SELECT min_id " +
			"FROM instagram_terms " +
			"WHERE search_term = ?";
	
	final String latest_time_sql = "SELECT latest_time " +
			"FROM instagram_last_run " +
			"WHERE id = 1";
	
	final String instagram_sql = "REPLACE INTO instagram (id, createdTime, username, caption, likes, comments, url, location, storePicture, latitude, longitude) " +
			"VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	final String comments_sql = "REPLACE INTO instagram_comments (id, parent_id, username, comment, createdTime) " +
			"VALUE (?, ?, ?, ?, ?)";
	
	final String last_run_sql = "REPLACE INTO instagram_last_run (id, latest_time) " +
			"VALUE (1, ?)";
	
	/*
	 * TODO consider using REPLACE, which would force repopulation of user meta-data
	 * but at the cost of spending quota
	 */
	final String users_sql = "INSERT IGNORE INTO instagram_users (id, fullname, bio, username) " +
			"VALUE (?, ?, ?, ?)";
	
	final String term_sql = "REPLACE INTO instagram_terms (search_term, min_id) " +
			"VALUE (?, ?)";
	
	private static Connection conn;
	
	final int returnSize = 33;
	final int loop_reset = 4; // 1 = 5 min

	private Connection getConnection() {
		return TcorsInstagramScraper.conn;
	}
	
	private static void setConnection(Connection connection) {
		conn = connection;
	}
	
	public static void main(String[] args) throws InstagramException {
		
		int delayed_loop = 0;
		while (true) {
			
			TcorsMinerUtils tmu = new TcorsMinerUtils();
			try {
				setConnection(tmu.getDBConn("configuration.properties"));
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			
			TcorsInstagramScraper tis = new TcorsInstagramScraper();
		
			Token secretToken = TcorsInstagramUtils.getSecretToken();
			Instagram instagram = new Instagram(secretToken);
			
			/*
			 * run the historical scrape of keywords every 10 minutes
			 */

			long now = System.currentTimeMillis()/1000;
			
			try {
				tis.go(instagram);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			try {
				tis.updateLastRun(now);
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			/*
			 * run the image download immediately after
			 */

			TcorsInstagramUtils.getImages(tis.getConnection());
			
			/*
			 * run the user bios scrape every 20 minutes
			 */
			if (++delayed_loop >= 4) {
				delayed_loop = 0;
				TcorsInstagramUtils.updateUsers(tis.getConnection(), instagram);
			}
			
			try {
				tis.getConnection().close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			try {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				System.out.println(LocalDateTime.now().format(dtf) + "... Taking a 10 minute nap...\n\n");
				Thread.sleep(600000); // 10 minute wait between ids chunks
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateLastRun(long now) throws SQLException {
		PreparedStatement last_run_ps = null;
		last_run_ps = getConnection().prepareStatement(last_run_sql);
		last_run_ps.setLong(1, now);
		last_run_ps.execute();
	}

	private void go(Instagram instagram) throws SQLException {
		
		// get search terms
//		String[] test = new String[]{"ecigarette","ecig","e-hookah"};
		String[] terms = {};
		try {
			terms = TcorsMinerUtils.loadSearchTerms();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// for each search term
		for (String term : terms) {
			
			// ignore terms with unacceptable symbols
			if (!term.contains("-") && !term.contains(" ")) {
			
				// get latest ID
				// String full_min_id = "";
				// String min_id = "";
				
				// get last run time
				long last_run_time = 0L;
				
				// grab the post ID portion without user ID
				System.out.println("Working on term: " + term);

//				full_min_id = getLatestID(term);
//				System.out.println("Found latest ID: " + full_min_id);
//				if (full_min_id != "") {
//					min_id = full_min_id.substring(0, full_min_id.indexOf("_"));
//				}

				last_run_time = getLatestTime();
				
				// get data from Instagram
				List<MediaFeedData> mediaList = TcorsInstagramUtils.getPostsByTerm(instagram, term, last_run_time);
				
				System.out.println("Updating DB...");
				String new_min_id = "";
				
				PreparedStatement instagram_ps = null;
				PreparedStatement comments_ps = null;
				PreparedStatement users_ps = null;
				// PreparedStatement term_ps = null;
				
				try {
				
					instagram_ps = getConnection().prepareStatement(instagram_sql);
					comments_ps = getConnection().prepareStatement(comments_sql);
					users_ps = getConnection().prepareStatement(users_sql);
					// term_ps = getConnection().prepareStatement(term_sql);
					
					String search_term = "";
					
					// iterate for each data chunk
					for (MediaFeedData mfd : mediaList) {
	
						String id = mfd.getId();
						
						// process post, comment, and user data separately
						instagram_ps = TcorsInstagramUtils.parseInstagramPostData(instagram_ps, mfd);
						comments_ps = TcorsInstagramUtils.parseInstagramCommentData(comments_ps, mfd);
						users_ps = TcorsInstagramUtils.parseInstagramUsersData(users_ps, mfd);
						
						/*
						 * get the latest ID for future runs
						 */
//						if (new_min_id == "") {
//							search_term = term;
//							new_min_id = id;
//							
//							term_ps.setString(1, search_term);
//							term_ps.setString(2, new_min_id);
//						}
						
					}
					
					// insert into database
					instagram_ps.executeBatch();
					users_ps.executeBatch();
					comments_ps.executeBatch();
					
//					if (search_term != "") {
//						term_ps.execute();
//					}
					
					System.out.println("Finished DB update");
				
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (NullPointerException e1) {
					e1.printStackTrace();
				} finally {
					instagram_ps.close();
					users_ps.close();
					comments_ps.close();
//					term_ps.close();
				}
			}
		}
	}
	
	private String getLatestID(String term) throws SQLException {
		String min_id = "";
		PreparedStatement st = null;
		try {
			st = getConnection().prepareStatement(latest_id_sql);
			st.setString(1, term);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				min_id = rs.getString("min_id");
			}
			
			st.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			st.close();
		}
		return min_id;
	}
	
	private long getLatestTime() throws SQLException {
		long latest_time = 0L;
		PreparedStatement st = null;
		try {
			st = getConnection().prepareStatement(latest_time_sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				latest_time = rs.getLong("latest_time");
			}
			
			st.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			st.close();
		}
		return latest_time;
	}
}
