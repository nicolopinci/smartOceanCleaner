package beans;

public class User {

	Integer ID;
	String username;
	String password;
	String email;
	Integer points;
	
	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	Boolean isAdmin;
	Boolean isRobot;
	Integer percentage;


	public Integer getPercentage() {
		return percentage;
	}

	public void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	public Boolean getIsRobot() {
		return isRobot;
	}

	public void setIsRobot(Boolean isRobot) {
		this.isRobot = isRobot;
	}

	public User() {
		
	}
	
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}	
	

	
}
