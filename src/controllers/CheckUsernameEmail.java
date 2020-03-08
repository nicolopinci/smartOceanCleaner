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

import org.json.JSONException;
import org.json.JSONObject;

import dao.UserDAO;

/* This servlet is used in order to show an error in case one 
 * tries to register using an existing username and e-mail address,
 * without having to reload the page. The result computed by this
 * servlet is sent using JSON format and is read thanks to a JS
 * function.
 * Other controls are performed afterwards: this servlet is used only
 * to show an user-friendly message before reloading the page.
 */

@WebServlet("/CheckUsernameEmail")
public class CheckUsernameEmail extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

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

		/* There is no session control since this servlet is used for registration,
		 * when there is no logged user yet.
		 */

		UserDAO uDAO = new UserDAO(connection);

		response.setContentType("application/json"); // the response will be in JSON format
		response.setCharacterEncoding("utf-8"); // utf-8 encoding
		JSONObject json = new JSONObject();

		try {

			/* Note: the query counts the number of equal emails and usernames, that might
			 * be greater than 1 in case the DB structure will be modified in the future for
			 * any reason. These queries, therefore, represent a more general situation with
			 * respect to queries which return a boolean result.
			 */

			json.put("email", uDAO.numberOfEqualEmails(request.getParameter("email"))); // how many emails are equal to my input email?
			json.put("username", uDAO.numberOfEqualUsernames(request.getParameter("username"))); // and how many usernames?

			// Those results will be evaluated through JS in order to give information without reloading the page

		} catch (JSONException | SQLException e) {
			response.sendError(500);
			return;
		}

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
