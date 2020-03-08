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

@WebServlet("/StartCampaign")
public class StartCampaign extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public StartCampaign() {
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
		LoginResult lr = null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			lr = (LoginResult) s.getAttribute("user");
			if (!lr.getUserType().equals(TypeOfUser.manager)) {
				response.sendRedirect(loginpath);
				return;
			}
		}
		/* End of session control */

		Integer idc = Integer.parseInt(request.getParameter("idc")); // this is the campaign id
		CampaignDAO cDAO = new CampaignDAO(connection);

		try {
			if(!cDAO.isStartable(idc)) {
				response.sendError(500);
				return;
			}
		} catch (SQLException e1) {
			response.sendError(500);
			return;
		}

		try {
			cDAO.startCampaign(idc, lr.getId()); // try to start a campaign (only if you are its owner)
		} catch (SQLException e) {
			throw new ServletException(e);
		}

		response.sendRedirect("GoToDetailsPage?idc="+idc);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
