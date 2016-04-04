package edu.usc.tcors.study.survey;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import edu.usc.tcors.utils.TcorsMinerUtils;
import edu.usc.tcors.utils.TcorsTwitterUtils;
import twitter4j.DirectMessage;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TcorsTwitterSurvey {

	private HashMap<String,String> users = new HashMap<String,String>();
	
	public static void main(String[] args) {
		TcorsTwitterSurvey tts = new TcorsTwitterSurvey();
		TcorsTwitterUtils u = new TcorsTwitterUtils();
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		// Connection conn = null;
		Twitter t = u.getInstance();
		
		// test sending a message
//		String survey_msg = "";
//		tts.sendMsg(t,"hello","gvegayon");
		
		// test following someone
		tts.followUser(t, "karhai");
		
//		try {
//			conn = tmu.getDBConn("configuration.properties");
//		} catch (SQLException s) {
//			s.printStackTrace();
//		}
	}
	
	// retrieve survey message
	private void getSurveyMessage() {
		
	}
	
	// retreve names from file
	private void getNames(String file) {
		
	}
	
	// process multiple names via DM
	private void sendMsgs() {
		
	}
	
	// process single name via DM
	private void sendMsg(Twitter t, String msg, String user) {
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
	
	// process multiple names via mention
	private void sendMentions() {
		
	}
	
	// process single name via mention
	private void sendMention(Twitter t, String msg) {
		try {
			t.updateStatus(msg);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private void markUser(String user, String mark) {
		
	}
	
	public HashMap<String, String> getUsers() {
		return users;
	}

	// populate users to work with based on attributes from DB
	public void setUsers(HashMap<String, String> users) {
		this.users = users;
	}
	
	private void followUser(Twitter t, String user) {
		try {
			t.createFriendship(user);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Relationship getRelationship(Twitter t, String user) {
		Relationship r = null;
		try {
			r = t.showFriendship("StudyTobacco", user);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}
	
	private boolean isFollowing(Relationship r) {
		return r.isSourceFollowingTarget();
	}
	
	private boolean isFollowedBy(Relationship r) {
		return r.isSourceFollowedByTarget();
	}
	
	private void updateDB() {
		
	}
}
