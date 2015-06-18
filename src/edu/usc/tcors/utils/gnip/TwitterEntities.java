package edu.usc.tcors.utils.gnip;

import java.util.List;

public class TwitterEntities {

	private List<Hashtag> hashtags;
	private List<String> trends;
	private List<Url> urls;
	private List<UserMention> user_mentions;
	private List<Symbol> symbols;
	private List<Media> media;
	
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
	public List<Url> getUrls() {
		return urls;
	}
	public void setUrls(List<Url> urls) {
		this.urls = urls;
	}
	public List<UserMention> getUser_mentions() {
		return user_mentions;
	}
	public void setUser_mentions(List<UserMention> user_mentions) {
		this.user_mentions = user_mentions;
	}
	public List<Symbol> getSymbols() {
		return symbols;
	}
	public void setSymbols(List<Symbol> symbols) {
		this.symbols = symbols;
	}
	public List<Media> getMedia() {
		return media;
	}
	public void setMedia(List<Media> media) {
		this.media = media;
	}
}
