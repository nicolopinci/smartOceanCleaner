package dao;

import java.sql.*;

import beans.User;
import utils.*;

public class UserDAO {

	private Connection connection = null;

	public UserDAO(Connection con){
		this.connection = con;
	}

	public User getProfile(Integer id) throws SQLException {

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM user where (ID = ?)"; //query
		User userData = new User();

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, id);
			results = pStatement.executeQuery();

			while(results.next()) {
				userData.setUsername(results.getString("username"));
				userData.setPassword(results.getString("psw"));
				userData.setEmail(results.getString("e-mail"));					
				userData.setPoints(results.getInt("points"));
				userData.setIsAdmin(results.getBoolean("isAdmin"));
				userData.setIsRobot(results.getBoolean("isRobot"));
				userData.setPercentage(results.getInt("percentage"));


			}

			return userData;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}
	
	public void editProfile(Integer ID, String username, String psw, String email) throws SQLException {

		
		PreparedStatement pStatement= null;
		String query = "UPDATE user SET username=?, psw=?, `e-mail`=? WHERE ID=?"; //query

		pStatement = connection.prepareStatement(query);
		pStatement.setString(1, username);
		pStatement.setString(2, psw);
		pStatement.setString(3, email);
		pStatement.setInt(4, ID);
		
		pStatement.executeUpdate();

	}
	
	public void updatePoints(Integer ID, Integer points) throws SQLException {

		
		PreparedStatement pStatement= null;
		String query = "UPDATE user SET points=? WHERE ID=?"; //query

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, points);
		pStatement.setInt(2, ID);
		
		pStatement.executeUpdate();

	}
	
	public void updatePercentage(Integer ID, Integer percentage) throws SQLException {

		
		PreparedStatement pStatement= null;
		String query = "UPDATE user SET percentage=? WHERE ID=?"; //query

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, percentage);
		pStatement.setInt(2, ID);
		
		pStatement.executeUpdate();

	}
	
	
	public Integer numberOfEqualEmails(String email) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS num FROM user WHERE (`e-mail`=?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setString(1, email);
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("num");
		result.close();		
		
		return number;
	}
	
	public Integer numberOfEqualUsernames(String username) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS num FROM user WHERE (BINARY username=?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setString(1, username);
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("num");
		result.close();		
		
		return number;
	}
	public Integer numberOfEqualEmailsButId(String email, Integer id) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS num FROM user WHERE (`e-mail`=? AND ID!=?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setString(1, email);
		pStatement.setInt(2, id);

		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("num");
		result.close();		
		
		return number;
	}
	
	public Integer numberOfEqualUsernamesButId(String username, Integer id) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS num FROM user WHERE (BINARY username=? and ID!=?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setString(1, username);
		pStatement.setInt(2, id);

		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("num");
		result.close();		
		
		return number;
	}
}

