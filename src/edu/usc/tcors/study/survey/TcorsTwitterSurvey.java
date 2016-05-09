package edu.usc.tcors.study.survey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.usc.tcors.utils.TcorsMinerUtils;
import edu.usc.tcors.utils.TcorsTwitterUtils;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TcorsTwitterSurvey {

	private HashMap<String,Integer> users = new HashMap<String,Integer>();
	
	private static Connection conn = null;
	
	private final static long account_id = 4750520330L;
	
	private static String sn = "";
	
	private static String link = "";
	
	private final static String dm1 = "Hi ";
	private final static String dm2 = ",\n\n" +
			"We’re researchers from the University of Southern California, doing a survey about tobacco and social media. Your input would be very helpful (even if you are not a smoker), and you will get a gift card for your time!\n\n" +
			"If you're willing to help, click here to get started: ";
	private final static String dm3 = "\n\n" +
			"To learn more about us:\n" +
			"https://tcors.usc.edu\n" +
			"(323) 442-8211\n" +
			"tobaccostudy@tcors.net\n\n" +
			"Thanks so much!\n" +
			"USC";
	
	final static String getUserData = "SELECT screenName, url " +
			"FROM twitter_survey " +
			"WHERE userId = ? ";
	
	final static String getInitialDmUsers = "SELECT userId " +
			"FROM twitter_survey " +
			"WHERE initialDM = 0 " +
			"AND type = \"OL\" " +
			"LIMIT 1 ";
	
	final static String updateInitialDmUsers = "UPDATE twitter_survey " +
			"SET initialDM = ? " +
			"WHERE userId = ? ";

	final static String getFollowRqUsers = "SELECT userId " +
			"FROM twitter_survey " +
			"WHERE initialDM = -1 " +
			"AND followRQ = 0 " +
			"LIMIT 50 ";
	
	final static String updateFollowRqUsers = "UPDATE twitter_survey " +
			"SET followRQ = ? " + 
			"WHERE userId = ? ";
	
	final static String getFriendRqUsers = "SELECT userId, friendRQ " + 
			"FROM twitter_survey " +
			"WHERE followRQ = 1 " +
			"AND friendRQ < 1 " +
			"ORDER BY friendRQ DESC " +
			"LIMIT 100 ";
	
	final static String updateFriendRqUsers = "UPDATE twitter_survey " +
			"SET friendRQ = ? " +
			"WHERE userId = ? ";
	
	final static String getFinalDmUsers = "SELECT userId " +
			"FROM twitter_survey " + 
			"WHERE friendRQ = 1 " +
			"AND finalDM = 0 " +
			"LIMIT 170 ";
	
	final static String updateFinalDmUsers = "UPDATE twitter_survey " +
			"SET finalDM = ? " +
			"WHERE userId = ? ";
	
	public static void main(String[] args) {
		setConnection();

		TcorsTwitterUtils u = new TcorsTwitterUtils();
		Twitter t = u.getInstance();
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		for(String user : users.keySet()) {
		
			// check if can send DM
			Relationship r = null;
			// System.out.println("Checking relationship with:" + user);
			r = getRelationship(t,user);
			
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
				
				try {
					Random rand = new Random();
					int randNum = rand.nextInt(11) + 20;
					System.out.println("Waiting " + randNum + " seconds...");
					Thread.sleep(randNum * 1000); // 1000 = 1 sec
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				
			} else {
				// if not, update local status of denial
				// System.out.println("Can NOT dm:" + user);
				users.put(user, -1);
			}
		}
		
		// update DB
		updateUsers(users, updateInitialDmUsers);
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
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (NullPointerException n) {
			// System.out.println("NPE?");
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
		
		for(String user : users.keySet()) {

			String[] user_data = new String[2];
			System.out.println("looking for:" + user);
			user_data = getUserData(getUserData,user);
			
			sn = user_data[0];
			link = user_data[1];
			
			String direct_message = getMessage(sn, link);
			
			int res = 0;
			res = sendMsg(t,direct_message,user);
			
			try {
				Random rand = new Random();
				int randNum = rand.nextInt(11) + 10;
				System.out.println("Waiting " + randNum + " seconds...");
				Thread.sleep(randNum * 1000); // 1000 = 1 sec
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			
			users.put(user, res);
		}
		
		// update DB
		updateUsers(users, updateFinalDmUsers);
	}
	
	private static void testDM(Twitter t) {
		List<DirectMessage> messages = null;
		try {
			messages = t.getDirectMessages();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Size:" + messages.size());
		for (DirectMessage dm : messages) {
			System.out.println("From:" + dm.getSenderScreenName() + " -- " + dm.getText());
		}
	}
}
