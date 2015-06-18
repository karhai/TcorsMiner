package edu.usc.tcors.utils.gnip;

public class Geo {

	private String type;
	
	// TODO: handle multiple types of "coordinates"
	private Object coordinates;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Object coordinates) {
		this.coordinates = coordinates;
	}
}
