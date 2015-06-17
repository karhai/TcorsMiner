package edu.usc.tcors.utils.gnip;

import java.sql.Date;

public class Actor {

	private String objectType;
	private String user_id;
	private boolean unavailable;
	private Date unavailable_at;
	private String handle;
	private String bio;
	private String lang;
	private String time_zone;
	private int utc_offset;
	private Date posted_at;
	
	private String location;
	private String profile_geo_name;
	private float profile_geo_long;
	private float profile_geo_lat;
	private String profile_geo_country_code;
	private String profile_geo_region;
	private String profile_geo_subregion;
	private String profile_geo_locality;
	
	private Date created_at;
	private Date updated_at;
	
	private int followers_count;
	private int friends_count;
	private int statuses_count;
	private int klout_score;
	private String topics;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public boolean isUnavailable() {
		return unavailable;
	}
	public void setUnavailable(boolean unavailable) {
		this.unavailable = unavailable;
	}
	public Date getUnavailable_at() {
		return unavailable_at;
	}
	public void setUnavailable_at(Date unavailable_at) {
		this.unavailable_at = unavailable_at;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getTime_zone() {
		return time_zone;
	}
	public void setTime_zone(String time_zone) {
		this.time_zone = time_zone;
	}
	public int getUtc_offset() {
		return utc_offset;
	}
	public void setUtc_offset(int utc_offset) {
		this.utc_offset = utc_offset;
	}
	public Date getPosted_at() {
		return posted_at;
	}
	public void setPosted_at(Date posted_at) {
		this.posted_at = posted_at;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getProfile_geo_name() {
		return profile_geo_name;
	}
	public void setProfile_geo_name(String profile_geo_name) {
		this.profile_geo_name = profile_geo_name;
	}
	public float getProfile_geo_long() {
		return profile_geo_long;
	}
	public void setProfile_geo_long(float profile_geo_long) {
		this.profile_geo_long = profile_geo_long;
	}
	public float getProfile_geo_lat() {
		return profile_geo_lat;
	}
	public void setProfile_geo_lat(float profile_geo_lat) {
		this.profile_geo_lat = profile_geo_lat;
	}
	public String getProfile_geo_country_code() {
		return profile_geo_country_code;
	}
	public void setProfile_geo_country_code(String profile_geo_country_code) {
		this.profile_geo_country_code = profile_geo_country_code;
	}
	public String getProfile_geo_region() {
		return profile_geo_region;
	}
	public void setProfile_geo_region(String profile_geo_region) {
		this.profile_geo_region = profile_geo_region;
	}
	public String getProfile_geo_subregion() {
		return profile_geo_subregion;
	}
	public void setProfile_geo_subregion(String profile_geo_subregion) {
		this.profile_geo_subregion = profile_geo_subregion;
	}
	public String getProfile_geo_locality() {
		return profile_geo_locality;
	}
	public void setProfile_geo_locality(String profile_geo_locality) {
		this.profile_geo_locality = profile_geo_locality;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public int getFollowers_count() {
		return followers_count;
	}
	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}
	public int getFriends_count() {
		return friends_count;
	}
	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}
	public int getStatuses_count() {
		return statuses_count;
	}
	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}
	public int getKlout_score() {
		return klout_score;
	}
	public void setKlout_score(int klout_score) {
		this.klout_score = klout_score;
	}
	public String getTopics() {
		return topics;
	}
	public void setTopics(String topics) {
		this.topics = topics;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
