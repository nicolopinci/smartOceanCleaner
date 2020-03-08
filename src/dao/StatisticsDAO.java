package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import beans.Image;
import utils.Resolution;

public class StatisticsDAO {
	
	private Connection connection = null;
	
	public StatisticsDAO(Connection connection){
		this.connection = connection;
	}

	public int locationsNumber(int campaignID) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS locations FROM campaign_vessel WHERE ID_Campaign = ?";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("locations");
		result.close();		
		
		return number;
	}
	
	public int imagesNumber(int campaignID) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS images FROM campaign_vessel JOIN image ON image.ID_Vessel = campaign_location.ID_Location WHERE campaign_vessel.ID_Campaign = ?";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("images");
		result.close();		
		
		return number;
	}
	
	public int annotationsNumber(int campaignID) throws SQLException {
		int number = 0;
		String query = "SELECT COUNT(*) AS annotations FROM (campaign_vessel JOIN image ON campaign_vessel.ID_Vessel = image.ID_Vessel) JOIN annotation ON image.ID_Image = annotation.ID_Image WHERE campaign_vessel.ID_Campaign = ?";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("annotations");
		result.close();		
		
		return number;
	}
	
	public Double imagesAverage(int campaignID) throws SQLException {
		Double number = 0.0;
		String query = "SELECT AVG(Images.ImagesNumber) AS ImagesAvg FROM (SELECT COUNT(image.I) AS ImagesNumber FROM image JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE campaign.ID_Campaign = ? GROUP BY image.ID_Location) AS Images"; 

		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		result = pStatement.executeQuery();
		result.next();
		number = result.getDouble("ImagesAvg");
		result.close();		
				
		return number;
	}
	
	public Double notesAverage(int campaignID) throws SQLException {
		Double number = 0.0;
		String query = "SELECT AVG(Notes.NotesNumber) as NotesAvg FROM (SELECT COUNT(image.ID_Image) AS NotesNumber, image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE campaign_vessel.ID_Campaign = ? GROUP BY image.ID_Image) AS Notes";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		result = pStatement.executeQuery();
		result.next();
		number = result.getDouble("NotesAvg");
		result.close();		
		
		return number;
	}
	
	
	public List<Image> conflictsList(int campaignID) throws SQLException {

		List<Image> imagesList = new ArrayList<Image>(); 
		String query = "SELECT DISTINCT image.* FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE (image.ID_Image IN (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE (ID_campaign = ? AND validity = 0)) AND image.ID_Image IN (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_vessel = campaign_vessel.ID_Vessel WHERE (ID_Vessel = ? AND validity = 1)))";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		pStatement.setInt(2, campaignID);
		result = pStatement.executeQuery();
		
		while(result.next()) {
			Image image = new Image();

			image.setID(result.getInt("ID_Image"));
			image.setLocation_id(result.getInt("ID_Location"));
			image.setDate(result.getDate("date"));
			imagesList.add(image);
		}
		
		result.close();		
	

		return imagesList;
	}
	
	public int conflictsNumber(int campaignID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE (image.ID_Image IN (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE (ID_campaign = ? AND validity = 0)) AND image.ID_Image IN (SELECT DISTINCT image.ID_Image FROM (image JOIN annotation ON image.ID_Image = annotation.ID_Image) JOIN campaign_vessel ON image.ID_Vessel = campaign_vessel.ID_Vessel WHERE (ID_campaign = ? AND validity = 1)))) AS CONFLICTS";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, campaignID);
		pStatement.setInt(2, campaignID);
		
		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");
		
		result.close();		
		
		return number;
	}
	
}
