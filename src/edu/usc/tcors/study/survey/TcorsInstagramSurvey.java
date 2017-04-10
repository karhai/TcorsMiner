package edu.usc.tcors.study.survey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramBadRequestException;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.exceptions.InstagramRateLimitException;

import edu.usc.tcors.utils.TcorsInstagramUtils;
import edu.usc.tcors.utils.TcorsMinerUtils;

public class TcorsInstagramSurvey {
	
	final static String get_posts = "SELECT id " +
			"FROM s3_instagram_posts " + 
			"WHERE comments IS NULL " +
			"LIMIT 10 ";
	
	final static String store_comments = "INSERT IGNORE INTO s3_instagram_comments (id,parent_id,username,comment) " +
			"VALUES (?,?,?,?)";
	
	final static String update_posts = "UPDATE s3_instagram_posts " +
			"SET comments = ? " +
			"WHERE id = ? ";
	
	public static void main(String[] args) throws Exception {
		
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = tmu.getDBConn("configuration.properties");
		
		Token secretToken = TcorsInstagramUtils.getSecretToken();
		Instagram instagram = new Instagram(secretToken);
		
		// 
//		for (int x = 0; x < 100; x++) {
//			runCommentUpdates(conn, instagram);
//			System.out.println("Loop " + x + ": Pause for 15 minutes");
//			System.out.println(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
//			Thread.sleep(15 * 60 * 1000);
//		}
		
		String tagName = "snow";
		TagMediaFeed mediaFeed = instagram.getRecentMediaTags(tagName);

		List<MediaFeedData> mediaFeeds = mediaFeed.getData();
	}
	
	private static void runCommentUpdates(Connection conn, Instagram instagram) {
		HashMap<String,Integer> ids = new HashMap<String,Integer>();
		
		ids = getInstagramPosts(conn);
		
		HashMap<String,List<CommentData>> comment_data = null;
		comment_data = getComments(ids, instagram);
		
		updatePosts(conn, ids);
		
		if (comment_data != null) {
			storeComments(conn, comment_data);
		}
	}
	
	// get the IDs of the Instagram posts
	public static HashMap<String,Integer> getInstagramPosts(Connection conn) {
		
		HashMap<String,Integer> ids = new HashMap<String,Integer>();
		
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(get_posts);
			
			while(rs.next()) {
				String id = rs.getString("id");
				ids.put(id, 0);
			}
			
			st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ids;
	}
	
	// for each ID, get comments
	public static HashMap<String,List<CommentData>> getComments(HashMap<String,Integer> ids, Instagram instagram) {
		
		List<CommentData> comments = null;
		HashMap<String,List<CommentData>> comment_data = new HashMap<String,List<CommentData>>();
		
		for (String id : ids.keySet()) {
			
			int size = 0;
			try {
				MediaCommentsFeed feed = instagram.getMediaComments(id);
				
				comments = feed.getCommentDataList();
				size = comments.size();
				
				System.out.println("id:" + id + " with size:" + size);
				ids.put(id, size);
				comment_data.put(id, comments);
			} catch (InstagramRateLimitException r) {
				r.printStackTrace();
				TcorsTwitterSurvey.delay(0, 3600); // wait an hour
			}  catch (InstagramException e) {
				System.out.println("id:" + id + " NOT FOUND");
				// e.printStackTrace();
			}
		}
		// update posts
		return comment_data;
	}
	
	// update posts in the DB
	public static void updatePosts(Connection conn, HashMap<String,Integer> ids) {
		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_posts);
			
			for (Map.Entry<String, Integer> entry : ids.entrySet()) {
				String id = entry.getKey();
				Integer status = entry.getValue();
				
				ps.setInt(1, status);
				ps.setString(2, id);
				
				ps.addBatch();
			}
			
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
	}
	
	// store comments in the DB
	public static void storeComments(Connection conn, HashMap<String,List<CommentData>> commentdata) {
		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(store_comments);
		
			for (Map.Entry<String, List<CommentData>> entry : commentdata.entrySet()) {
				String id = entry.getKey();
				List<CommentData> comments = entry.getValue();
	
				for (CommentData comment : comments) {
					String comment_id = comment.getId();
					String comment_username = comment.getCommentFrom().getUsername();
					String comment_text = comment.getText();
				
					ps.setString(1, comment_id);
					ps.setString(2, id);
					ps.setString(3, comment_username);
					ps.setString(4, comment_text);
					
					ps.addBatch();
				}
			}
			
			ps.executeBatch();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
	}
}
