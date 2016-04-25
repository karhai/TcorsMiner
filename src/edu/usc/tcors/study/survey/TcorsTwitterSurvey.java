package edu.usc.tcors.study.survey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.tcors.utils.TcorsMinerUtils;
import edu.usc.tcors.utils.TcorsTwitterUtils;
import twitter4j.DirectMessage;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TcorsTwitterSurvey {

	private HashMap<String,Integer> users = new HashMap<String,Integer>();
	
	private static Connection conn = null;
	
	private final static long account_id = 4750520330L;
	
	final static String getInitialDmUsers = "SELECT userId " +
			"FROM twitter_survey " +
			"WHERE initialDM = 0 ";
	
	final static String updateInitialDmUsers = "UPDATE twitter_survey " +
			"SET initialDM = ? " +
			"WHERE screenName = ? ";
	
	public static void main(String[] args) {
		setConnection();

		TcorsTwitterUtils u = new TcorsTwitterUtils();
		Twitter t = u.getInstance();
		
		// test sending a message
		sendInitialDMs(t);
		
		// test following someone
		// followUsers(t);
		
		// test friend RQs
		// checkFriendRQs(t);
		
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
	
	private static void sendInitialDMs(Twitter t) {
		
		// get list of users from DB
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		users = getInitialDmUsers();
		
		for(String user : users.keySet()) {
		
			// check if can send DM
			Relationship r = null;
			System.out.println("Checking relationship with:" + user);
			r = getRelationship(t,user);
			
			if(r.canSourceDm()) {
				// if able to, send message and update as success
				// sendMsg(t,"hello",user);
				users.put(user, 1);
			} else {
				// if not, update local status of denial
				users.put(user, -1);
			}
		}
		
		// update DB
		updateInitialDmUsers(users);
	}
	
	/**
	 * @param conn
	 * @return
	 */
	private static HashMap<String,Integer> getInitialDmUsers() {
		HashMap<String,Integer> users = new HashMap<String,Integer>();
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(getInitialDmUsers);
		
			while (rs.next()) {
				String userId = rs.getString("userId");
				users.put(userId, 0);
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
	
	private static void updateInitialDmUsers(HashMap<String,Integer> users) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(updateInitialDmUsers);
			for (String user : users.keySet()) {
				ps.setInt(1, 1);
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
	}
	
	/**
	 * @param t
	 * @param msg
	 * @param user
	 */
	private static void sendMsg(Twitter t, String msg, String user) {
		try {
			DirectMessage dm = t.sendDirectMessage(user, msg);
			System.out.println("Sent DM to:" + dm.getRecipientScreenName());
		} catch (TwitterException e) {
			e.printStackTrace();
			if(e.getErrorCode() == 150) {
				System.out.println("Blocked DM");
			} else {
				System.out.println("Failed message:" + e.getMessage());
			}
		}
		
	}
	
	private static void followUsers(Twitter t) {
		
		// get list of users from DB
		
		// for each user
		
		// follow user
		followUser(t, "karhai");
		
		// repeat loop
		
		// update DB
	}
	
	private static void followUser(Twitter t, String user) {
		try {
			t.createFriendship(user);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	private static void checkFriendRQs(Twitter t) {
		
		// get list of users from DB
		
		// for each user
		
		// check relationship of user with self
		Relationship r = getRelationship(t, "jenniferunger");
		boolean following = r.isSourceFollowingTarget();
		boolean followedBy = r.isSourceFollowedByTarget();
		
		// update local list as needed
		
		// repeat loop
		
		// update DB
	}
	
	private static Relationship getRelationship(Twitter t, String user) {
		Relationship r = null;
		try {
			r = t.showFriendship(4750520330L, Long.parseLong(user));
			System.out.println("Found r:" + r.toString());
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (NullPointerException n) {
			System.out.println("NPE?");
			n.printStackTrace();
		}
		return r;
	}
}
