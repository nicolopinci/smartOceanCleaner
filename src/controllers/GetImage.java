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

import dao.CampaignDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.Status;
import utils.TypeOfUser;

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
 * The name of the image will be the id of the image itself
 */

@WebServlet("/GetImage")
public class GetImage extends HttpServlet{
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

		Integer idi = Integer.parseInt(request.getParameter("idi"));

		ImageDAO iDAO = new ImageDAO(connection);
		CampaignDAO cDAO = new CampaignDAO(connection);
		LocationDAO lDAO = new LocationDAO(connection);

		Integer idl = 0;
		try {

			idl = iDAO.locationOfImage(idi); // Get the location from the image
			
			// If an image is associated to the worker
			if(lr.getUserType().equals(TypeOfUser.worker)) {
				Integer idc = lDAO.campaignOfLocation(idl); // Get the campaign from the location
				if(cDAO.isUserAssociatedWithCampaign(idc, lr.getId())==0 || !cDAO.getCampaignStatus(idc).equals(Status.started)) {
					response.sendError(403);
					return;
				}
			}
			else { // if the manager is the image owner
				if(lDAO.isUserTheLocationOwner(idl, lr.getId())==0) {
					response.sendError(403);
					return;
				}
			}
		} catch (SQLException e) {
			response.sendError(500);
			return;
		}

		response.setContentType("image/jpeg");  
		ServletOutputStream out;  
		out = response.getOutputStream();  

		try {
			FileInputStream fin = new FileInputStream(filePath + idi);  // get the data from the image stored here

			BufferedInputStream bin = new BufferedInputStream(fin);  // the input stream is buffered
			BufferedOutputStream bout = new BufferedOutputStream(out);  // the output stream is buffered

			int ch = 0;

			while((ch = bin.read())!=-1)  // Read all the input
			{  
				bout.write(ch);  // write every piece of input on the output stream (-> the response)
			}  

			// Close the streams
			bin.close();  
			fin.close();  
			bout.close();  
			out.close();  
		} catch(Exception e) {
			response.setContentType("html");
			response.sendError(400);
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