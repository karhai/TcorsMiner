package edu.usc.tcors.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.media.MediaInfoFeed;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramBadRequestException;
import org.jinstagram.exceptions.InstagramException;

import edu.usc.tcors.TcorsTwitterStream;

/*
 * command line execution: java -cp TcorsMiner.jar edu.usc.tcors.utils.TcorsInstagramUtils [users/images] [number of 1000 images] 
 */

public class TcorsInstagramUtils {

	final static String get_image_data = "SELECT id, url , createdTime " +
			"FROM instagram " + 
			"WHERE storePicture = 0 " + 
			"LIMIT 1000";
	
	final static String update_file_URLs = "UPDATE instagram " +
			"SET storePicture = 1 " +
			"WHERE id = ?";
	
	final static String update_bad_URLs = "UPDATE instagram " +
			"SET storePicture = -1 " +
			"WHERE id = ?";
	
	final static String get_user_IDs = "SELECT id " +
			"FROM instagram_users " +
			"WHERE follows IS NULL " +
			"AND followedBy IS NULL " +
			"AND bio IS NULL " +
			"LIMIT 1000";
	
	final static String update_bad_users = "UPDATE instagram_users " +
			"SET follows = -1, followedBy = -1 " +
			"WHERE id = ?";
	
	final static String update_user_info = "UPDATE instagram_users " +
			"SET bio = ?, follows = ?, followedBy = ? " +
			"WHERE id = ?";
	
	final static String get_media_IDs_to_update = "SELECT id " +
			"FROM instagram_study_sample " + 
			"WHERE current = 0 " +
			"LIMIT 1000";
	
	final static String update_media_info = "UPDATE instagram_study_sample " + 
			"SET likes = ?, comments = ?, current = 1 "+
			"WHERE id = ?";
	
	// TODO: make this directory configurable
	final static String destination_directory = "h:\\tcorstwitter\\Documents\\instagram_images\\";
	final static String destination_directory2 = "g:\\tcorstwitter\\Documents\\instagram_images\\";
	
