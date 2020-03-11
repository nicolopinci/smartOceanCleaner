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

import beans.Annotation;
import beans.Image;
import dao.AnnotationDAO;
import dao.CampaignDAO;
import dao.ImageDAO;
import dao.UserDAO;
import dto.LoginResult;
import utils.Trust;
import utils.TypeOfUser;
import utils.Validity;

/* This servlet is used in order to allow workers to annotate images
 * in the campaigns they have joined, provided that they are allowed to annotate
 * every image only once and that the campaign status must be in the 'started' status.
 */

@WebServlet("/Annotate")
public class Annotate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;


	public Annotate() {
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
			if (!lr.getUserType().equals(TypeOfUser.worker)) { // Only the workers are allowed to access annotation creation servlet
				response.sendRedirect(loginpath); // Others are redirected to the login page
				return;
			}
		}
		/* End of session control */

		AnnotationDAO aDAO = new AnnotationDAO(connection);
		Integer isImageAcc = 0; // is the image accessible? (default: 0 = no)
		Integer hasAnn = 1; // is there any annotation for the image by this user? (default: 1 = yes)
		
		Integer idi = Integer.parseInt(request.getParameter("idi")); // this is the image identifier

		try {
			isImageAcc = aDAO.isImageAccessible(idi, lr.getId()); // the image is associated to the user through its campaign, that has been started
			hasAnn = aDAO.hasUserAlreadyAnnotated(idi, lr.getId()); // has THIS user already annotated THIS image?
		} catch (NumberFormatException e1) {
			response.sendError(400); // There is an exception: show an error page
			return; // Otherwise it would keep executing the code
		} catch (SQLException e1) {
			response.sendError(500); // There is an exception: show an error page
			return; // Otherwise it would keep executing the code
		}

		if(isImageAcc!=0 && hasAnn == 0) { // the image is accessible and the user hasn't already annotated it

			UserDAO uDAO = new UserDAO(connection);
			
			Integer userPercentage;
			Integer trustLevel = 0;

			
			try {
				userPercentage = uDAO.getProfile(lr.getId()).getPercentage();
				
				if(userPercentage >= 60 && userPercentage < 80) {
					trustLevel = 1;
				}
				
				if(userPercentage >= 80) {
					trustLevel = 2;
				}
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			
			Trust trust = Trust.convertInt(trustLevel); // Get trust level from the user percentage
			String notes = request.getParameter("notes"); // Get notes from the form

			Annotation annotation = new Annotation(); // New "empty" annotation

			try {
				java.util.Date date = new java.util.Date(); //today's date
				annotation.setDate(new java.sql.Date(date.getTime())); // The annotation's date is today's date
			}catch (Exception e) {
				response.sendError(500); // There has been a problem... show an error page
				return; // Otherwise it would keep executing the code
			}

			if(trust == null || notes == null || notes.contentEquals("")) { // not all the data has been inserted properly
				response.sendError(400); // error: bad request
				return;
				
			}
			// Fill the annotation
			annotation.setNotes(notes); // put the notes
			annotation.setTrust(trust); // put the trust level
			annotation.setUser(lr.getId()); // put the user id (from session)
			annotation.setImage(Integer.parseInt(request.getParameter("idi"))); // put the image (hidden field)

			// Generate points and percentage
			try {
				Integer points;
				points = uDAO.getProfile(lr.getId()).getPoints();
				Integer percentage = uDAO.getProfile(lr.getId()).getPercentage();
								
				Integer numberRobotAnnotations = aDAO.hasRobotAnnotated(idi);
				
				if(numberRobotAnnotations > 0) {
					
					points = points + percentage;
					
					Integer totAnnotations = aDAO.countAllAnnotations(annotation.getImage());
					Integer totMaterialAnnotations = aDAO.countMaterialAnnotations(annotation.getImage(), annotation.getNotes());
					
					Integer totUserAnnotations = aDAO.countUserAnnotations(lr.getId()) + 1;
					Integer currentScore = 0;
					
					if(totAnnotations <= 2*totMaterialAnnotations) {
						currentScore = 100;
					}
					
					percentage = (currentScore + percentage*totUserAnnotations)/(totUserAnnotations+1);

					uDAO.updatePercentage(lr.getId(), percentage);
					uDAO.updatePoints(lr.getId(), points);
					
				}
	
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Try to add the annotation
			try {
				aDAO.createAnnotation(annotation);

			} catch (SQLException e) { // Unluckily there has been an error...
				response.sendError(403); // There has been an error while creating the annotation
				return;
			}
		}
		else
		{
			response.sendError(403);
			return;
		}
		
		CampaignDAO cDAO = new CampaignDAO(connection);
		ImageDAO iDAO = new ImageDAO(connection);
		
		Integer selectedID = null;

		try {
			Integer cid = cDAO.selectCampaignByImageID(idi);
			List<Image> allCampaignImages = new ArrayList<Image>();
			
			allCampaignImages = iDAO.allImagesByCampaign(cid);
						
			for(Image img:allCampaignImages) {
				if(aDAO.hasRobotAnnotated(img.getID())!=0 && aDAO.hasUserAlreadyAnnotated(img.getID(), lr.getId())==0) {
					selectedID = img.getID();
					System.out.println(selectedID);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(selectedID != null) {
			response.sendRedirect("GoToAnnotationPage?idi=" + selectedID); // ok, now choose something else to do
		}
		else {
			response.sendRedirect("GoToHomeWorker");
		}
		return;
	}
}