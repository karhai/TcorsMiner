package edu.usc.tcors.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import edu.usc.tcors.TcorsTwitterStream;

/*
 * Simple functions for generic tools used by either Twitter or Instagram
 * 
 * Specifically, grabbing database information
 */

public class TcorsMinerUtils {

	static String fileName = "keywords.txt";
	
	public static String[] loadSearchTerms() throws IOException {
		ArrayList<String> ret = new ArrayList<String>();
		
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		BufferedReader file = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = file.readLine()) != null) {
			if (!line.startsWith("#")) {
				ret.add(line);
			}
		}
		file.close();
		
		return ret.toArray(new String[ret.size()]);
	}
	
	/*
	 * Configuration utilities
	 */
	
	public static Properties getProps(String filename) {
		Properties prop = new Properties();
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream in = loader.getResourceAsStream(filename);
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		return prop;
	}
	
	public static Connection getDBConn(String filename) throws SQLException {
		System.out.println("Creating a DB connection...");
		Connection conn = null;
		Properties prop = getProps(filename);

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = prop.getProperty("url");
		String user = prop.getProperty("user");
		String password = prop.getProperty("password");
			
		conn = DriverManager.getConnection(url,user,password);
		return conn;
	}
}
