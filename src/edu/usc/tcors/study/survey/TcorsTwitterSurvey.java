package edu.usc.tcors.study.survey;

import java.net.UnknownHostException;
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
import java.util.Random;

import edu.usc.tcors.utils.TcorsMinerUtils;
import edu.usc.tcors.utils.TcorsTwitterUtils;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TcorsTwitterSurvey {

	private HashMap<String,Integer> users = new HashMap<String,Integer>();
	
	private static Connection conn = null;
	
	private final static long account_id = 734880763523407873L;
	
	private static String sn = "";
	
	private static String link = "";
	
	private final static String dm1 = "Hi ";
	private final static String dm2 = ",\n\n" +
			"We’re researchers from the University of Southern California, doing a survey about tobacco and social media. Your input would be very helpful (even if you are not a smoker), and you will get an Amazon gift card for your time!\n\n" +
			"If you live in the United States and are willing to help, click here to get started: ";
	private final static String dm3 = "\n\n" +
			"To learn more about us:\n" +
			"https://tcors.usc.edu\n" +
			"(323) 442-8211\n" +
			"tobaccostudy@tcors.net\n\n" +
			"Thanks so much!\n" +
			"USC";
	
	final static String getUserData = "SELECT screenName, url " +
			"FROM s3_survey " +
			"WHERE userId = ? ";
	
	final static String getInitialDmUsers = "SELECT userId " +
			"FROM s3_survey " +
			"WHERE (initialDM = 0 OR initialDM = -226) " +
			"AND (type = \"follower\" OR type = \"OL\")" +
			"LIMIT 20 ";
	
	final static String updateInitialDmUsers = "UPDATE s3_survey " +
			"SET initialDM = ? " +
			"WHERE userId = ? ";

	final static String getFollowRqUsers = "SELECT userId " +
			"FROM s3_survey " +
			"WHERE initialDM = -1 " +
			"AND (followRQ = 0 OR followRQ = -1) " +
			"LIMIT 10 ";
	
	final static String updateFollowRqUsers = "UPDATE s3_survey " +
			"SET followRQ = ? " + 
			"WHERE userId = ? ";
	
	final static String getFriendRqUsers = "SELECT userId, friendRQ " + 
			"FROM s3_survey " +
			"WHERE followRQ = 1 " +
			"AND friendRQ < 1 " +
			"ORDER BY friendRQ DESC " +
			"LIMIT 100 ";
	
	final static String updateFriendRqUsers = "UPDATE s3_survey " +
			"SET friendRQ = ? " +
			"WHERE userId = ? ";
	
	final static String getFinalDmUsers = "SELECT userId " +
			"FROM s3_survey " + 
			"WHERE friendRQ = 1 " +
			"AND finalDM = 0 " +
			"LIMIT 10 ";
	
	final static String updateFinalDmUsers = "UPDATE s3_survey " +
			"SET finalDM = ? " +
			"WHERE userId = ? ";
	
	final static String get_not_follower_ids = "SELECT userId " +
			"FROM s3_survey " +
			"WHERE followrq = 1 " +
			"AND friendrq < 0 " +
			"ORDER BY friendrq ASC " +
			"LIMIT 10 ";
	
	public static void main(String[] args) {
		setConnection();

		TcorsTwitterUtils u = new TcorsTwitterUtils();
		Twitter t = u.getInstance();
		
		int loops = 0;
		if (args.length > 0) {
			loops = Integer.parseInt(args[0]);
		}
		
		/*
		 * TODO: add remove friends
		 */
		
		for (int x = 0; x < loops; x++) {
			
			System.out.println("Loop:" + x);
			
			// sending a message
			sendInitialDMs(t);
			
			// following someone
			// followUsers(t);
			
			// friend RQs
			// checkFriendRQs(t);
			
			// send final DM
			// sendFinalDMs(t);
			
			// testDM(t);
			
			try {
				System.out.println("Limits:" + t.getRateLimitStatus());
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			
			System.out.println(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
		}
		
		// close connection
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void setConnection() {
		try {
			conn = TcorsMinerUtils.getDBConn("configuration.properties");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param t
	 */
	private static void sendInitialDMs(Twitter t) {
		
		// get list of users from DB
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		users = getUsers(getInitialDmUsers,"");
		int wait_time = 50;
		
		for(String user : users.keySet()) {
		
			// check if can send DM
			Relationship r = null;
			// System.out.println("Checking relationship with:" + user);
			
			r = getRelationship(t,user);
			
			if (r == null) {
				users.put(user, -50);
			} else {
			
				if(r.canSourceDm()) {
					// if able to, send message and update as success
					
					String[] user_data = new String[2];
					System.out.println("looking for:" + user);
					user_data = getUserData(getUserData,user);
					
					sn = user_data[0];
					link = user_data[1];
					
					String direct_message = getMessage(sn, link);
					
					int result = 0;
					result = sendMsg(t,direct_message,user);
					users.put(user, result);
					
					wait_time = delay(result, wait_time);
					
				} else {
					// if not, update local status of denial
					System.out.println(user + " is locked");
					users.put(user, -1);
					
					delay(0, 10);
				}
			}
		}
		
		// update DB
		updateUsers(users, updateInitialDmUsers);
	}
	
	
	
	/**
	 * @param result
	 * @param wait_time: seconds to wait (with randomization added later)
	 * @return
	 */
	public static int delay(int result, int wait_time) {
		
		int new_wait = wait_time;
		
		if (result == -226) {
			new_wait = wait_time * 2;
			if (new_wait > 3600) {
				new_wait = 3600;
			}
		} else {
			if (wait_time > 50) {
				new_wait = wait_time - 10;
			}
		}
		
		try {
			Random rand = new Random();
			int randNum = rand.nextInt(11) + new_wait;
			System.out.println("Waiting " + randNum + " seconds...");
			Thread.sleep(randNum * 1000); // 1000 = 1 sec
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		
		return new_wait;
	}
	
	private static String getMessage(String sn, String link) {
		return dm1 + sn + dm2 + link + dm3;
	}
	
	/**
	 * @param conn
	 * @return
	 */
	private static HashMap<String,Integer> getUsers(String sql, String var) {
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
		
			while (rs.next()) {
				String userId = rs.getString("userId");
				if (var != "") {
					Integer param = rs.getInt(var);
					users.put(userId, param);
				} else {
					users.put(userId, 0);
				}
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (st != null) { 
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return users;
	}
	
	/**
	 * @param conn
	 * @return
	 */
	private static String[] getUserData(String sql, String userId) {
		String[] user_data = new String[2];
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(sql);
			st.setString(1, userId);
			
			rs = st.executeQuery();
		
			if (rs.next()) {
				String screen_name = rs.getString("screenName");
				String url = rs.getString("url");
				user_data[0] = screen_name;
				user_data[1] = url;
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (st != null) { 
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return user_data;
	}
	
	/**
	 * @param users
	 */
	private static void updateUsers(HashMap<String,Integer> users, String sql) {
		PreparedStatement ps = null;
		int size = users.size();
		System.out.println("Updating users:" + size);
		
		if(size > 0) {
			try {
				ps = conn.prepareStatement(sql);
				for (String user : users.keySet()) {
					ps.setInt(1, users.get(user));
					ps.setString(2, user);
					ps.addBatch();
				}
				
				ps.executeBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("No updates to be made");
		}
	}
	
	/**
	 * @param t
	 * @param msg
	 * @param user
	 */
	private static int sendMsg(Twitter t, String msg, String user) {
		int ret = 0;
		try {
			DirectMessage dm = t.sendDirectMessage(Long.parseLong(user), msg);
			System.out.println("Sent DM to:" + dm.getRecipientScreenName());
			ret = 1;
		} catch (TwitterException e) {
			e.printStackTrace();
			ret = -100;
			
			if(e.getErrorCode() == 150) {
				System.out.println("Blocked DM");
				ret = -150;
			} else {
				if(e.getErrorCode() == 226) {
					System.out.println("Treated as spam");
					ret = -226;
				} else {
					System.out.println("Failed message:" + e.getMessage());
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param t
	 */
	private static void followUsers(Twitter t) {
		
		// get list of users from DB
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		users = getUsers(getFollowRqUsers,"");
		
		// for each user
		for(String user : users.keySet()) {
			// follow user
			int success = followUser(t, user);
			
			System.out.println("Making friends with:" + user);
			
			try {
				Random rand = new Random();
				int randNum = rand.nextInt(11) + 10;
				System.out.println("Waiting " + randNum + " seconds...");
				Thread.sleep(randNum * 1000); // 1000 = 1 sec
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			
			users.put(user, success);
		}
		
		// update DB
		updateUsers(users, updateFollowRqUsers);
	}
	
	/**
	 * @param t
	 * @param user
	 * @return 1 if successful, -1 if failed
	 */
	private static int followUser(Twitter t, String user) {
		int success = -1;
		try {
			t.createFriendship(Long.parseLong(user));
			success = 1;
			// System.out.println("rate status:" + t.getRateLimitStatus());
		} catch (TwitterException e) {
			if (e.getErrorCode() == 108) {
				System.out.println("Unknown user");
				success = -108;
			} else {
				if (e.getErrorCode() == 250) {
					System.out.println("Requires age screen");
					success = -250;
				} else {
					e.printStackTrace();
				}
			}
		} 
		return success;
	}
	
	/**
	 * @param t
	 */
	private static void checkFriendRQs(Twitter t) {
		
		// get list of users from DB
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		users = getUsers(getFriendRqUsers,"friendRQ");
		
		long[] user_ids = new long[100];
		int count = 0;
		for(String user : users.keySet()) {
			user_ids[count] = Long.parseLong(user);
			count++;
		}
		
		ResponseList<Friendship> friendships = getFriendships(t, user_ids);
		
		int new_friends = 0;
		HashMap<String,Integer> users_update = new HashMap<String,Integer>();
		for(Friendship f : friendships) {
			String userId = Long.toString(f.getId());
			if(f.isFollowedBy()) {
				users_update.put(userId, 1);
				new_friends++;
			} else {
				Integer i = users.get(userId);
				users_update.put(userId, --i);
			}
		}
		
		// update DB
		System.out.println("Adding new friends:" + new_friends);
		updateUsers(users_update, updateFriendRqUsers);
	}
	
	/**
	 * @param t
	 * @param user
	 * @return
	 */
	private static Relationship getRelationship(Twitter t, String user) {
		Relationship r = null;
		try {
			r = t.showFriendship(account_id, Long.parseLong(user));
			// System.out.println("Rate status:" + t.getRateLimitStatus());
			// System.out.println("Found r:" + r.toString());
		} catch (TwitterException e) {
			switch(e.getErrorCode()) {
				case 50: 
					System.out.println("User not found");
					break;
				default:
					e.printStackTrace(); 
			}
		} catch (NullPointerException n) {
			System.out.println("User not found:" + user);
			n.printStackTrace();
		}
		return r;
	}

	/**
	 * @param t
	 * @param user_ids
	 * @return
	 */
	private static ResponseList<Friendship> getFriendships(Twitter t, long[] user_ids) {
		ResponseList<Friendship> friendships = null;
		try {
			friendships = t.lookupFriendships(user_ids);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return friendships;
	}

	/**
	 * @param t
	 */
	private static void sendFinalDMs(Twitter t) {
		
		// get list of users from DB
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		users = getUsers(getFinalDmUsers,"");
		int wait_time = 50;
		
		for(String user : users.keySet()) {

			String[] user_data = new String[2];
			System.out.println("looking for:" + user);
			user_data = getUserData(getUserData,user);
			
			sn = user_data[0];
			link = user_data[1];
			
			String direct_message = getMessage(sn, link);
			
			int res = 0;
			res = sendMsg(t,direct_message,user);
			users.put(user, res);
			
			wait_time = delay(res, wait_time);
		}
		
		// update DB
		updateUsers(users, updateFinalDmUsers);
	}
	
	private static void testDM(Twitter t) {
		List<DirectMessage> messages = null;
		
		Paging paging = new Paging(1);
		int counter = 0;
		
		try {
			do {
				messages = t.getSentDirectMessages(paging);
				// messages = t.getDirectMessages();
				for (DirectMessage dm : messages) {
					System.out.println("To:" + dm.getRecipientId());
					counter++;
				}
				paging.setPage(paging.getPage()+1);
			} while (messages.size() > 0 && paging.getPage() < 10);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		System.out.println("Total messages:" + counter);
	}
	
	private static void removeNoFollowBack(Connection conn, Twitter t) {
				
		HashMap<String,Integer> ids = new HashMap<String,Integer>();
		// get list of names from database
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(get_not_follower_ids);
			
			while(rs.next()) {
				String id = rs.getString("id");
				ids.put(id, 0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// remove followers
		for (String id : ids.keySet()) {
			try {
				t.destroyFriendship(id);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void removeRandomFollowers(Connection conn, Twitter t) {
		try {
			// get list of followers
			IDs follower_ids = t.getFollowersIDs(account_id);

			// get list of friends
			IDs friend_ids = t.getFriendsIDs(account_id);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
