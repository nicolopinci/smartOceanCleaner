package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Image;
import utils.Resolution;

public class ImageDAO {

	private Connection connection = null;

	public ImageDAO(Connection conn) {
		this.connection = conn;
	}




	public List<Image> getImagesByLocationId(Integer locationId) throws SQLException{ //returns the list of Images of a specific location

		String query = "SELECT * FROM image WHERE ID_Vessel = ?"; //Selection query

		List<Image> images = new ArrayList<Image>(); //the list that will be returned
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, locationId);
		result = pStatement.executeQuery();
		while(result.next()) {
			Image tempImage = new Image(); //a temp Image object that will be used to store single rows of the resultSet

			tempImage.setID(result.getInt("ID_Image")); 
			tempImage.setLocation_id(result.getInt("ID_Vessel"));
			tempImage.setSource(result.getString("source"));
			tempImage.setResolution(Resolution.convertInt(result.getInt("resolution")));
			tempImage.setDate(result.getDate("date"));
			images.add(tempImage); // the image is added to the list
		}
		result.close(); //closing the result set
				
		pStatement.close();
		return images;	
	}


	public Integer addImage(Image img) throws SQLException { //Adds an image bean to the database and returns the Autogenerated imageID in order for us..
		//...to store the actual file in the server's file system using the correct file name 
		String query = "insert into image (ID_Vessel, date, resolution, source) values(?, ?, ?, ?)";
		try {
			PreparedStatement pStatement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			pStatement.setInt(1, img.getLocation_id());
			pStatement.setDate(2, img.getDate());
			pStatement.setInt(3, Resolution.toInt(img.getResolution()));
			pStatement.setString(4, img.getSource());
			pStatement.executeUpdate(); //esegue l'update
						
			ResultSet tableKey = pStatement.getGeneratedKeys(); //ottiene il result set dell'inserimento
			tableKey.next(); //va all'unico elemento
			Integer id =  tableKey.getInt(1); //estrae il numero id e lo restituisce
			tableKey.close();
			pStatement.close();
			return id;
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	
	public int locationOfImage(int idi) throws SQLException {

		int idl = 0;
		String query = "SELECT ID_Vessel as idl FROM image WHERE (ID_Image = ?)";
		ResultSet result = null;
		PreparedStatement pStatement = null;
		
		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idi);

		
		result = pStatement.executeQuery();
		result.next();
		idl = result.getInt("idl");
		
		result.close();		
		
		return idl;
	}

	
	public List<Image> allImagesByCampaign(int idc) throws SQLException {

		String query = "SELECT image.ID_Image AS idi, image.ID_Vessel AS idv, image.source AS source, image.resolution AS res, image.date AS date FROM (campaign_vessel JOIN image) WHERE (campaign_vessel.ID_Campaign = ? AND campaign_vessel.ID_Vessel = image.ID_Vessel)";
		
		List<Image> images = new ArrayList<Image>(); //the list that will be returned
		ResultSet result = null;

		PreparedStatement pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idc);
				
		result = pStatement.executeQuery();
		while(result.next()) {
			Image tempImage = new Image(); //a temp Image object that will be used to store single rows of the resultSet

			tempImage.setID(result.getInt("idi")); 
			tempImage.setLocation_id(result.getInt("idv"));
			tempImage.setSource(result.getString("source"));
			tempImage.setResolution(Resolution.convertInt(result.getInt("res")));
			tempImage.setDate(result.getDate("date"));
			images.add(tempImage); // the image is added to the list
		}
		result.close(); //closing the result set
				
		pStatement.close();
		return images;	
	}

}
