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

import beans.Campaign;
import beans.Image;
import beans.Location;
import dao.CampaignDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.Status;
import utils.TypeOfUser;

@WebServlet("/GoToDetailsPage")
public class GoToDetailsPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToDetailsPage() {
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
			if (lr.getUserType().equals(TypeOfUser.worker)) {
				response.sendRedirect(getServletContext().getContextPath()+"/GoToHomeWorker");
				return;
			}
		}
		/* End of session control */

		Integer idc = Integer.parseInt(request.getParameter("idc")); // this is the campaign id
		Map<Location, List<Image>> locIm = new HashMap<Location, List<Image>>();

		CampaignDAO cDAO = new CampaignDAO(connection);
		LocationDAO lDAO = new LocationDAO(connection);
		ImageDAO iDAO = new ImageDAO(connection);

		Campaign info = new Campaign();

		try {
			info = cDAO.getCampaignInfo(idc, lr.getId()); // try to get campaign info (only if you are its owner)
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		
		// There are three lists of locations (green, yellow, red): the list of lists is kept
		// in order to facilitate a future extension in the future (for example in order to
		// show different lists on the basis of the color (color = number of conflicts)
		
		List<List<Location>> locationLists = null;  
		try {
			locationLists = lDAO.getLocationsByCampaign(idc); // lists of locations associated with this campaign
		} catch (SQLException e1) {
			response.sendError(500);
			return;
		}

		for(List<Location> locGroup : locationLists) {
			for(Location loc : locGroup) {
				List<Image> imageList = new ArrayList<Image>();
				try {
					imageList = iDAO.getImagesByLocationId(loc.getID());
				} catch (SQLException e) {
					response.sendError(500);
					return;
				}
				locIm.put(loc, imageList);
			}
		}

		if(info.getID()!=null) // you have tried to access a campaign and it is yours
		{
			String path = "/WEB-INF/campaign_details.html";

			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("info", info); // campaign info
			ctx.setVariable("showWizard", info.getStatus() != Status.closed); // show the wizard iff the status is created
			ctx.setVariable("allowClose", info.getStatus() == Status.started); // show the close button iff the status is started
			try {
				ctx.setVariable("startable", cDAO.isStartable(idc));
			} catch (SQLException e) {
				response.sendError(500);
				return;
			}
			ctx.setVariable("idc",idc); // campaign id
			ctx.setVariable("locIm", locIm); // map of the locations and their images

			templateEngine.process(path, ctx, response.getWriter());
		}
		else // you have tried to access a campaign, but that campaign isn't yours: go to your homepage!
		{
			response.sendRedirect("GoToHomeManager");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(405); // This method is not allowed
		return;
	}
}
