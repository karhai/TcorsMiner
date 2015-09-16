package edu.usc.tcors.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.exceptions.InstagramException;

import edu.usc.tcors.TcorsTwitterStream;

/*
 * command line execution: java -cp TcorsMiner.jar edu.usc.tcors.utils.TcorsInstagramUtils [users/images] [number of 1000 images] 
 */

public class TcorsInstagramUtils {

	final static String get_file_URLs = "SELECT id, url " +
			"FROM instagram " + 
			"WHERE storePicture = 0 " + 
			"LIMIT 1000";
	
	final static String update_file_URLs = "UPDATE instagram " +
			"SET storePicture = 1 " +
			"WHERE id = ?";
	
	final static String update_bad_URLs = "UPDATE instagram " +
			"SET storePicture = -1 " +
			"WHERE id = ?";
	
	// TODO is there a better check for profiles that need updates?
	
	final static String get_user_IDs = "SELECT id " +
			"FROM instagram_users " +
			"WHERE follows IS NULL " +
			"AND followedBy IS NULL " +
			"AND bio IS NULL " +
			"LIMIT 1000";
	
	final static String update_user_info = "UPDATE instagram_users " +
			"SET bio = ?, follows = ?, followedBy = ? " +
			"WHERE id = ?";
	
	// TODO get a generic version
	// final static String destination_directory = "/Users/karhai/tmp/instagram_pix/";
	final static String destination_directory = "c:\\Users\\tcorstwitter\\Documents\\instagram_images\\";
	
	public static void main(String[] args) throws Exception {
		
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = tmu.getDBConn("configuration.properties");
		
		if (args.length > 0) {
			
			/*
			 * user bios
			 */
			
			if (args[0].equals("users")) {
				Token secretToken = getSecretToken();
				Instagram instagram = new Instagram(secretToken);
				updateUsers(conn, instagram);
			}
			
			/*
			 * images
			 */
			
			if (args[0].equals("images")) {
				int loops = 1;
				if (args[1] != null) loops = Integer.parseInt(args[1]);
				for (int x = 0; x < loops; x++) {
					getImages(conn);
				}
			}
		}
			
			System.out.println("Pau!");
		
	}

	// general properties
	
	private static Properties getProps() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("jInstagram.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		return prop;
	}
	
	private static Token getSecretToken() {
		Properties iProp = getProps();
		Token secretToken = new Token(iProp.getProperty("oauth.accessToken"),null);
		return secretToken;
	}
	
	// get images
	
	private static String parseFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	private static void getImages(Connection conn) {
		String destinationFile = "";
		HashMap<String,String> id_urls = new HashMap<String,String>();
		id_urls = getImageURLs(conn);
		
//		for(Map.Entry<String,String> entry : id_urls.entrySet()) {
//			System.out.println("id:" + entry.getKey() + " url:" + entry.getValue());
//		}
		
		System.out.println("Getting images...");
		int counter = 0;
		List<String> bad_urls = new ArrayList<String>();
		
		for(Map.Entry<String,String> entry : id_urls.entrySet()) {
			String key = entry.getKey();
			String fileURL = entry.getValue();
			if (!fileURL.isEmpty()) {
				String fileName = parseFileName(fileURL);
				destinationFile = destination_directory + fileName;
				try {
					saveImage(fileURL, destinationFile);
				} catch (FileNotFoundException f) {
					bad_urls.add(key);
					System.out.println("Could not find file:" + fileURL);
					// f.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				bad_urls.add(key);
			}
			if(++counter % 100 == 0) System.out.println("Count:" + counter);
		}
		
		for(String id : bad_urls) {
			id_urls.remove(id);
		}
		
		updateBadURLs(conn, bad_urls);
		updateStorePicture(conn, id_urls);
	}
	
	private static HashMap<String,String> getImageURLs(Connection conn) {
		
		System.out.println("Getting image URLs...");
		HashMap<String,String> id_urls = new HashMap<String,String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(get_file_URLs);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				id_urls.put(rs.getString("id"), rs.getString("url"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { };
		}
		
		System.out.println("Found:" + id_urls.size());
		return id_urls;
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
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);
		
		byte[] b = new byte[2048];
		int length;
		
		while((length = is.read(b)) != -1) {
			os.write(b,0,length);
		}
		
		is.close();
		os.close();
	}
	
	// TODO update profile bios
	
	private static void updateUsers(Connection conn, Instagram inst) {
		List<String> user_ids = new ArrayList<String>();
		List<UserInfoData> user_info_list = new ArrayList<UserInfoData>();
		user_ids = getUserIds(conn);
		user_info_list = getUserInfo(inst, user_ids);
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
	
	private static List<UserInfoData> getUserInfo(Instagram inst, List<String> user_ids) {
		
		System.out.println("Getting user info...");
		UserInfoData userData = null;
		List<UserInfoData> user_info_list = new ArrayList<UserInfoData>();
		
		for (String id : user_ids) {
			try {
				UserInfo userInfo = inst.getUserInfo(id);
				userData = userInfo.getData();
				user_info_list.add(userData);
			} catch (InstagramException i) {
				i.printStackTrace();
			}
		}
		System.out.println("Found:" + user_info_list.size());
		return user_info_list;
	}
	
	private static void updateUser(List<UserInfoData> user_data, Connection conn) {
		
		System.out.println("Updating user...");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(update_user_info);
			for (UserInfoData user : user_data) {
				String id = user.getId();
				String bio = user.getBio();
				int followedBy = user.getCounts().getFollwed_by();
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
			
		} finally {
			if (ps != null) try { ps.close(); } catch (SQLException s) { }
		}	
	}
}