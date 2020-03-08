package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Location;
import dao.CampaignDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.TypeOfUser;

import org.json.JSONObject;

@WebServlet("/GoToMap")
public class GoToMap extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GoToMap() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Session control */
		String loginpath = getServletContext().getContextPath() + "/login.html";
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} 
		/* End of session control */

		Integer idc = Integer.parseInt(request.getParameter("idc")); // this is the campaign id
		CampaignDAO cDAO = new CampaignDAO(connection);
		
		try {
			if(cDAO.isUserTheCampaignOwner(idc, ((LoginResult) s.getAttribute("user")).getId())==0 && ((LoginResult) s.getAttribute("user")).getUserType().equals(TypeOfUser.manager)) {
				response.sendError(403);
				return;
			}
			if(cDAO.isUserAssociatedWithCampaign(idc, ((LoginResult) s.getAttribute("user")).getId())==0 && ((LoginResult) s.getAttribute("user")).getUserType().equals(TypeOfUser.worker)) {
				response.sendError(403);
				return;
			}
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}
		
		LocationDAO locDao = new LocationDAO(connection);
		List<List<Location>> locationsLists = null;
		try {
			locationsLists = locDao.getLocationsByCampaign(idc);	
		}catch(SQLException e) {
			response.sendError(500);
			return;
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();

		if(((LoginResult) s.getAttribute("user")).getUserType().equals(TypeOfUser.manager)) {	

			json.put("green", locationsLists.get(0));
			json.put("yellow", locationsLists.get(1));
			json.put("red", locationsLists.get(2));		
			json.put("black", "");
		}
		else {
			List<Location> uncoloredLocations = new ArrayList<Location>();
			for(int i=0; i<3;++i) {
				uncoloredLocations.addAll(locationsLists.get(i));
			}
			json.put("green", "");
			json.put("yellow", "");
			json.put("red", "");

			json.put("black", uncoloredLocations); // we don't want to reveal the color differences to a common worker
		}

		response.getWriter().write(json.toString());

	}



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}
