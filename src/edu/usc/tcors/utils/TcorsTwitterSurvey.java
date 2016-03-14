package edu.usc.tcors.utils;

import java.sql.Connection;
import java.sql.SQLException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TcorsTwitterSurvey {

	public static void main(String[] args) {
		TcorsTwitterUtils u = new TcorsTwitterUtils();
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		Connection conn = null;
		Twitter t = u.getInstance();
		
		String survey_msg = "";
		
		try {
			conn = tmu.getDBConn("configuration.properties");
		} catch (SQLException s) {
			s.printStackTrace();
		}
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
			t.sendDirectMessage(user, msg);
		} catch (TwitterException e) {
			e.printStackTrace();
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
}
