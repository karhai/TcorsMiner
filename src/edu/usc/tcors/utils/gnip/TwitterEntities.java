package edu.usc.tcors.utils.gnip;

import java.util.List;

public class TwitterEntities {

	private List<Hashtag> hashtags;
	private List<String> trends;
	private List<String> urls;
	private List<UserMention> user_mentions;
	private List<String> symbols;
	
	public List<Hashtag> getHashtags() {
		return hashtags;
	}
	public void setHashtags(List<Hashtag> hashtags) {
		this.hashtags = hashtags;
	}
	public List<String> getTrends() {
		return trends;
	}
	public void setTrends(List<String> trends) {
		this.trends = trends;
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	public List<UserMention> getUser_mentions() {
		return user_mentions;
	}
	public void setUser_mentions(List<UserMention> user_mentions) {
		this.user_mentions = user_mentions;
	}
	public List<String> getSymbols() {
		return symbols;
	}
	public void setSymbols(List<String> symbols) {
		this.symbols = symbols;
	}
}
