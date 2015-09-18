package edu.usc.tcors;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

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

import edu.usc.tcors.utils.TcorsMinerUtils;

/*
 * command line execution: java -cp TcorsMiner.jar edu.usc.tcors.TcorsInstagramScraper
 */

public class TcorsInstagramScraper {
	
	final String latest_id_sql = "SELECT min_id " +
			"FROM instagram_terms " +
			"WHERE search_term = ?";
	
	final String instagram_sql = "REPLACE INTO instagram (id, createdTime, username, caption, likes, comments, url, location, storePicture) " +
			"VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	final String comments_sql = "REPLACE INTO instagram_comments (id, parent_id, username, comment, createdTime) " +
			"VALUE (?, ?, ?, ?, ?)";
	
	final String users_sql = "INSERT IGNORE INTO instagram_users (id, fullname, bio, username) " +
			"VALUE (?, ?, ?, ?)";
	
	final String term_sql = "REPLACE INTO instagram_terms (search_term, min_id) " +
			"VALUE (?, ?)";
	
	private static Connection conn;
	
	final int returnSize = 33;

	private Connection getConnection() {
		return TcorsInstagramScraper.conn;
	}
	
	private static void setConnection(Connection connection) {
		conn = connection;
	}
	
	public static void main(String[] args) throws InstagramException {
		
		while (true) {
			
			TcorsMinerUtils tmu = new TcorsMinerUtils();
			try {
				setConnection(tmu.getDBConn("configuration.properties"));
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			
			TcorsInstagramScraper tis = new TcorsInstagramScraper();
		
			try {
				tis.go();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			try {
				tis.getConnection().close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			try {
				System.out.println("Taking a 5 minute nap...\n\n");
				Thread.sleep(300000); // 5 minute wait between ids chunks
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * TODO refactor out into the utilities
	 */
	
	private Properties getProps() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("jInstagram.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		return prop;
	}
	
	private Token getSecretToken() {
		Properties iProp = getProps();
		Token secretToken = new Token(iProp.getProperty("oauth.accessToken"),null);
		return secretToken;
	}
	
	private void go() throws SQLException {
		Token secretToken = getSecretToken();
		Instagram instagram = new Instagram(secretToken);
		
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
				String full_min_id = "";
				String min_id = "";
				
				// grab the post ID portion without user ID
				System.out.println("Working on term: " + term);
				full_min_id = getLatestID(term);
				System.out.println("Found latest ID: " + full_min_id);
				if (full_min_id != "") {
					min_id = full_min_id.substring(0, full_min_id.indexOf("_"));
				}
				
				// get data from Instagram
				List<MediaFeedData> mediaList = getPostsByTerm(instagram, term, min_id);
				
				System.out.println("Updating DB...");
				String new_min_id = "";
				
				PreparedStatement instagram_ps = null;
				PreparedStatement comments_ps = null;
				PreparedStatement users_ps = null;
				PreparedStatement term_ps = null;
				
				try {
				
					instagram_ps = getConnection().prepareStatement(instagram_sql);
					comments_ps = getConnection().prepareStatement(comments_sql);
					users_ps = getConnection().prepareStatement(users_sql);
					term_ps = getConnection().prepareStatement(term_sql);
					
					String search_term = "";
					
					// iterate for each data chunk
					for (MediaFeedData mfd : mediaList) {
	
						String id = mfd.getId();
						
						// process post, comment, and user data separately
						instagram_ps = parseInstagramPostData(instagram_ps, mfd);
						comments_ps = parseInstagramCommentData(comments_ps, mfd);
						users_ps = parseInstagramUsersData(users_ps, mfd);
						
						/*
						 * get the latest ID for future runs
						 */
						if (new_min_id == "") {
							search_term = term;
							new_min_id = id;
							
							term_ps.setString(1, search_term);
							term_ps.setString(2, new_min_id);
						}
						
					}
					
					// insert into database
					instagram_ps.executeBatch();
					users_ps.executeBatch();
					comments_ps.executeBatch();
					
					if (search_term != "") {
						term_ps.execute();
					}
					
					System.out.println("Finished DB update");
				
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (NullPointerException e1) {
					e1.printStackTrace();
				} finally {
					instagram_ps.close();
					users_ps.close();
					comments_ps.close();
					term_ps.close();
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
	
	private PreparedStatement parseInstagramPostData(PreparedStatement instagram_ps, MediaFeedData mfd) throws SQLException {
		
		String caption = "";
		try {
			caption = mfd.getCaption().getText();
		} catch (NullPointerException n) {
			
		}
		
		Timestamp ts = new Timestamp(Long.parseLong(mfd.getCreatedTime())*1000);
		String id = mfd.getId();
		String url = mfd.getImages().getStandardResolution().getImageUrl();
		int likes = mfd.getLikes().getCount();
		Location location = mfd.getLocation();
		String location_name = "";
		if (location != null) {
			location_name = location.getName();
		}
		
		User user = mfd.getUser();
		String username = user.getUserName();

		Comments comments = mfd.getComments();
		int comments_count = comments.getCount();
		
		instagram_ps.setString(1, id);
		instagram_ps.setTimestamp(2, ts);
		instagram_ps.setString(3, username);
		instagram_ps.setString(4, caption);
		instagram_ps.setInt(5, likes);
		instagram_ps.setInt(6, comments_count);
		instagram_ps.setString(7, url);
		instagram_ps.setString(8, location_name);
		instagram_ps.setInt(9, 0);
		
		instagram_ps.addBatch();
		
		return instagram_ps;
	}
	
	private PreparedStatement parseInstagramCommentData(PreparedStatement comments_ps, MediaFeedData mfd) throws SQLException {

		String id = mfd.getId();
		
		Comments comments = mfd.getComments();
		
		// process comments
		List<CommentData> cd = comments.getComments();
		for (CommentData comment_data : cd) {
			String comment_id = comment_data.getId();
			String comment_username = comment_data.getCommentFrom().getUsername();
			String comment_text = comment_data.getText();
			Timestamp comment_created_time = new Timestamp(Long.parseLong(comment_data.getCreatedTime())*1000);
			
			comments_ps.setString(1, comment_id);
			comments_ps.setString(2, id);
			comments_ps.setString(3, comment_username);
			comments_ps.setString(4, comment_text);
			comments_ps.setTimestamp(5, comment_created_time);
			
			comments_ps.addBatch();
		}
		
		return comments_ps;
	}
	
	private PreparedStatement parseInstagramUsersData(PreparedStatement users_ps, MediaFeedData mfd) throws SQLException {
		
		User user = mfd.getUser();
		String username = user.getUserName();
		String user_bio = user.getBio();
		String user_fullname = user.getFullName();
		String user_id = user.getId();

		users_ps.setString(1, user_id);
		users_ps.setString(2, user_fullname);
		users_ps.setString(3, user_bio);
		users_ps.setString(4, username);
		
		users_ps.addBatch();
		
		return users_ps;
	}
	
	/*
	 * use Instagram API to retrieve posts based on a single term
	 */
	
	private List<MediaFeedData> getPostsByTerm(Instagram instagram, String term, String min_id) {
		
		TagMediaFeed mediaFeed = null;
		List<MediaFeedData> mediaList = null;
		int limit = 0;
		
		try {
			
			mediaFeed = instagram.getRecentMediaTags(term, min_id, "", returnSize);
			mediaList = mediaFeed.getData();
			System.out.println("Step 1 size:" + mediaFeed.getData().size());
			
			MediaFeed recentMediaNextPage = null;
			// if (mediaFeed.getData().size() >= 33) { // or returnSize?
			if (mediaFeed.getPagination().hasNextPage()) {
				recentMediaNextPage = instagram.getRecentMediaNextPage(mediaFeed.getPagination());
			}
			
			// TODO the above/below if loops need to be cleaned up
			int counter = 1;
			
			limit = mediaFeed.getRemainingLimitStatus();
			System.out.println("mediaFeed REMAIN:" + limit);
			
			if (recentMediaNextPage != null) {
				while (recentMediaNextPage.getPagination() != null && counter < 10) {
					
					mediaList.addAll(recentMediaNextPage.getData());

					if (recentMediaNextPage.getPagination() != null) {
						recentMediaNextPage = instagram.getRecentMediaNextPage(recentMediaNextPage.getPagination());
					}
					
					// test
					// System.out.println("test ID:" + recentMediaNextPage.getData().get(0).getId());
					
					counter++;
					System.out.println("Counter (pages):" + counter);
				}
			}
			System.out.println("mediaList size (x33):" + mediaList.size());
		} catch (InstagramException e) {
			e.printStackTrace();
		}
		
		return mediaList;
	}
}
