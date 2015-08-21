package edu.usc.tcors;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

import edu.usc.tcors.utils.TcorsTwitterUtils;

public class TcorsInstagramScraper {
	
	final String instagram_sql = "REPLACE INTO instagram (id, createdTime, username, caption, likes, comments, url, location) " +
			"VALUE (?, ?, ?, ?, ?, ?, ?, ?)";
	
	final String comments_sql = "REPLACE INTO instagram_comments (id, parent_id, username, comment, createdTime) " +
			"VALUE (?, ?, ?, ?, ?)";
	
	final String users_sql = "INSERT IGNORE INTO instagram_users (id, fullname, bio, username) " +
			"VALUE (?, ?, ?, ?)";
	
	final String term_sql = "REPLACE INTO instagram_terms (search_term, min_id) " +
			"VALUE (?, ?)";
	
	private static Connection conn;

	private Connection getConnection() {
		return TcorsInstagramScraper.conn;
	}
	
	private static void setConnection(Connection connection) {
		conn = connection;
	}
	
	public static void main(String[] args) throws InstagramException {
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/TcorsTwitter";
			setConnection(DriverManager.getConnection(url,"root","309root"));
			
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		TcorsInstagramScraper tis = new TcorsInstagramScraper();
		tis.getStuff();
	}
	
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
	
	private void getStuff() {
		Token secretToken = getSecretToken();
		Instagram instagram = new Instagram(secretToken);
		
		// get search terms
		// String[] test = new String[]{"ecigarette","ecig"};
		String[] test = {};
		try {
			test = TcorsTwitterUtils.loadSearchTerms();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// for each search term
		for (String term : test) {
			
			// get data from Instagram
			List<MediaFeedData> mediaList = getPostsByTerm(instagram, term);
			
			System.out.println("Updating DB...");
			
			try {
			
				PreparedStatement instagram_ps = getConnection().prepareStatement(instagram_sql);
				PreparedStatement comments_ps = getConnection().prepareStatement(comments_sql);
				PreparedStatement users_ps = getConnection().prepareStatement(users_sql);
				PreparedStatement term_ps = getConnection().prepareStatement(term_sql);
				
				String search_term = "";
				String min_id = "";
				
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
					if (min_id == "") {
						search_term = term;
						min_id = id;
						
						term_ps.setString(1, search_term);
						term_ps.setString(2, min_id);
					}
					
				}
				
				// insert into database
				instagram_ps.executeBatch();
				users_ps.executeBatch();
				comments_ps.executeBatch();
				term_ps.execute();
				
				System.out.println("Finished DB update");
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// pause
	}
	
	private PreparedStatement parseInstagramPostData(PreparedStatement instagram_ps, MediaFeedData mfd) throws SQLException {
		
		String caption = mfd.getCaption().getText();
		
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
	
	private List<MediaFeedData> getPostsByTerm(Instagram instagram, String term) {
		
		TagMediaFeed mediaFeed = null;
		List<MediaFeedData> mediaList = null;
		int limit = 0;
		
		try {
			// mediaFeed = instagram.getRecentMediaTags(term, 100); // max is only 33
			mediaFeed = instagram.getRecentMediaTags(term, "1056644176036480951", "");
			mediaList = mediaFeed.getData();
			System.out.println("Step 1 size:" + mediaFeed.getData().size());
			
			MediaFeed recentMediaNextPage = null;
			if (mediaFeed.getData().size() == 33) {
				recentMediaNextPage = instagram.getRecentMediaNextPage(mediaFeed.getPagination());
			}
			int counter = 1;
			
			limit = mediaFeed.getRemainingLimitStatus();
			System.out.println("mediaFeed REMAIN:" + limit);
			
			if (recentMediaNextPage != null) {
				while (recentMediaNextPage.getPagination() != null && limit > 4990) {
					
					mediaList.addAll(recentMediaNextPage.getData());
					recentMediaNextPage = instagram.getRecentMediaNextPage(recentMediaNextPage.getPagination());
					
					counter++;
					System.out.println("Counter (pages):" + counter);
					limit = mediaFeed.getRemainingLimitStatus();
					System.out.println("mediaFeed REMAIN:" + limit);
					limit = 4800;
				}
			}
			System.out.println("Size (x33):" + mediaList.size());
		} catch (InstagramException e) {
			e.printStackTrace();
		}
		
		return mediaList;
	}
}
