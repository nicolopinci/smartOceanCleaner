package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import beans.Annotation;
import beans.User;
import utils.ExpLevel;
import utils.Trust;
import utils.Validity;

public class AnnotationDAO {

	private Connection connection = null;

	public AnnotationDAO(Connection con){
		this.connection=con;
	}

	public void createAnnotation(Annotation ann) throws SQLException {

		PreparedStatement pStatement = null;
		String query = "INSERT INTO annotation VALUES(0, ?, ?, ?, ?, ?)";


		pStatement = connection.prepareStatement(query);
		pStatement.setDate(1, ann.getDate());
		pStatement.setInt(2, ann.getTrust().ordinal());
		pStatement.setString(3, ann.getNotes());
		pStatement.setInt(4, ann.getImage());
		pStatement.setInt(5, ann.getUser());

		pStatement.executeUpdate();
	}
	public Map<Annotation, User> getAnnotationsByImageID(Integer imageID) throws SQLException{ //returns the list of Images of a specific Lcation

		String query = "SELECT * FROM (annotation JOIN user) WHERE (ID_Image = ? AND annotation.ID_User = user.ID)"; //Selection query

		Map<Annotation, User> annotUser = new HashMap<Annotation, User>();
		
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, imageID);
		result = pStatement.executeQuery();
		while(result.next()) {
			Annotation tempAnnotation = new Annotation(); //a temp Image object that will be used to store single rows of the resultSet

			tempAnnotation.setID(result.getInt("ID_Annotation"));
			tempAnnotation.setDate(result.getDate("date"));
			tempAnnotation.setTrust(Trust.convertInt(result.getInt("trust")));
			tempAnnotation.setNotes(result.getString("wasteType"));
			tempAnnotation.setImage(result.getInt("ID_Image")); 
			tempAnnotation.setUser(result.getInt("ID_User")); 

			User tempUser = new User();
			
			tempUser.setUsername(result.getString("username"));
			tempUser.setPassword("");
			tempUser.setEmail(result.getString("e-mail"));					
			tempUser.setPoints(result.getInt("points"));
			tempUser.setIsAdmin(result.getBoolean("isAdmin"));
			tempUser.setIsRobot(result.getBoolean("isRobot"));
			tempUser.setPercentage(result.getInt("percentage"));
			
			annotUser.put(tempAnnotation, tempUser);
		}
		result.close(); //closing the result set
		pStatement.close();
		return annotUser;	
	}

	
	public Integer isImageAccessible(Integer idi, Integer idw) throws SQLException {
		String query = "SELECT COUNT(*) as imAc FROM (user JOIN image JOIN campaign_vessel JOIN campaign JOIN player_campaign) WHERE (player_campaign.ID_Campaign = campaign.ID and player_campaign.ID_User = user.ID and image.ID_Vessel = campaign_vessel.ID_Vessel AND campaign_vessel.ID_Campaign = campaign.ID AND image.ID_Image = ? AND campaign.status = 1 AND user.ID = ? AND user.isAdmin = 0);";
	
	
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idi);
		pStatement.setInt(2, idw);

		Integer imAc = 0;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			imAc = result.getInt("imAc");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return imAc;	
	
	}
	
	
	public Integer hasRobotAnnotated(Integer idi) throws SQLException {
		String query = "SELECT COUNT(*) AS hasAnn FROM (annotation JOIN user) WHERE (ID_Image = ? AND user.isRobot = 1)";
		
		
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idi);

		Integer hasAnn = 1;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			hasAnn = result.getInt("hasAnn");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return hasAnn;	
	
	}
	
	
	public Integer hasUserAlreadyAnnotated(Integer idi, Integer idw) throws SQLException {
		String query = "SELECT COUNT(*) AS hasAnn FROM annotation WHERE (ID_Image = ? AND ID_User = ?)";
	
	
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idi);
		pStatement.setInt(2, idw);

		Integer hasAnn = 1;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			hasAnn = result.getInt("hasAnn");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return hasAnn;	
	
	}
	
	public Integer userAnnotations(Integer idw) throws SQLException {
		String query = "SELECT COUNT(*) AS numAnn FROM annotation WHERE (ID_User = ?)";
	
	
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idw);

		Integer numAnn = 1;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			numAnn = result.getInt("numAnn");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return numAnn;	
	
	}
	
	public Integer countAllAnnotations(Integer idi) throws SQLException {
		String query = "SELECT COUNT(*) AS ca FROM annotation WHERE (ID_Image = ?)";
	
	
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idi);

		Integer ca = 1;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			ca = result.getInt("ca");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return ca;	
	
	}
	
	public Integer countUserAnnotations(Integer idu) throws SQLException {
		String query = "SELECT COUNT(*) AS ca FROM annotation WHERE (ID_User = ?)";
	
	
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idu);

		Integer ca = 1;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			ca = result.getInt("ca");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return ca;	
	
	}
	
	public Integer countMaterialAnnotations(Integer idi, String material) throws SQLException {
		String query = "SELECT COUNT(*) AS ca FROM annotation WHERE (ID_Image = ? AND wasteType = ?)";
	
	
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idi);
		pStatement.setString(2, material);


		Integer ca = 1;
		
		result = pStatement.executeQuery();
		while(result.next()) {

			ca = result.getInt("ca");
			

		}
		result.close(); //closing the result set
		pStatement.close();
		return ca;	
	
	}
	
	


}