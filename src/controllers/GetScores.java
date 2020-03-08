package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CampaignDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dao.UserDAO;
import dto.LoginResult;
import utils.Status;
import utils.TypeOfUser;

import org.json.JSONObject;


@WebServlet("/GetScores")
public class GetScores extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String filePath = null;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			filePath = getServletContext().getInitParameter("file-upload"); 

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
		LoginResult lr = null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			lr = (LoginResult) s.getAttribute("user");
		}
		/* End of session control */


		UserDAO uDAO = new UserDAO(connection);
		Integer points = 0;
		Integer percentage = 0;
		
		try {
			if(lr.getUserType().equals(TypeOfUser.worker)) {
				points = uDAO.getProfile(lr.getId()).getPoints();
				percentage = uDAO.getProfile(lr.getId()).getPercentage();
			}
			
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();
		
		json.put("points", points);
		json.put("percentage", percentage);
				
		response.getWriter().write(json.toString());
	}


	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {}
	}
}