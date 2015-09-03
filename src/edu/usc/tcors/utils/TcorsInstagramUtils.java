package edu.usc.tcors.utils;

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
import java.util.List;

public class TcorsInstagramUtils {

	public static void main(String[] args) throws Exception {
		String destinationFile = "";
		
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = tmu.getDBConn("configuration.properties");
		
		List<String> urls = new ArrayList<String>();
		urls = getImageURLs(conn);
		for(String url : urls) {
			String fileName = parseFileName(url);
			destinationFile = "/Users/karhai/tmp/instagram_pix/" + fileName;
			saveImage(url, destinationFile);
		}
	}
	
	private static String parseFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	private static List<String> getImageURLs(Connection conn) throws SQLException {
		
		List<String> urls = new ArrayList<String>();
		String query = "SELECT url " +
				"FROM instagram " +
				"WHERE storePicture = 0 " +
				"LIMIT 10";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				urls.add(rs.getString("url"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}
		
		return urls;
	}
	
	// TODO update profile bios
	
	private static void saveImage(String imageURL, String destinationFile) throws IOException {
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
}
