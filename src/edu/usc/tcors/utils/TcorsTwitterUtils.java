package edu.usc.tcors.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import edu.usc.tcors.TcorsTwitterStream;

public class TcorsTwitterUtils {

	static String fileName = "keywords.txt";
	
	public static void main(String[] args) {
		
		TcorsTwitterUtils u = new TcorsTwitterUtils();
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn  = null;
		try {
			conn = tmu.getDBConn("configuration.properties");
			
			// maxId = 609485130428743680L
			// u.search("cigarettes", 623798359074050048L, 623892873835085824L, conn);
			
			// u.getTweetsByID(631411025993035776L, 631549675347152896L, conn);
			
			u.getUserHistoricalTweets(2231009702L, conn);
			
//			// get follower IDs
//			IDs followers = null;
//			followers = u.getFollowers("vapingmilitia",-1,conn);
//			// get follower profiles
//			long[] id_array = followers.getIDs();
//			ResponseList<User> users = u.getProfiles(id_array);
//			// store profiles in DB
//			u.storeUserDataFromList(conn, users);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void getTweetsByID(long min_id, long max_id, Connection conn) throws SQLException {
		
		// open keywords.txt
		String keywords[] = null;
		try {
			keywords = TcorsMinerUtils.loadSearchTerms();
		} catch (IOException e1) {
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
	
	private void getUserHistoricalTweets(long id, Connection conn) throws SQLException {
		Twitter twitter = getInstance();
		Paging paging = new Paging(1,200);
		paging.setCount(1000);
		paging.setMaxId(412286821792751616L);
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
	
	
	private IDs getFollowers(String id, long cursor, Connection conn) {
		Twitter twitter = getInstance();
		IDs followers = null;
		try {
			followers = twitter.getFollowersIDs(id, cursor);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		System.out.println("Followers found for " + id + ":" + followers.getIDs().length);
		
		return followers;
	}
	
	private ResponseList<User> getProfiles(long[] ids) {
		ResponseList<User> profiles = null;
		
		// loop 100 at a time
//		try {
//			profiles = getInstance().lookupUsers(ids);
//		} catch (TwitterException e) {
//			e.printStackTrace();
//		}
		
		/*
		 * This is some strange code. It can definitely be improved.
		 */
		
		int quotient = 0;
		quotient = ids.length/100;
		
		int counter = 0;
		boolean first = true;
		while (counter < quotient+1) {
			long[] tmp_ids = new long[100];
			
			for (int x = 0; x < 100; x++) {
				if (counter*100 + x < ids.length) {
					tmp_ids[x] = ids[counter*100 + x];
				}
			}
			
			// call get profiles on the tmp array
			System.out.println("Loaded tmp_ids with:" + tmp_ids.length);
			try {
				if (first) {
					profiles = getInstance().lookupUsers(tmp_ids);
					first = false;
				} else {
					profiles.addAll(getInstance().lookupUsers(tmp_ids));
				}
				
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			counter++;
		}
		
		System.out.println("User profiles found: " + profiles.size());
		
		return profiles;
	}
	
	/*
	 * Might be refactorable with storeUserData
	 */
	
	private void storeUserDataFromList(Connection conn, ResponseList<User> users) throws SQLException {
		String sql = "REPLACE INTO twitter_profiles(userId, description, friendsCount, followersCount, screenName, statusesCount, location, name)" +
				"VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null; 
		
		System.out.println("Preparing to update DB...");
		try {
			ps = conn.prepareStatement(sql);
			
			for (User u : users) {
			
				ps.setString(1, String.valueOf(u.getId()));
				ps.setString(2, u.getDescription());
				ps.setInt(3, u.getFriendsCount());
				ps.setInt(4, u.getFollowersCount());
				ps.setString(5, u.getScreenName());
				ps.setInt(6, u.getStatusesCount());
				ps.setString(7, u.getLocation());
				ps.setString(8, u.getName());
				
				ps.addBatch();
			}
			
			ps.executeBatch();
			System.out.println("DB updated!");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}
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
			e.printStackTrace();
		} finally {
			ps.close();
		}
	}
	
	private void storeTweetData(Connection conn, Status status) throws SQLException {
		String sql = "INSERT IGNORE INTO tweets (id, createdAt, text, userId, isRetweet, retweets)" +
				"VALUES (?,?,?,?,?,?)";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Long.toString(status.getId()));
			ps.setTimestamp(2, new Timestamp(status.getCreatedAt().getTime()));
			ps.setString(3, status.getText());
			ps.setString(4, String.valueOf(status.getUser().getId()));
			ps.setBoolean(5, status.isRetweet());
			ps.setInt(6, status.getRetweetCount());
			
			ps.execute();
				
			System.out.println("Stored tweet " + status.getId());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}
	}
}
