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

import dao.CampaignDAO;
import dto.LoginResult;
import utils.Status;
import utils.TypeOfUser;

@WebServlet("/Map")
public class Map extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public Map() {
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

		String loginpath = getServletContext().getContextPath() + "/login.html";
		LoginResult lr = null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		lr = (LoginResult) s.getAttribute("user");
		Integer idc = Integer.parseInt(request.getParameter("idc"));
		CampaignDAO cDAO = new CampaignDAO(connection);

		Status status = Status.closed;

		try {
			status = cDAO.getCampaignStatus(idc);
		} catch (NumberFormatException e) {
			response.sendError(400);
			return;
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}

		try {
			if(cDAO.isUserTheCampaignOwner(idc, lr.getId())==0 && lr.getUserType().equals(TypeOfUser.manager)) {
				response.sendError(500);
				return;
			}
			
			if(cDAO.isUserAssociatedWithCampaign(idc, lr.getId())==0 && lr.getUserType().equals(TypeOfUser.worker)) {
				response.sendError(500);
				return;
			}
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}

		if(status.equals(Status.started) || lr.getUserType().equals(TypeOfUser.manager)) {
			String path = "/WEB-INF/map.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());	
		}
		else
		{
			response.sendError(500);
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}