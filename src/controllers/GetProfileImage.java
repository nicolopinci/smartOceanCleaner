package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dto.LoginResult;

/* The images are collected from the folder where they are.
 * Note: images are not stored INSIDE the DB as blobs
 * (see https://www.quora.com/What-are-the-pros-and-cons-of-storing-images-in-a-sql-database)
 * The main reasons why it's better to avoid storing images
 * in the DB are the following:
 * - Easier backup (when an image is stored outside the DB
 * it is easy to save it in more different locations)
 * - DB is smaller and, therefore, it might be contained in less
 * hardware nodes
 * - It is possible to decentralize data, saving images on a node and
 * other information on another node, somewhere else.
 * 
 * Differently from the other images, profile images' name
 * has the following structure: profile + id, where id represents
 * the worker id.
 */

@WebServlet("/GetProfileImage")
public class GetProfileImage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String filePath = null;

	public void init() throws ServletException {

		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			filePath = getServletContext().getInitParameter("file-upload"); 

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

		response.setContentType("image/jpeg");  
		ServletOutputStream out;  
		out = response.getOutputStream();  

		FileInputStream fin = null;

		Integer idu = lr.getId();

		try {
			fin = new FileInputStream(filePath + "profile" + idu);  
			BufferedInputStream bin = new BufferedInputStream(fin);  
			BufferedOutputStream bout = new BufferedOutputStream(out);  
			int ch =0; ;  

			while((ch=bin.read())!=-1)  
			{  
				bout.write(ch);  
			}  

			bin.close();  
			fin.close();  
			bout.close();  
			out.close();  
		}
		catch (Exception e) {
			response.setContentType("html");
			response.sendError(404);
			return;
		}

	}



	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {}
	}
}