	public static void main(String[] args) throws Exception {
		
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = tmu.getDBConn("configuration.properties");
		
		if (args.length > 0) {
			
			/*
			 * user bios: recover user bios for new users (not included during initial data mine)
			 */
			
			if (args[0].equals("users")) {
				Token secretToken = getSecretToken();
				Instagram instagram = new Instagram(secretToken);
				updateUsers(conn, instagram);
			}
			
			/*
			 * images: download images for each post
			 */
			
			if (args[0].equals("images")) {
				int loops = 1;
				if (args[1] != null) loops = Integer.parseInt(args[1]);
				for (int x = 0; x < loops; x++) {
					getImages(conn);
				}
			}
			
			/*
			 * user bios: massive recovery of user bios
			 */
			
			if (args[0].equals("recovery")) {
				while(true) {
					Token secretToken = getSecretToken();
					Instagram instagram = new Instagram(secretToken);
					
					updateUsers(conn, instagram);
					
					try {
						System.out.println("Taking a 20 minute nap...\n\n");
						Thread.sleep(1200000); // 20 minute wait
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			/*
			 * historical: recover missing historical data based on min/max ID's
			 */
			
			if (args[0].equals("historical")) {
				getHistorical("1171523268256194717","1171893930623798416");
			}
			
			/*
			 * update: update existing Instagram posts (only for test)
			 */
			
			if (args[0].equals("media")) {
				Token secretToken = getSecretToken();
				Instagram instagram = new Instagram(secretToken);
				getMediaUpdates(conn, instagram);
			}
			
			/*
			 * 
			 */
			
			if (args[0].equals("test")) {
				Token secretToken = getSecretToken();
				Instagram instagram = new Instagram(secretToken);
				getPostsByTerm(instagram, "vape", 1458948511L);
			}
		}
			
		System.out.println("Completed!");
		
	}
	
	/*
	 * Retrieve tokens for Instagram account
	 */
	
	public static Token getSecretToken() {
		Properties iProp = TcorsMinerUtils.getProps("jInstagram.properties");
		Token secretToken = new Token(iProp.getProperty("oauth.accessToken"),null);
		return secretToken;
	}
	
	final static String instagram_sql = "REPLACE INTO instagram (id, createdTime, username, caption, likes, comments, url, location, storePicture, latitude, longitude) " +
			"VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	final static String comments_sql = "REPLACE INTO instagram_comments (id, parent_id, username, comment, createdTime) " +
			"VALUE (?, ?, ?, ?, ?)";
	
	/*
	 * TODO: consider using REPLACE, which would force repopulation of user meta-data
	 * but at the cost of spending quota, will need to be revisited based on Instagram changes
	 */
	
	final static String users_sql = "INSERT IGNORE INTO instagram_users (id, fullname, bio, username) " +
			"VALUE (?, ?, ?, ?)";
	
	/*
	 * TODO could attempt to refactor this, there is overlap with the primary scraper
	 */
	
	public static void getHistorical(String min, String max) {
		System.out.println("Getting historical data...");

		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = null;
		try {
			conn = tmu.getDBConn("configuration.properties");
		} catch (SQLException s) {
			s.printStackTrace();
		}
		
		Token secretToken = getSecretToken();
		Instagram instagram = new Instagram(secretToken);
		
//		String[] terms = {"ecigarette","ecigarettes"};
		String[] terms = null;
		try {
			terms = TcorsMinerUtils.loadSearchTerms();
		} catch (IOException i) {
			
		}
		
		List<MediaFeedData> mediaList = null;
		for (String term : terms) {
			if (!term.contains("-") && !term.contains(" ")) {
				// mediaList = getPostsByTerm(instagram, term, min, max);
				mediaList = getPostsByTerm(instagram, term, 0L);

				System.out.println("Finished term:" + term + " with updated size " + mediaList.size());
			
				System.out.println("Updating DB...");
				
				PreparedStatement instagram_ps = null;
				PreparedStatement comments_ps = null;
				PreparedStatement users_ps = null;
				
				try {
					instagram_ps = conn.prepareStatement(instagram_sql);
					comments_ps = conn.prepareStatement(comments_sql);
					users_ps = conn.prepareStatement(users_sql);
					
					for (MediaFeedData mfd : mediaList) {
						
						String id = mfd.getId();
						
						// process post, comment, and user data separately
						instagram_ps = parseInstagramPostData(instagram_ps, mfd);
						comments_ps = parseInstagramCommentData(comments_ps, mfd);
						users_ps = parseInstagramUsersData(users_ps, mfd);
						
					}
					
					// insert into database
					instagram_ps.executeBatch();
					users_ps.executeBatch();
					comments_ps.executeBatch();
					
					System.out.println("Finished DB update");
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	// TODO since the main scraper uses this function, will need an option to do limited run for recovery situations
	
	public static List<MediaFeedData> getPostsByTerm(Instagram instagram, String term, long last_time) {
		TagMediaFeed mediaFeed = null;
		List<MediaFeedData> mediaList = null;
		
		try {
			mediaFeed = instagram.getRecentMediaTags(term, "", "", 33);
			mediaList = mediaFeed.getData();
		} catch (InstagramException e) {
			e.printStackTrace();
		} catch (NullPointerException n) {
			n.printStackTrace();
		} catch (IllegalStateException i) {
			i.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * For each page, mediaFeed(0) is the latest time, down to mediaFeed(32), which is the earliest time.
		 */
		
		if (mediaList != null) {
			System.out.println("Word:" + term + " // Step 1 size:" + mediaList.size());
			// System.out.println("API Limit:" + mediaFeed.getRemainingLimitStatus());
			
			long current_time = 0L;
			Timestamp current_time_ts = new Timestamp(current_time);
			if (mediaFeed.getData().size() > 0) {
				System.out.println("mediaFeed size:" + mediaFeed.getData().size());
				current_time = Long.parseLong(mediaFeed.getData().get(mediaFeed.getData().size()-1).getCreatedTime());
				current_time_ts = new Timestamp(current_time * 1000);
				System.out.println("API Limit:" + mediaFeed.getRemainingLimitStatus() + " | current time(bot):" + current_time_ts + " | last time:" + last_time + "=" + new Timestamp(last_time * 1000));
			}

			if (mediaFeed.getPagination().hasNextPage() == false || last_time > current_time) {
				System.out.println("Done.");
			} else {
			
				// String nextMaxString = mediaFeed.getPagination().getNextMaxId();
				// System.out.println("nextMaxString:" + nextMaxString);
				// long nextMax = Long.parseLong(nextMaxString.replaceAll("[^\\d.]", ""));
				// System.out.println("nextMax: " + nextMax);
				MediaFeed recentMediaNextPage = null;
				try {
					recentMediaNextPage = instagram.getRecentMediaNextPage(mediaFeed.getPagination());
				} catch (InstagramException e) {
					e.printStackTrace();
				}
			
				// loop
	
				if (recentMediaNextPage != null) {
					
//					if (min == "") {
//						min = String.valueOf(nextMax);
//					}

					int current_size = recentMediaNextPage.getData().size();
					current_time = Long.parseLong(recentMediaNextPage.getData().get(0).getCreatedTime());

					// while (recentMediaNextPage.getPagination() != null && nextMax > Long.parseLong(min)) {	
					current_time_ts = new Timestamp(current_time * 1000);
					System.out.println("API Limit:" + recentMediaNextPage.getRemainingLimitStatus() + " | current time:" + current_time_ts + " | last time:" + last_time + "=" + new Timestamp(last_time * 1000) + " -- PAGINATION LOOP CHECK");
					while (recentMediaNextPage.getPagination() != null && current_time > last_time) {
						
						current_size = recentMediaNextPage.getData().size();
						current_time = Long.parseLong(recentMediaNextPage.getData().get(0).getCreatedTime());
						current_time_ts = new Timestamp(current_time * 1000);
						mediaList.addAll(recentMediaNextPage.getData());
						// System.out.println("mediaList size:" + mediaList.size());
						System.out.println("API Limit:" + recentMediaNextPage.getRemainingLimitStatus() + " | current time:" + current_time_ts + " | last time:" + last_time + "=" + new Timestamp(last_time * 1000));
				
						// String nextMaxString2 = recentMediaNextPage.getPagination().getNextMaxId();
						// nextMax = Long.parseLong(nextMaxString2.replaceAll("[^\\d.]", ""));
						// System.out.println("next max:" + nextMax);
						
						try {
							recentMediaNextPage = instagram.getRecentMediaNextPage(recentMediaNextPage.getPagination());
						} catch (InstagramException e) {
							e.printStackTrace();
						} catch (IllegalStateException i) {
							i.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return mediaList;
	}
	
	public static PreparedStatement parseInstagramPostData(PreparedStatement instagram_ps, MediaFeedData mfd) throws SQLException {
		
		String caption = "";
		try {
			caption = mfd.getCaption().getText();
		} catch (NullPointerException n) {
			
		}
		
		Timestamp ts = new Timestamp(Long.parseLong(mfd.getCreatedTime())*1000);
		String id = mfd.getId();
		String url = mfd.getImages().getStandardResolution().getImageUrl();
		
		// update to remove trailing ig_cache_key in the url
		String final_url = "";
		if (url.indexOf("?") != -1) {
			final_url = url.substring(0, url.indexOf("?"));
		} else {
			final_url = url;
		}
		
		int likes = mfd.getLikes().getCount();
		Location location = mfd.getLocation();
		String location_name = "";
		double lat = 0;
		double lon = 0;
		if (location != null) {
			location_name = location.getName();
			lat = location.getLatitude();
			lon = location.getLongitude();
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
		instagram_ps.setString(7, final_url);
		instagram_ps.setString(8, location_name);
		instagram_ps.setInt(9, 0);
		instagram_ps.setDouble(10, lat);
		instagram_ps.setDouble(11, lon);
		
		instagram_ps.addBatch();
		
		return instagram_ps;
	}
	
	public static PreparedStatement parseInstagramCommentData(PreparedStatement comments_ps, MediaFeedData mfd) throws SQLException {

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
	
	public static PreparedStatement parseInstagramUsersData(PreparedStatement users_ps, MediaFeedData mfd) throws SQLException {
		
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
	
	// get images
	
	private static String parseFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	public static void getImages(Connection conn) {
		String destinationFile = "";
		String parentDir = "";
		HashMap<String,String> image_data = new HashMap<String,String>();
		image_data = getImageData(conn);
		
		System.out.println("Getting images...");
		int counter = 0;
		List<String> bad_urls = new ArrayList<String>();
		
		for(Map.Entry<String,String> entry : image_data.entrySet()) {
			String key = entry.getKey();
			String data = entry.getValue();
			if (!data.isEmpty()) {
				String[] image_url_date = data.split("\t");
				String image_url = image_url_date[0];
				String image_date = image_url_date[1];
				String fileName = parseFileName(image_url);
				String parse_date[] = image_date.split(" ");
				String store_date = parse_date[0];
				
				parentDir = destination_directory + store_date + "\\";
				destinationFile = parentDir + fileName;
				
				// parentDir = destination_directory + today + "/";
				// destinationFile = parentDir + fileName;
				
				// check directory
				checkDirectory(parentDir);
				
				try {
					saveImage(image_url, destinationFile);
				} catch (FileNotFoundException f) {
					bad_urls.add(key);
					System.out.println("Could not find file:" + fileName);
					f.printStackTrace();
				} catch (SocketTimeoutException s) {
					System.out.println("SocketTimeoutException. File:" + fileName);
					// s.printStackTrace();
					bad_urls.add(key);
				} catch (IOException e) {
					System.out.println("IOException");
					e.printStackTrace();
					bad_urls.add(key);
				}
			} else {
				bad_urls.add(key);
			}
			if(++counter % 100 == 0) System.out.println("Count:" + counter);
		}
		
		for(String id : bad_urls) {
			image_data.remove(id);
		}
		
		updateBadURLs(conn, bad_urls);
		updateStorePicture(conn, image_data);
	}
	
	private static void checkDirectory(String destinationDir) {
		File file = new File(destinationDir);
		
		if (!file.exists()) {
			file.mkdir();
			System.out.println("Created directory:" + file);
		}
	}
	
	private static HashMap<String,String> getImageData(Connection conn) {
		
		System.out.println("Getting image data...");
		HashMap<String,String> image_data = new HashMap<String,String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(get_image_data);
			rs = ps.executeQuery();
			
			String data = "";
			while(rs.next()) {
				data = rs.getString("url") + "\t" + rs.getString("createdTime");
				image_data.put(rs.getString("id"), data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
		
		System.out.println("Found:" + image_data.size());
		return image_data;
	}
	
	private static void updateStorePicture(Connection conn, HashMap<String,String> id_urls) {
		
		System.out.println("Updating DB...");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_file_URLs);
			for(Map.Entry<String,String> entry : id_urls.entrySet()) {
				String id = entry.getKey();
				ps.setString(1, id);
				ps.addBatch();
			}
			
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
	}
	
	private static void updateBadURLs(Connection conn, List<String> bad_urls) {
		
		System.out.println("Updating bad URLs...");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_bad_URLs);
			for(String id : bad_urls) {
				ps.setString(1, id);
				ps.addBatch();
			}
			
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
	}
	
	private static void saveImage(String imageURL, String destinationFile) throws IOException, FileNotFoundException {
		
		URL url = new URL(imageURL);
		URLConnection urlConn = url.openConnection();
		urlConn.setConnectTimeout(5000);
		urlConn.setReadTimeout(10000);
		InputStream is = urlConn.getInputStream();
		FileOutputStream os = new FileOutputStream(destinationFile);
		
		// TODO possibly add second save directory for backup
		
		byte[] b = new byte[2048];
		int length;
		
		while((length = is.read(b)) != -1) {
			os.write(b,0,length);
		}
		
		is.close();
		os.close();
	}
	
	/*
	 * update profile bios
	 */
	
	public static void updateUsers(Connection conn, Instagram inst) {
		List<String> user_ids = new ArrayList<String>();
		List<UserInfoData> user_info_list = new ArrayList<UserInfoData>();
		user_ids = getUserIds(conn);
		user_info_list = getUserInfo(conn, inst, user_ids);
		updateUser(user_info_list, conn);
	}
	
	private static List<String> getUserIds(Connection conn) {
		System.out.println("Getting user IDs...");
		List<String> user_ids = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(get_user_IDs);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				user_ids.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException s) { };
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
		
		System.out.println("Found:" + user_ids.size());
		return user_ids;
	}
	
	private static List<UserInfoData> getUserInfo(Connection conn, Instagram inst, List<String> user_ids) {
		
		System.out.println("Getting user info...");
		UserInfoData userData = null;
		List<UserInfoData> user_info_list = new ArrayList<UserInfoData>();
		List<String> bad_users = new ArrayList<String>();
		
		for (String id : user_ids) {
			try {
				UserInfo userInfo = inst.getUserInfo(id);
				userData = userInfo.getData();
				user_info_list.add(userData);
			} catch (InstagramBadRequestException b) {
				bad_users.add(id);
				System.out.println("Bad user:" + id);
			} catch (InstagramException i) {
				i.printStackTrace();
			}
		}
		
		// update DB with bad users
		updateBadIds(conn, bad_users);
		
		System.out.println("Found:" + user_info_list.size());
		return user_info_list;
	}
	
	private static void updateBadIds(Connection conn, List<String> bad_users) {
		System.out.println("Updating bad users...");
		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_bad_users);
			for (String id : bad_users) {
				ps.setString(1, id);
				ps.addBatch();
			}
			
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { }
		}
	}
	
	private static void updateUser(List<UserInfoData> user_data, Connection conn) {
		
		System.out.println("Updating user...");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_user_info);
			for (UserInfoData user : user_data) {
				String id = user.getId();
				String bio = user.getBio();
				int followedBy = user.getCounts().getFollowedBy();
				int follows = user.getCounts().getFollows();
				// System.out.println("user:" + id + " " + bio + " " + followedBy + " " + follows);
				
				ps.setString(1, bio);
				ps.setInt(2, follows);
				ps.setInt(3, followedBy);
				ps.setString(4, id);
				
				ps.addBatch();
			}
			
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { }
		}	
	}	
	
	/*
	 * Update media info (test only)
	 */
	
	public static void getMediaUpdates(Connection conn, Instagram inst) {
		System.out.println("Updating historical Instagram data...");
		List<String> ids_to_update = new ArrayList<String>();
		List<MediaFeedData> ids_updated = new ArrayList<MediaFeedData>();
		ids_to_update = getIdsToUpdate(conn);
		ids_updated = getMediaInfo(conn, inst, ids_to_update);
		updateMedia(ids_updated, conn);
	}
	
	private static List<String> getIdsToUpdate(Connection conn) {
		System.out.println("Getting IDs...");
		List<String> ids = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(get_media_IDs_to_update);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				ids.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException s) { };
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
		
		System.out.println("Found:" + ids.size());
		return ids;
	}
	
	private static List<MediaFeedData> getMediaInfo(Connection conn, Instagram inst, List<String> ids_to_update) {
		
		System.out.println("Getting media info...");
		MediaFeedData mediaFeedData = null;
		List<MediaFeedData> media_feed_list = new ArrayList<MediaFeedData>();
		
		for (String id : ids_to_update) {
			try {
				MediaInfoFeed mediaInfoFeed = inst.getMediaInfo(id);
				mediaFeedData = mediaInfoFeed.getData();
				media_feed_list.add(mediaFeedData);
			} catch (InstagramBadRequestException b) {
				System.out.println("Bad ID:" + id);
			} catch (InstagramException i) {
				i.printStackTrace();
			}
		}
		
		System.out.println("Found:" + media_feed_list.size());
		return media_feed_list;
	}
	
	private static void updateMedia(List<MediaFeedData> updated_ids, Connection conn) {
		
		System.out.println("Updating media...");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_media_info);
			for (MediaFeedData mfd : updated_ids) {
				
				int likes = mfd.getLikes().getCount();
				int comments = mfd.getComments().getCount();
				String id = mfd.getId();
				
				ps.setInt(1, likes);
				ps.setInt(2, comments);
				ps.setString(3, id);
				
				ps.addBatch();
			}
			
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { }
		}	
	}	
}