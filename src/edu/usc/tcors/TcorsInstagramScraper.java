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

public class TcorsInstagramScraper {
	
	public static void main(String[] args) throws InstagramException {

		Connection conn = null;
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/TcorsTwitter";
			conn = DriverManager.getConnection(url,"root","309root");
			
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		TcorsInstagramScraper tis = new TcorsInstagramScraper();
		tis.getStuff(conn);
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
	
	private void getStuff(Connection conn) {
		Token secretToken = getSecretToken();
		Instagram instagram = new Instagram(secretToken);
		
		// get search terms
		String[] test = new String[]{"ecigarette"};
		
		// for each search term
		for (String term : test) {
		
			// get data from Instagram
			
			// use min_tag_id with highest already retrieved, to get newer ones
			
			TagMediaFeed mediaFeed = null;
			List<MediaFeedData> mediaList = null;
			int limit = 0;
			
			try {
				// mediaFeed = instagram.getRecentMediaTags(term, 100); // max is only 33
				mediaFeed = instagram.getRecentMediaTags(term, "1053911742112114767", "");
				mediaList = mediaFeed.getData();
				System.out.println("Step 1 size:" + mediaFeed.getData().size());
				
				MediaFeed recentMediaNextPage = instagram.getRecentMediaNextPage(mediaFeed.getPagination());
				int counter = 1;
				
				limit = mediaFeed.getRemainingLimitStatus();
				System.out.println("mediaFeed REMAIN:" + limit);
				
				while (recentMediaNextPage.getPagination() != null && limit > 4990) {
					
					mediaList.addAll(recentMediaNextPage.getData());
					recentMediaNextPage = instagram.getRecentMediaNextPage(recentMediaNextPage.getPagination());
					
					counter++;
					System.out.println("Counter (pages):" + counter);
					limit = mediaFeed.getRemainingLimitStatus();
					System.out.println("mediaFeed REMAIN:" + limit);
					limit = 4800;
				}
				System.out.println("Size (x33):" + mediaList.size());
			} catch (InstagramException e) {
				e.printStackTrace();
			}
			
			// #2 retrieve and store individual pieces of data
			String instagram_sql = "REPLACE INTO instagram (id, createdTime, username, caption, likes, comments, url, location) " +
					"VALUE (?, ?, ?, ?, ?, ?, ?, ?)";
			
			String comments_sql = "REPLACE INTO instagram_comments (id, parent_id, username, comment, createdTime) " +
					"VALUE (?, ?, ?, ?, ?)";
			
			String users_sql = "INSERT IGNORE INTO instagram_users (id, fullname, bio, username) " +
					"VALUE (?, ?, ?, ?)";
			
			System.out.println("Updating DB...");
			
			try {
				PreparedStatement instagram_ps = conn.prepareStatement(instagram_sql);
				PreparedStatement comments_ps = conn.prepareStatement(comments_sql);
				PreparedStatement users_ps = conn.prepareStatement(users_sql);
			
				for (MediaFeedData mfd : mediaList) {
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
					String user_bio = user.getBio();
					String user_fullname = user.getFullName();
					String user_id = user.getId();

					Comments comments = mfd.getComments();
					int comments_count = comments.getCount();
					
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
					
					// store data
					instagram_ps.setString(1, id);
					instagram_ps.setTimestamp(2, ts);
					instagram_ps.setString(3, username);
					instagram_ps.setString(4, caption);
					instagram_ps.setInt(5, likes);
					instagram_ps.setInt(6, comments_count);
					instagram_ps.setString(7, url);
					instagram_ps.setString(8, location_name);
					
					instagram_ps.addBatch();
					
					users_ps.setString(1, user_id);
					users_ps.setString(2, user_fullname);
					users_ps.setString(3, user_bio);
					users_ps.setString(4, username);
					
					users_ps.addBatch();
				}
				
				instagram_ps.executeBatch();
				users_ps.executeBatch();
				comments_ps.executeBatch();
				
				System.out.println("Finished DB update");
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// pause
	}
}
