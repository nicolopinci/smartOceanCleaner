package controllers;

import java.io.File;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dao.UserDAO;
import dto.LoginResult;
import utils.TypeOfUser;

/* This servlet allows a user to edit its profile. The profile page will
 * be different on the basis of the user type. Workers are allowed to
 * upload a profile image, while managers are not.
 * The fields mustn't be empty, with the exception
 * of the image field: it might be left empty and the
 * current image will be maintained.
 */

@WebServlet("/EditProfile")
public class EditProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	private String filePath;
	private int maxFileSize = 500 * 1024 * 1024;  // 500 MB
	private File file ;

	public EditProfile() {
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
			filePath = getServletContext().getInitParameter("file-upload"); 


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

		HttpSession session = request.getSession();	
		LoginResult sessionUser = (LoginResult) session.getAttribute("user");

		String loginpath = getServletContext().getContextPath() + "/login.html";

		if(session.isNew() || sessionUser == null) {
			response.sendRedirect(loginpath);
		} 

		formElaboration(request, response, sessionUser.getId(), sessionUser.getUserType());

	}


	static boolean isValid(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(regex);
	}

	private void formElaboration(HttpServletRequest request, HttpServletResponse response, Integer id, TypeOfUser tou) throws IOException
	{
		String path = getServletContext().getContextPath();

		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		List<FileItem> fileItems = new ArrayList<FileItem>();

		FileItem imageItem = null;
		String username = "";
		String password = "";
		String repeat = "";
		String email = "";
		Integer fieldCounter = 0;

		try {
			fileItems = upload.parseRequest(request);

			for(FileItem item : fileItems) {
				if(!item.isFormField()) {
					if(isImage(item)) {
						imageItem = item;
					}
				}
				else {
					String fieldName = item.getFieldName();
					String fieldValue = item.getString();
					
					if(fieldValue.contentEquals("")) {
						response.sendError(400);
						return;
					}

					if(fieldName.contentEquals("username")) {
						username = fieldValue;
						++fieldCounter;
					}
					else if(fieldName.contentEquals("pwd")) {
						password = fieldValue;
						++fieldCounter;
					}
					else if(fieldName.contentEquals("repeat")) {
						repeat = fieldValue;
						++fieldCounter;

						if(!repeat.contentEquals(password)) {
							response.sendError(400);
							return;
						}
					}
					else if(fieldName.contentEquals("email")) {		
						email = fieldValue;
						++fieldCounter;

						if(!isValid(email)) { // The email MUST be formatted correctly
							response.sendError(400);
							return;
						}
					}		
				}
			}

			if(fieldCounter!=4) {
				response.sendError(400);
				return;
			}
			
			// If the image is null then the previous image is maintained

			UserDAO uDAO = new UserDAO(connection);

			try {
				uDAO.editProfile(id, username, password, email);
				path += "/Logout";
			} catch (SQLException e) { // Unluckily there has been an error...
				response.sendError(403);
				return;
			}

			if(tou.equals(TypeOfUser.worker) && imageItem != null) {

				String fileName = "profile"+id;

				// Write the file
				if(fileName.lastIndexOf("\\") >= 0 ) {
					file = new File(filePath + fileName.substring( fileName.lastIndexOf("\\"))) ;
				} else {

					file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\")+1)) ;
				}
				
				File newFile = null;
				
				try {
					newFile = file;
					file.delete();
					imageItem.write(newFile);
				}
				catch(Exception e) {
					imageItem.write(file) ;
				}	
			}
		} catch (FileUploadException e1) {
			response.sendError(400);
			return;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		response.sendRedirect(path);
	}

	private Boolean isImage(FileItem fi) { //returns true if FileItem is an image
		String mime = fi.getContentType();
		int slashPosition = mime.indexOf('/');
		String mimeType = mime.substring(0,slashPosition);
		if(mimeType.equals("image")) {
			return true;
		}
		return false;
	}
}