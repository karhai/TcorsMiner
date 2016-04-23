package edu.usc.tcors.study.survey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import edu.usc.tcors.utils.TcorsMinerUtils;
import edu.usc.tcors.utils.TcorsTwitterUtils;
import twitter4j.DirectMessage;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TcorsTwitterSurvey {

	private HashMap<String,Integer> users = new HashMap<String,Integer>();
	
	private final static String account_name = "USC_TCORS";
	
	final static String getInitialDmUsers = "SELECT userId " +
			"FROM twitter_survey " +
			"WHERE initialDM = 0 ";
	
	public static void main(String[] args) {
		// TcorsTwitterSurvey tts = new TcorsTwitterSurvey();

		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = null;
		try {
			conn = tmu.getDBConn("configuration.properties");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		TcorsTwitterUtils u = new TcorsTwitterUtils();
		Twitter t = u.getInstance();
		
		// test sending a message
		sendInitialDMs(t, conn);
		
		// test following someone
		// followUsers(t);
		
		// test friend RQs
		// checkFriendRQs(t);
	}
	
	private static void sendInitialDMs(Twitter t, Connection conn) {
		
		// get list of users from DB
		getInitialDmUsers(conn);
		
		// for each user
		String user = "karhai";
		
		// check if can send DM
		getRelationship(t,user).canSourceDm();
		
		// if yes, send DM
		sendMsg(t,"hello",user);
		
		// update their local status success or fail
		
		// if not, update local status of denial
		
		// repeat loop
		
		// update DB
	}
	
	private static void getInitialDmUsers(Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(getInitialDmUsers);
		
			while (rs.next()) {
				String userId = rs.getString("userId");
				
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// process single name via DM
	private static void sendMsg(Twitter t, String msg, String user) {
		try {
			DirectMessage dm = t.sendDirectMessage(user, msg);
			// System.out.println("Sent to:" + dm.getRecipientScreenName());
			markUser(user, "success");
		} catch (TwitterException e) {
			e.printStackTrace();
			if(e.getErrorCode() == 150) {
				markUser(user, "block");
			} else {
				System.out.println("Failed message:" + e.getMessage());
			}
		}
		
	}
	
	private static void markUser(String user, String mark) {
		
	}

	// 
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
			r = t.showFriendship(account_name, user);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return r;
	}
}
