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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Campaign;
import dao.CampaignDAO;
import dto.LoginResult;
import utils.TypeOfUser;

@WebServlet("/GoToHomeWorker")
public class GoToHomeWorker extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GoToHomeWorker() {
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
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Session control
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
		
		CampaignDAO cDAO = new CampaignDAO(connection);
		List<Campaign> availableCampaigns = new ArrayList<Campaign>();
		List<Campaign> completedCampaigns = new ArrayList<Campaign>();

		try {
			availableCampaigns = cDAO.getAvailableCampaigns(lr.getId());
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}
		
		try {
			completedCampaigns = cDAO.getCompletedCampaigns(lr.getId());
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}
		
		// Redirect to worker page in case session control was successful
		String path = "/WEB-INF/workerpage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("available", availableCampaigns);
		ctx.setVariable("completed", completedCampaigns);

		templateEngine.process(path, ctx, response.getWriter());	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}