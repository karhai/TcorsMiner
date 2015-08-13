package edu.usc.tcors.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import edu.usc.tcors.TcorsTwitterStream;

public class TcorsTwitterUtils {

	static String fileName = "keywords.txt";
	
	public static void main(String[] args) {
		
		TcorsTwitterUtils u = new TcorsTwitterUtils();
		Connection conn  = null;
		try {
			conn = u.getDBConn("configuration.properties");
			
			// maxId = 609485130428743680L
			// u.search("cigarettes", 623798359074050048L, 623892873835085824L, conn);
			
			u.getTweetsByID(631411025993035776L, 631549675347152896L, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void getTweetsByID(long min_id, long max_id, Connection conn) throws SQLException {
		
		// open keywords.txt
		String keywords[] = null;
		try {
			keywords = loadSearchTerms();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// for each word
		long check = 0L;
		for (String word : keywords) {
		
			// u.search word with min and max
			System.out.println("SEARCH FOR:" + word);
			check = search(word, min_id, max_id, conn);
			
			// if number is returned, wait 15 minutes, then run search with new maxId
			while (check > 0L) {
				// wait 15 minutes
				System.out.println("Pausing for effect...");
				try {
					Thread.sleep(900000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// run search with same word, new maxId
				check = search(word, min_id, check, conn);
			}
		}
	}

	/*
	 * TODO: refactor
	 */
	
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
	
	private void getUserHistoricalTweets(long id, Connection conn) throws SQLException {
		Twitter twitter = getInstance();
		Paging paging = new Paging(1,200);
		paging.setCount(1000);
		// paging.setMaxId(587170184189710336L);
		// paging.setSinceId(587170184189710336L);
		
		List<Status> statuses = null;
		try {
			statuses = twitter.getUserTimeline(id, paging);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		for (Status status : statuses) {
			storeTweetData(conn, status);
		}
		
	}
	
	private long search(String searchTerm, long sinceId, long maxId, final Connection conn) throws SQLException {
		// Map<String,Integer> terms = new HashMap<String,Integer>();
		Twitter twitter = getInstance();
		Query query = new Query(searchTerm);
		
		if (sinceId > 0) query.setSinceId(sinceId);
		if (maxId > 0) query.setMaxId(maxId);
		
		query.setCount(1000);
		
		QueryResult result = null;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		long newMaxId = 0L;
		for (Status status : result.getTweets()) {
			// System.out.println(++i + " @" + status.getUser().getScreenName() + " " + status.getCreatedAt());
			
			storeUserData(conn, status);
			storeTweetData(conn, status);
			
			newMaxId = status.getId();
			i++;
		}
		System.out.println("i total:" + i);
		System.out.println("term:" + searchTerm);
		
		/*
		 * TODO: need to handle the rare case of exactly 100 remaining 
		 */
		
		long check = 0L;
		int remaining = 2; // assume greater than 1.. if less, it will fail anyway ;)
		try {
			remaining = result.getRateLimitStatus().getRemaining();
		} catch (NullPointerException n) {
			// do nothing
		}
		
		/*
		 * TODO: logic needs to be fixed. problem if a word is completed (<100) and breaks the 2-to-1 limit
		 */
		
		if (i == 100 && remaining > 1) {
			System.out.println("more needed");
			check = search(searchTerm, sinceId, newMaxId, conn);
		} else {
			if (i == 100 && result.getRateLimitStatus().getRemaining() == 1) {
				check = newMaxId;
			} else {
				if (i < 100) {
					check = 0;
				}
			}
		}

		return check;
	}
	
	private Twitter getInstance() {
		ConfigurationBuilder cb = getConf();
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		return twitter;
	}
	
	private ConfigurationBuilder getConf() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("twitter4j.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		ConfigurationBuilder twitterConf = new ConfigurationBuilder();
		twitterConf.setIncludeEntitiesEnabled(true);
		twitterConf.setDebugEnabled(Boolean.valueOf(prop.getProperty("debug")));
		twitterConf.setOAuthAccessToken(prop.getProperty("oauth.accessToken"));
		twitterConf.setOAuthAccessTokenSecret(prop.getProperty("oauth.accessTokenSecret"));
		twitterConf.setOAuthConsumerKey(prop.getProperty("oauth.consumerKey"));
		twitterConf.setOAuthConsumerSecret(prop.getProperty("oauth.consumerSecret"));
		twitterConf.setJSONStoreEnabled(true);
		
		return twitterConf;
	}
	
	/*
	 * Utilities for Gnip data integration
	 */
	
	private void storeUserData(Connection conn, Status status) throws SQLException {
		String sql = "REPLACE INTO twitter_profiles(userId, description, friendsCount, followersCount, screenName, statusesCount, location, name)" +
				"VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null; 
		try {
			ps = conn.prepareStatement(sql);
			
			User u = status.getUser();
			ps.setString(1, String.valueOf(u.getId()));
			ps.setString(2, u.getDescription());
			ps.setInt(3, u.getFriendsCount());
			ps.setInt(4, u.getFollowersCount());
			ps.setString(5, u.getScreenName());
			ps.setInt(6, u.getStatusesCount());
			ps.setString(7, u.getLocation());
			ps.setString(8, u.getName());
			
			ps.execute();
			System.out.println("Stored profile " + u.getScreenName());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ps.close();
		}
	}
	
	private void storeTweetData(Connection conn, Status status) throws SQLException {
		String sql = "INSERT IGNORE INTO tweets (id, createdAt, text, userId, isRetweet)" +
				"VALUES (?,?,?,?,?)";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Long.toString(status.getId()));
			ps.setTimestamp(2, new Timestamp(status.getCreatedAt().getTime()));
			ps.setString(3, status.getText());
			ps.setString(4, String.valueOf(status.getUser().getId()));
			ps.setBoolean(5, status.isRetweet());
			
			ps.execute();
				
			System.out.println("Stored tweet " + status.getId());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ps.close();
		}
	}
	
	/*
	 * Configuration utilities
	 */
	
	private Properties getDBConf(String filename) {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream(filename);
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		return prop;
	}
	
	public Connection getDBConn(String filename) throws SQLException {
		System.out.println("Creating a DB connection...");
		Connection conn = null;
		Properties prop = getDBConf(filename);

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
