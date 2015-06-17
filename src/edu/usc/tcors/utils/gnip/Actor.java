package edu.usc.tcors.utils.gnip;

import java.sql.Date;
import java.util.List;

public class Actor {

	private String objectType;
	private String link;
	private String id;
	private String displayName;
	private String postedTime;
	private String image;
	private String summary;
	private List<Link> links;
	private int friendsCount;
	private int followersCount;
	private int listedCount;
	private int statusesCount;
	private String twitterTimeZone;
	private String verified;
	private String utcOffset;
	private String preferredUsername;
	private List<String> languages;
	private Location location;
	private int favoritesCount;
	
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPostedTime() {
		return postedTime;
	}
	public void setPostedTime(String postedTime) {
		this.postedTime = postedTime;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getFriendsCount() {
		return friendsCount;
	}
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public int getListedCount() {
		return listedCount;
	}
	public void setListedCount(int listedCount) {
		this.listedCount = listedCount;
	}
	public int getStatusesCount() {
		return statusesCount;
	}
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}
	public String getTwitterTimeZone() {
		return twitterTimeZone;
	}
	public void setTwitterTimeZone(String twitterTimeZone) {
		this.twitterTimeZone = twitterTimeZone;
	}
	public String getVerified() {
		return verified;
	}
	public void setVerified(String verified) {
		this.verified = verified;
	}
	public String getUtcOffset() {
		return utcOffset;
	}
	public void setUtcOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}
	public String getPreferredUsername() {
		return preferredUsername;
	}
	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public int getFavoritesCount() {
		return favoritesCount;
	}
	public void setFavoritesCount(int favoritesCount) {
		this.favoritesCount = favoritesCount;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
