package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Location;

public class LocationDAO {

	private Connection connection=null;

	public LocationDAO(Connection con)	{
		this.connection = con;
	}


	public Integer createLocation(Location location,Integer campaignID) throws SQLException { //adds location to db
														//and returns the id of the added location
		PreparedStatement pStatement= null;
		String query = "INSERT INTO vessel (latitude,longitude,name,municipality,region) VALUES (?, ?, ?, ?, ?)"; //to insert a new location into the db
		ResultSet tableKey;
		
		pStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		pStatement.setString(1, location.getLatitude());
		pStatement.setString(2, location.getLongitude());
		pStatement.setString(3, location.getName());
		pStatement.setString(4, location.getCity());
		pStatement.setString(5, location.getRegion());
		
		pStatement.executeUpdate();
		
		tableKey = pStatement.getGeneratedKeys();
		
		tableKey.next();
		Integer id = tableKey.getInt(1);
		tableKey.close();
		
		query = "INSERT INTO campaign_vessel (ID_Campaign,ID_Vessel) VALUES (?,?)"; //to create the "connection" between Campagna and Localita
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		pStatement.setInt(2, id);
		pStatement.executeUpdate();
		
		pStatement.close();
		
		return id;
	}

	public Location getLocationInfo(Integer idl) throws SQLException {
		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM vessel WHERE (ID=?)";


		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idl);
						
			results = pStatement.executeQuery();

			Location l = new Location();

			while(results.next()) {
				l.setID(results.getInt("ID"));
				l.setLatitude(results.getString("latitude"));
				l.setLongitude(results.getString("longitude"));
				l.setName(results.getString("name"));
				l.setCity(results.getString("municipality"));
				l.setRegion(results.getString("region"));
			}
			results.close();
			pStatement.close();
						
			return l;

		}catch(SQLException e){
			System.out.println("PROBLEM WITH GET LOCATION INFO");
			throw new SQLException(e); //  TODO implementare un interfaccia migliore
		}
	}
	
	
	public List<List<Location>> getLocationsByCampaign(Integer idc) throws SQLException{ // get all the locations - to be shown for the worker
		List<Location> greenLocations = new ArrayList<Location>();
		List<Location> yellowLocations = new ArrayList<Location>();
		List<Location> redLocations = new ArrayList<Location>();

		List<List<Location>> locationsLists = new ArrayList<List<Location>>();

		String query = "SELECT ID,latitude,longitude,name,municipality,region FROM campaign_vessel AS cl join vessel AS l on cl.ID_Vessel=l.ID where cl.ID_Campaign=?";
		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idc);
		
		ResultSet resultSet = pStatement.executeQuery();
		Location tempLocation;
		
		while(resultSet.next()) {
			tempLocation = new Location();
			tempLocation.setID(resultSet.getInt("ID"));
			tempLocation.setLatitude(resultSet.getString("latitude"));
			tempLocation.setLongitude(resultSet.getString("longitude"));
			tempLocation.setName(resultSet.getString("name"));
			tempLocation.setCity(resultSet.getString("municipality"));
			tempLocation.setRegion(resultSet.getString("region"));
			
			Integer factor = countHumanEvaluations(tempLocation.getID())/countRobotEvaluations(tempLocation.getID());
						
			if(factor <= 1) {
				redLocations.add(tempLocation);
			}
			else if(factor >= 10) {
				greenLocations.add(tempLocation);
			}
			else {
				yellowLocations.add(tempLocation);
			}
		}
				
		locationsLists.add(greenLocations);
		locationsLists.add(yellowLocations);
		locationsLists.add(redLocations);

		
		return locationsLists;
	}
	
	
	public int conflictsNumberByLocation(int locationID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) WHERE (image.ID_Image IN (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) WHERE (ID_Vessel = ?)) AND image.ID_Image IN (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) WHERE (ID_Vessel = ?)))) AS CONFLICTS";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, locationID);
		pStatement.setInt(2, locationID);
		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
	public int countHumanEvaluations(int locationID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM (annotation JOIN user JOIN image) WHERE (annotation.ID_Image = image.ID_Image AND image.ID_Vessel = ? AND annotation.ID_User = user.ID AND user.isRobot = 0)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, locationID);
		
		System.out.println(pStatement);
		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
	public int countRobotEvaluations(int locationID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM (annotation JOIN user JOIN image) WHERE (annotation.ID_Image = image.ID_Image AND image.ID_Vessel = ? AND annotation.ID_User = user.ID AND user.isRobot = 1)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, locationID);
		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
	public int imageNumberByLocation(int locationID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM image WHERE image.ID_Vessel=?";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, locationID);
		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
	public int annotationNumberByLocation(int locationID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM (image JOIN annotation) WHERE (image.ID_Vessel=? AND image.ID_Image = annotation.ID_Image)" ;
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, locationID);
		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
	
	public int isUserTheLocationOwner(int locationID, int userID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) as number FROM (user JOIN campaign JOIN campaign_vessel) WHERE (user.ID=? AND campaign.manager = user.ID and campaign.ID = campaign_vessel.ID_Campaign AND campaign_vessel.ID_Vessel=?);";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, userID);
		pStatement.setInt(2, locationID);

		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
				
		return number;
	}
	
	
	public int isThisLocationInThisCampaign(int locationID, int campaignID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) as number FROM campaign_vessel WHERE (ID_Campaign = ? AND ID_Vessel = ?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		pStatement.setInt(2, locationID);

		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
	
	public int campaignOfLocation(int idl) throws SQLException {

		int idc = 0;
		String query = "SELECT ID_Campaign as idc FROM campaign_vessel WHERE (ID_Vessel = ?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idl);

		
		result = pStatement.executeQuery();
		result.next();
		idc = result.getInt("idc");
		
		result.close();		
		
		return idc;
	}
	
}

