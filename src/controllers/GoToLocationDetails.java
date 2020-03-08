package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Annotation;
import beans.Image;
import beans.Location;
import beans.User;
import dao.AnnotationDAO;
import dao.CampaignDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.Status;
import utils.TypeOfUser;

@WebServlet("/GoToLocationDetails")
public class GoToLocationDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToLocationDetails() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
		}
		/* End of session control */			

		lr = (LoginResult) s.getAttribute("user");

		Integer idl = Integer.parseInt(request.getParameter("idl")); // this is the location id

		String path = "/WEB-INF/location_details.html";

		LocationDAO lDAO = new LocationDAO(connection);
		AnnotationDAO aDAO = new AnnotationDAO(connection);
		ImageDAO iDAO = new ImageDAO(connection);
		CampaignDAO cDAO = new CampaignDAO(connection);

		Location info = new Location();
		Map<Image, Map<Annotation, User>> imAnUs = new HashMap<Image, Map<Annotation, User>>();
		Map<Image, Integer> imNumAn = new HashMap<Image, Integer>();

		List<Image> imageList = new ArrayList<Image>();
		
		try {

			if(lr.getUserType().equals(TypeOfUser.worker)) {
				Integer idc = lDAO.campaignOfLocation(idl);
				if(cDAO.isUserAssociatedWithCampaign(idc, lr.getId())==0 || !cDAO.getCampaignStatus(idc).equals(Status.started)) {
					response.sendError(403);
					return;
				}
			}
			else {
				if(lDAO.isUserTheLocationOwner(idl, lr.getId())==0) {
					response.sendError(403);
					return;
				}
			}

			info = lDAO.getLocationInfo(idl);
			imageList = iDAO.getImagesByLocationId(idl);

			for(Image image : imageList) {
				
				if(lr.getUserType().equals(TypeOfUser.manager)) {
					imAnUs.put(image, aDAO.getAnnotationsByImageID(image.getID()));
				}
				else
				{
					Integer numberRobotAnnotations = aDAO.hasRobotAnnotated(image.getID());
					
					if(numberRobotAnnotations != 0) {
						imNumAn.put(image, aDAO.hasUserAlreadyAnnotated(image.getID(), lr.getId()));
					}
				}
			}
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		ctx.setVariable("info", info);
		ctx.setVariable("isAdmin", lr.getUserType().equals(TypeOfUser.manager));
		ctx.setVariable("imAnUs", imAnUs);
		ctx.setVariable("imNumAn", imNumAn);

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}