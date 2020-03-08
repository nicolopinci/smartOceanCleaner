package beans;

import java.sql.Date;

import utils.Trust;

public class Annotation {

	Integer ID;
	Date date;
	Trust trust;
	String wasteType;
	Integer image;
	Integer user;
	
	public Annotation() {
		
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

	public Trust getTrust() {
		return trust;
	}

	public void setTrust(Trust trust) {
		this.trust = trust;
	}

	public String getNotes() {
		return wasteType;
	}

	public void setNotes(String wasteType) {
		this.wasteType = wasteType;
	}

	public Integer getImage() {
		return image;
	}

	public void setImage(Integer image) {
		this.image = image;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}


}
