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

public class TcorsInstagramUtils {

	final static String get_file_URLs = "SELECT id, url " +
			"FROM instagram " + 
			"WHERE storePicture = 0 " + 
			"LIMIT 1000";
	
	final static String update_file_URLs = "UPDATE instagram " +
			"SET storePicture = 1 " +
			"WHERE id = ?";
	
	// TODO need a better destintation folder for files storage
	
	public static void main(String[] args) throws Exception {
		String destinationFile = "";
		
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = tmu.getDBConn("configuration.properties");
		
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
				destinationFile = "/Users/karhai/tmp/instagram_pix/" + fileName;
				try {
					saveImage(fileURL, destinationFile);
				} catch (FileNotFoundException f) {
					bad_urls.add(key);
					f.printStackTrace();
				}
			} else {
				bad_urls.add(key);
			}
			if(++counter % 100 == 0) System.out.println("Count:" + counter);
		}
		
		// TODO add function to delete URL of files not found
		
		updateStorePicture(conn, id_urls);
		System.out.println("Pau!");
	}
	
	private static String parseFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	private static HashMap<String,String> getImageURLs(Connection conn) throws SQLException {
		
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
			ps.close();
		}
		
		System.out.println("Found:" + id_urls.size());
		return id_urls;
	}
	
	private static void updateStorePicture(Connection conn, HashMap<String,String> id_urls) throws SQLException {
		
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
			ps.close();
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
}