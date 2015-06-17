package edu.usc.tcors.utils.gnip;

public class UserMention {

	private String screen_name;
	private String name;
	private int id;
	private int id_str;
	private int[] indices;
	
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId_str() {
		return id_str;
	}
	public void setId_str(int id_str) {
		this.id_str = id_str;
	}
	public int[] getIndices() {
		return indices;
	}
	public void setIndices(int[] indices) {
		this.indices = indices;
	}
}
