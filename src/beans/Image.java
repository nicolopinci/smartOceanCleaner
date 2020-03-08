package beans;

import java.sql.Date;
import utils.Resolution;

public class Image {

	Integer ID;
	Date date;
	Resolution resolution;
	Integer location_id;
	String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Image() {

	}
	
	public Integer getID() {
		return ID;
	}
	
	public void setID(Integer iD) {
		ID = iD;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Resolution getResolution() {
		return resolution;
	}
	
	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}
	
	public Integer getLocation_id() {
		return location_id;
	}
	
	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}

}
