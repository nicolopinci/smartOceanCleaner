package dao;

import java.sql.*;

import dto.LoginResult;
import utils.*;

public class LoginDAO {

	private Connection connection = null;

	public LoginDAO(Connection con)	{
		this.connection = con;
	}


	public LoginResult login(String username, String psw) throws SQLException {

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM user where (BINARY username = ? AND BINARY psw = ?)"; //query - binary for case sensitiveness
		
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, username);
			pStatement.setString(2, psw);
			results = pStatement.executeQuery();


			TypeOfUser resultType = null;
			Integer resultID = null;
			String resultName = null;
			int tempInt = 0; // used to temporarily store "isAdmin" value returned by db


			while(results.next()) {
				resultID= results.getInt("ID");
				resultName = results.getString("username");
				
				tempInt = results.getInt("isAdmin");
				if(tempInt == 0) { // convert isAdmin in TYPE OF USER
					resultType = TypeOfUser.worker;
				}else if(tempInt == 1) {
					resultType = TypeOfUser.manager;
				}
				
			}
			

			
			if(resultID==null || resultName == null) { //just for safety
				return null;
			} else {
				return new LoginResult(resultID,resultName,resultType);
			}
		}catch(SQLException e){
			throw new SQLException(e);
		}
	}

	public int register(String username, String psw, String email, Integer isAdmin) throws SQLException {

		PreparedStatement pStatement= null;
		String query = "INSERT INTO user VALUES(0, ?, ?, ?, ?, ?, ?, ?)"; //query

		pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pStatement.setString(1, username);
		pStatement.setString(2, psw);
		pStatement.setString(3, email);
		pStatement.setInt(4, 0);
		pStatement.setInt(5, isAdmin);
		pStatement.setInt(6, 0);
		pStatement.setInt(7, 50);
		
		pStatement.executeUpdate();
		
		ResultSet tableKey = pStatement.getGeneratedKeys(); // obtain the result set of insertion
		tableKey.next(); // go to the only element
		Integer id =  tableKey.getInt(1); // extracts the number and returns it
		tableKey.close();
		pStatement.close();
		return id;

	}
}

