package dao;

import java.util.ArrayList;

import beans.Campaign;
import utils.*;

import java.sql.*;

public class CampaignDAO {

	private Connection connection = null;

	public CampaignDAO(Connection con)	{
		this.connection = con;
	}

	public ArrayList<Campaign> getAvailableCampaigns(Integer idw) throws SQLException{

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM campaign WHERE (campaign.ID NOT IN (SELECT ID_Campaign FROM player_campaign WHERE (ID_User = ?)) AND campaign.status = 1)"; //query

		ArrayList<Campaign> campaignList = new ArrayList<Campaign>();

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idw);
			results = pStatement.executeQuery();

			while(results.next()) {

				Campaign c = new Campaign();

				c.setID(results.getInt("ID"));
				c.setName(results.getString("name"));
				c.setClient(results.getString("customer"));
				c.setStatus(Status.values()[results.getInt("status")]);
				c.setManager(results.getInt("manager"));

				campaignList.add(c);
			}

			return campaignList;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}

	public Integer isAvailable(Integer idw, Integer idc) throws SQLException{

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT COUNT(*) AS isAv FROM campaign WHERE (campaign.ID = ? AND campaign.ID NOT IN (SELECT ID_Campaign FROM player_campaign WHERE (ID_User = ?)) AND campaign.status = 1)"; //query

		Integer isAv = 0;

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idc);
			pStatement.setInt(2, idw);

			results = pStatement.executeQuery();

			while(results.next()) {

				isAv = results.getInt("isAv");
			}

			return isAv;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}


	public ArrayList<Campaign> getCompletedCampaigns(Integer idw) throws SQLException{

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM campaign WHERE (campaign.ID IN (SELECT ID_Campaign FROM player_campaign WHERE (ID_User = ?)) AND campaign.status = 1)"; //query

		ArrayList<Campaign> campaignList = new ArrayList<Campaign>();

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idw);

			results = pStatement.executeQuery();

			while(results.next()) {

				Campaign c = new Campaign();

				c.setID(results.getInt("ID"));
				c.setName(results.getString("name"));
				c.setClient(results.getString("customer"));
				c.setStatus(Status.values()[results.getInt("status")]);
				c.setManager(results.getInt("manager"));

				campaignList.add(c);
			}

			return campaignList;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un interfaccia migliore
		}
	}


	public ArrayList<Campaign> getManagerCampaigns(Integer id) throws SQLException{

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM (campaign JOIN user) WHERE (campaign.manager = user.ID AND user.ID = ?) ORDER BY campaign.ID DESC";
		ArrayList<Campaign> campaignList = new ArrayList<Campaign>();

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, id);
			results = pStatement.executeQuery();

			while(results.next()) {

				Campaign c = new Campaign();

				c.setID(results.getInt("ID"));
				c.setName(results.getString("name"));
				c.setClient(results.getString("customer"));
				c.setStatus(Status.values()[results.getInt("status")]);
				c.setManager(results.getInt("manager"));

				campaignList.add(c);
			}

			return campaignList;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}


	public Integer createCampaign(String name, String customer, Integer manager) throws SQLException {

		PreparedStatement pStatement= null;
		String query = "INSERT INTO campaign (name, customer, status, manager) VALUES (?, ?, ?, ?)"; //query


		pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

		pStatement.setString(1, name);
		pStatement.setString(2, customer);
		pStatement.setInt(3, 0);
		pStatement.setInt(4, manager);


		pStatement.executeUpdate();

		// Useful in order to determine the primary key which has been generated
		ResultSet tableKeys = pStatement.getGeneratedKeys();

		tableKeys.next();

		return tableKeys.getInt(1); // returns the primary key, so that it is possible to go directly to the detail page
	}

	public void associateWithCampaign(Integer idu, Integer idc) throws SQLException {

		PreparedStatement pStatement= null;
		String query = "INSERT INTO player_campaign VALUES (?, ?)"; //query


		pStatement = connection.prepareStatement(query);

		pStatement.setInt(1, idu);
		pStatement.setInt(2, idc);


		pStatement.executeUpdate();

	}


	public Campaign getCampaignInfo(Integer idc, Integer manager) throws SQLException{

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT * FROM campaign WHERE (ID=? AND manager=?)";


		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idc);
			pStatement.setInt(2, manager);

			results = pStatement.executeQuery();

			Campaign c = new Campaign();

			while(results.next()) {

				c.setID(results.getInt("ID"));
				c.setName(results.getString("name"));
				c.setClient(results.getString("customer"));
				c.setStatus(Status.values()[results.getInt("status")]);
				c.setManager(results.getInt("manager"));

			}

			return c;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un interfaccia migliore
		}
	}


	public void closeCampaign(Integer idc, Integer idu) throws SQLException{

		PreparedStatement pStatement= null;
		String query = "UPDATE campaign SET status=2 WHERE (ID=? AND manager=? AND status=1)";


		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idc);
			pStatement.setInt(2, idu);

			pStatement.executeUpdate();

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}
	public void startCampaign(Integer idc, Integer idu) throws SQLException{

		PreparedStatement pStatement= null;
		String query = "UPDATE campaign SET status=1 WHERE (ID=? AND manager=? AND status=0)";


		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idc);
			pStatement.setInt(2, idu);

			pStatement.executeUpdate();

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}

	public String getCampaignName(int id) throws SQLException{

		String name = null;
		String query = "SELECT name FROM campaign WHERE ID = ?";

		PreparedStatement pStatement = null;
		ResultSet result = null;

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, id);
		result = pStatement.executeQuery();
		result.next();
		name = result.getString("nome");
		result.close();

		return name;
	}

	public Boolean isStartable(int id) throws SQLException {
		String query = "SELECT DISTINCT campaign_vessel.ID_Campaign as id FROM (image JOIN campaign_vessel JOIN campaign) WHERE (campaign.ID = campaign_vessel.ID_Campaign AND campaign.status=0 AND campaign_vessel.ID_Vessel = image.ID_Vessel AND campaign_vessel.ID_Campaign=?);";

		PreparedStatement pStatement = null;
		ResultSet result = null;

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, id);

		result = pStatement.executeQuery();

		if(result.next()) {
			result.close();
			return true;
		}
		else {
			result.close();
			return false;
		}
	}


	public Status getCampaignStatus(Integer idc) throws SQLException{

		ResultSet results = null;
		PreparedStatement pStatement= null;
		String query = "SELECT status FROM campaign WHERE ID=?";


		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, idc);

			results = pStatement.executeQuery();

			Status status = null;
			while(results.next()) {

				status = Status.values()[results.getInt("status")];


			}

			return status;

		}catch(SQLException e){
			throw new SQLException(e); //  TODO implementare un'interfaccia migliore
		}
	}


	public int isUserTheCampaignOwner(int idc, int userID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM campaign WHERE (ID=? AND manager=?);";
		ResultSet result = null;
		PreparedStatement pStatement = null;

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idc);
		pStatement.setInt(2, userID);


		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");

		result.close();		

		return number;
	}
	
	public int isUserAssociatedWithCampaign(int idc, int userID) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM player_campaign WHERE (ID_Campaign=? AND ID_User=?);";
		ResultSet result = null;
		PreparedStatement pStatement = null;

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idc);
		pStatement.setInt(2, userID);


		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");

		result.close();		

		return number;
	}
	
	public int areThereLocationsInThisCampaign(int idc) throws SQLException {

		int number = 0;
		String query = "SELECT COUNT(*) AS number FROM campaign_vessel WHERE (ID_Campaign = ?);";
		ResultSet result = null;
		PreparedStatement pStatement = null;

		pStatement = connection.prepareStatement(query);
		pStatement.setInt(1, idc);


		result = pStatement.executeQuery();
		result.next();
		number = result.getInt("number");

		result.close();		

		return number;
	}
}

