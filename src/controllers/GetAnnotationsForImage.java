package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import beans.Annotation;
import beans.User;
import dao.AnnotationDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.TypeOfUser;

/* This servlet is used to get annotations for an image owned by
 * a manager. The result is sent using the JSON format and it will
 * be read using JavaScript.
 * Only the location creator will be able to see its images and,
 * consequently, the related annotations.
 * This servlet is used in order to get the results in case of
 * conflicts, so that the request is made only when the manager
 * clicks on an image, without having to load all the annotations
 * when the page is shown.
 */

@WebServlet("/GetAnnotationsForImage")
public class GetAnnotationsForImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetAnnotationsForImage() {
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
		lr = (LoginResult) s.getAttribute("user");
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		else if(!lr.getUserType().equals(TypeOfUser.manager)) { // Only the manager should see the annotations related to his images
			response.sendError(403);
			return;
		}
		/* End of session control */			

		Integer idi = Integer.parseInt(request.getParameter("idi")); // this is the image id

		LocationDAO lDAO = new LocationDAO(connection);
		AnnotationDAO aDAO = new AnnotationDAO(connection);
		ImageDAO iDAO = new ImageDAO(connection);

		Map<String, Annotation> usAn = new HashMap<String, Annotation>();
		Map<Annotation, User> annotUser = new HashMap<Annotation, User>();


		try {
			annotUser = aDAO.getAnnotationsByImageID(idi); // Annotations associated with this image
			Integer idl = iDAO.locationOfImage(idi); // Location associated to this image
			
			if(lDAO.isUserTheLocationOwner(idl, lr.getId())==0) { // verify whether the location is owned by the manager
				response.sendError(403);
				return;
			}

			if(lr.getUserType().equals(TypeOfUser.manager)) {
				for (Map.Entry<Annotation, User> entry : annotUser.entrySet()) {
					usAn.put(entry.getValue().getUsername(), entry.getKey()); // prepare the structure for JSON
					// Note: passwords and email addresses are not sent, sine they might be considered personal data
					// and they are not shown
				}
			}
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();

		json.put("annotations", usAn);

		response.getWriter().write(json.toString());
	}
}
