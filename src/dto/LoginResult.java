package dto;

import utils.TypeOfUser;

public class LoginResult {
	
	private Integer id;
	private String username;
	private TypeOfUser userType;
	
	public LoginResult(Integer id,String username, TypeOfUser userType) {
		this.id = id;
		this.username = username;
		this.userType = userType;
	}

	public String getUsername() {
		return username;
	}
	
	public Integer getId() {
		return id;
	}

	public TypeOfUser getUserType() {
		return userType;
	}	
	
}
