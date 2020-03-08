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

import dao.UserDAO;
import dto.LoginResult;
import utils.TypeOfUser;

@WebServlet("/GoToEditPage")
public class GoToEditPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToEditPage() {
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
		} else {
			lr = (LoginResult) s.getAttribute("user");
			if (!lr.getUserType().equals(TypeOfUser.manager) && !lr.getUserType().equals(TypeOfUser.worker)) { // are you a user?
				response.sendRedirect(loginpath); // you are not a user. Do you want to become a user?
				return;
			}
		}


		// Are you a worker? Go to the edit page in worker mode. Otherwise go to the edit page in manager mode.
		String path = "/WEB-INF/editprofile.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		UserDAO uDAO = new UserDAO(connection);

		try {
			ctx.setVariable("username", uDAO.getProfile(lr.getId()).getUsername());
			ctx.setVariable("email", uDAO.getProfile(lr.getId()).getEmail());
			ctx.setVariable("password", uDAO.getProfile(lr.getId()).getPassword());


		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(lr.getUserType().equals(TypeOfUser.worker)) {
			ctx.setVariable("isWorker", 1);
		}
		else if(lr.getUserType().equals(TypeOfUser.manager)) {
			ctx.setVariable("isWorker", 0);
		}

		templateEngine.process(path, ctx, response.getWriter());	
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}