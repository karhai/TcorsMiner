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
import org.jinstagram.entity.common.Comments;
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
		
			// #1 get data from Instagram
			
			TagMediaFeed mediaFeed = null;
			List<MediaFeedData> mediaList = null;
			
			try {
				mediaFeed = instagram.getRecentMediaTags(term, 100);
				
				mediaList = mediaFeed.getData();
				System.out.println("Step 1 size:" + mediaFeed.getData().size());
				
				MediaFeed recentMediaNextPage = instagram.getRecentMediaNextPage(mediaFeed.getPagination());
				int counter = 1;
				while (recentMediaNextPage.getPagination() != null && counter < 10) {
					mediaList.addAll(recentMediaNextPage.getData());
					recentMediaNextPage = instagram.getRecentMediaNextPage(recentMediaNextPage.getPagination());
					counter++;
					System.out.println("Counter (pages):" + counter);
				}
				System.out.println("Size (x33):" + mediaList.size());
			} catch (InstagramException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("mediaFeed APILS:" + mediaFeed.getAPILimitStatus());
			System.out.println("mediaFeed REMAIN:" + mediaFeed.getRemainingLimitStatus());
			
			// #2 retrieve and store individual pieces of data
			String sql = "REPLACE INTO instagram (id, createdTime, username, caption, likes, comments, url) " +
					"VALUE (?, ?, ?, ?, ?, ?, ?)";
			System.out.println("Updating DB...");
			
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
			
				for (MediaFeedData mfd : mediaList) {
					String caption = mfd.getCaption().getText();
					Comments comments = mfd.getComments();
					int comments_count = comments.getCount();
					// process comments
					// processComments(comments);
					Timestamp ts = new Timestamp(Long.parseLong(mfd.getCreatedTime())*1000);
					String id = mfd.getId();
					String url = mfd.getImages().getStandardResolution().getImageUrl();
					int likes = mfd.getLikes().getCount();
					// String location = mfd.getLocation().getName();
					User user = mfd.getUser();
					String username = user.getUserName();
					// process user
					// processUser(user);
					
					// store data
					ps.setString(1, id);
					ps.setTimestamp(2, ts);
					ps.setString(3, username);
					ps.setString(4, caption);
					ps.setInt(5, likes);
					ps.setInt(6, comments_count);
					ps.setString(7, url);
					
					ps.addBatch();
				}
				
				ps.executeBatch();
				System.out.println("Finished DB update");
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// pause
	}
}
