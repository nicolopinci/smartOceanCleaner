package beans;

import utils.Status;

public class Campaign {

	Integer ID;
	String name;
	String client;
	Status status;
	Integer manager;

	public Campaign() {

	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getClient() {
		return client;
	}
	
	public void setClient(String client) {
		this.client = client;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Integer getManager() {
		return manager;
	}
	
	public void setManager(Integer manager) {
		this.manager = manager;
	}

}
