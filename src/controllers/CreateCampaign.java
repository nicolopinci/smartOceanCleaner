package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CampaignDAO;
import dto.LoginResult;
import utils.TypeOfUser;

/* This servlet allows the creation of a campaign. A campaign might be
 * created by a manager and its status is 'created'.
 */

@WebServlet("/CreateCampaign")
public class CreateCampaign extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CreateCampaign() {
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


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(405);
		return;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* Session control: it is always the same! */
		String loginpath = getServletContext().getContextPath() + "/login.html";
		LoginResult lr = null;

		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			lr = (LoginResult) s.getAttribute("user");
			if (!lr.getUserType().equals(TypeOfUser.manager)) { // the user MUST be a manager
				response.sendRedirect(loginpath);
				return;
			}
		}
		/* End of session control */

		String cName = request.getParameter("campaign_name"); // Gets the campaign name from the form...
		String cClient = request.getParameter("campaign_client"); // ... and also the client

		String path = getServletContext().getContextPath();

		if(cName=="" || cClient=="") { // one of the fields is empty
			response.sendError(400);
			return;
		}
		else
		{
			CampaignDAO cDAO = new CampaignDAO(connection); // create a new campaign DAO object


			try {
				int idc = cDAO.createCampaign(cName, cClient, lr.getId()); // in order to create a campaign you have to insert its name and its client. You (logged admin) are its owner.
				// idc is needed in order to be redirected to the campaign details without other queries
				path += "/GoToDetailsPage?idc="+idc; // this is the path for the details

			} catch (SQLException e) { // we have a problem here: show an error page!
				response.sendError(500);
				return;
			}
		}
		response.sendRedirect(path);	
	}

}
