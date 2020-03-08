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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.AnnotationDAO;
import dao.CampaignDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.Status;
import utils.TypeOfUser;

/* The purpose of this servlet is to go to the annotation page.
 * Only workers are allowed to annotate images, therefore it
 * is necessary to check the user type before loading the page
 * content.
 */

@WebServlet("/GoToAnnotationPage")
public class GoToAnnotationPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToAnnotationPage() {
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
		} else {
			lr = (LoginResult) s.getAttribute("user");
			if (!lr.getUserType().equals(TypeOfUser.worker)) {
				response.sendRedirect(loginpath);
				return;
			}
		}
		/* End of session control */			

		Integer idi = Integer.parseInt(request.getParameter("idi")); // this is the image id
		String path = "/WEB-INF/annotate.html";
		Integer idl = null;

		ImageDAO iDAO = new ImageDAO(connection);
		CampaignDAO cDAO = new CampaignDAO(connection);
		LocationDAO lDAO = new LocationDAO(connection);
		AnnotationDAO aDAO = new AnnotationDAO(connection);

		try {
			idl = iDAO.locationOfImage(idi); // get the location hat contains he image

			Integer idc = lDAO.campaignOfLocation(idl); // get the campaign that contains the location
			if(cDAO.isUserAssociatedWithCampaign(idc, lr.getId())==0 || !cDAO.getCampaignStatus(idc).equals(Status.started)) { // verify association and status
				response.sendError(403);
				return;
			}
			
			if(aDAO.hasUserAlreadyAnnotated(idi, lr.getId())>0) { // has THIS user already annotated THIS image?
				response.sendError(403);
				return;
			}
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		ctx.setVariable("idi", idi);

		templateEngine.process(path, ctx, response.getWriter());
	}
}