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

/* This servlet allows the association of the campaign with a user in case
 * all the conditions are respected (campaign is started, campaign hasn't been
 * chosen before, user is not a manager, but it is a worker, the campaign exists).
 */

@WebServlet("/AssociateCampaign")
public class AssociateCampaign extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public AssociateCampaign() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		/* Session control */
		String loginpath = getServletContext().getContextPath() + "/login.html";
		LoginResult lr = null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			lr = (LoginResult) s.getAttribute("user");
			if (!lr.getUserType().equals(TypeOfUser.worker)) { // A campaign might be associated only to a worker (and owned by a manager)
				response.sendRedirect(loginpath); // If you are not a worker try to login again
				return;
			}
		}
		/* End of session control */

		Integer idc = Integer.parseInt(request.getParameter("idc")); // This is the campaign id
		Integer idu = lr.getId(); // and this is the user (worker!) id, from the session

		CampaignDAO cDAO = new CampaignDAO(connection);

		Integer isAvailable = 0; // default: the campaign is not available

		try {
			isAvailable = cDAO.isAvailable(idu, idc); // look in the DB to see whether the campaign is actually available
			if(isAvailable == 0) { // if it isn't
				response.sendError(403); // error
				return;
			}
		} catch (SQLException e) { // unluckily there has been an exception...
			response.sendError(403); // show an error
			return;
		}

		try {
			if(isAvailable!=0) { // if the campaign is actually available
				cDAO.associateWithCampaign(idu, idc); // associate THIS user with THIS campaign
			}
		} catch (SQLException e) { // unluckily there has been an exception
			response.sendError(403); // show an error
			return;
		}		

		if(isAvailable!=0) { // if the campaign is available
			response.sendRedirect("Map?idc="+idc); // show the campaign map (servlet map) of THIS campaign
		}
		return;

	}


}
