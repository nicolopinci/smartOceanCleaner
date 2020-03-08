package controllers;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import dao.LoginDAO;
import dto.LoginResult;
import utils.TypeOfUser;

/* This servlet is used to check whether the credentials for login exist in the DB */

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public CheckLogin() {
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
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/* Gets username and password from the login form */
		String username = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		
		// Creates a new login object
		LoginDAO lDAO = new LoginDAO(connection);
		
		LoginResult result = null;
		
		try {
			result = lDAO.login(username, pwd); // try to login (check if credentials are ok)
		} catch (SQLException e) { // credentials are not ok or there has been another problem
			throw new ServletException(e);
		}
		String path = getServletContext().getContextPath(); // current context path
		
		if (result == null || result.getId()== null || result.getUsername() == null || result.getUserType()==null) { // if the result is null or there's any other user related problem shows error...
			//path = getServletContext().getContextPath() + "/GoToLoginError";
			response.sendError(401);
			return;
		} else {
			request.getSession().setAttribute("user", result); // ...otherwise sets session attribute
			
			String target = null;
			
			if(result.getUserType().equals(TypeOfUser.manager)) { // if the user is a manager...
				 target = "/GoToHomeManager"; // ... this is the right servlet
			}
			else if(result.getUserType().equals(TypeOfUser.worker)) { // if the user is a worker...
				 target = "/GoToHomeWorker"; // ... this is the right servlet
			}

			path = path + target;
		}
		response.sendRedirect(path);
		return;
	}
}
