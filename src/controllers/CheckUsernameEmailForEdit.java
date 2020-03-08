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

import org.json.JSONException;
import org.json.JSONObject;

import dao.UserDAO;
import dto.LoginResult;

/* This servlet is used in order to show an error in case one 
 * tries to edit its profile using an existing username and e-mail address,
 * without having to reload the page. The result computed by this
 * servlet is sent using JSON format and is read thanks to a JS
 * function.
 * Other controls are performed afterwards: this servlet is used only
 * to show an user-friendly message before reloading the page.
 */

@WebServlet("/CheckUsernameEmailForEdit")
public class CheckUsernameEmailForEdit extends HttpServlet{
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

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();

		/* This servlet is equal to the CheckUsernameEmail servlet. The only difference
		 * is that an email and a username are not considered already existent in case
		 * they refer to the current user.
		 * The info given by this server are used only to avoid reloading the page. Additional
		 * controls are performed once the data are submitted.
		 */


		try {

			/* Note: the query counts the number of equal emails and usernames, that might
			 * be greater than 1 in case the DB structure will be modified in the future for
			 * any reason. These queries, therefore, represent a more general situation with
			 * respect to queries which return a boolean result.
			 */

			json.put("email", uDAO.numberOfEqualEmailsButId(request.getParameter("email"), lr.getId()));
			json.put("username", uDAO.numberOfEqualUsernamesButId(request.getParameter("username"), lr.getId()));

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
