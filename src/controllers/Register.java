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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import dao.LoginDAO;

@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	private String filePath;
	private int maxFileSize = 500 * 1024 * 1024;  // 500 MB
	private File file ;

	public Register() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {

		formElaboration(request, response);

	}

	static boolean isValid(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(regex);
	}


	private void formElaboration(HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		String path = getServletContext().getContextPath();


		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);


		List<FileItem> fileItems = new ArrayList<FileItem>();

		FileItem imageItem = null;
		Integer userType = 0;
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
						System.out.println("Empty field");
						response.sendError(401);
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
					else if(fieldName.contentEquals("pwd_check")) {
						repeat = fieldValue;
						++fieldCounter;

						if(!repeat.contentEquals(password)) {
							System.out.println("Password don't match");
							response.sendError(401);
							return;
						}
					}
					else if(fieldName.contentEquals("email")) {
						email = fieldValue;
						++fieldCounter;

						if(!isValid(email)) {
							System.out.println("The mail is not valid");
							response.sendError(401);
							return;
						}
					}
					else if(fieldName.contentEquals("radios")) {
						userType = Integer.parseInt(fieldValue);
						if(userType<0 || userType>1) {
							System.out.println("Wrong user type");
							response.sendError(401);
							return;
						}
						++fieldCounter;
					}
				}
			}

			if(fieldCounter!=5) {
				System.out.println("Wrong field counter");
				response.sendError(401);
				return;
			}
			
			if(imageItem == null && userType == 0) {
				System.out.println("No image");
				response.sendError(401);
				return;
			}

			int userID=0;
			LoginDAO lDAO = new LoginDAO(connection);
			try {
				userID = lDAO.register(username, password, email, userType);
				path += "/login.html";

			} catch (SQLException e) { // Unluckily there has been an error...
				System.out.println("SQL Exception");
				response.sendError(401);
				return;
			}

			if(userType == 0) {

				String fileName = "profile"+userID;

				// Write the file
				if(fileName.lastIndexOf("\\") >= 0 ) {
					file = new File(filePath + fileName.substring( fileName.lastIndexOf("\\"))) ;
				} else {

					file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\")+1)) ;
				}
				imageItem.write(file) ;
			}
		} catch (FileUploadException e1) {
			response.sendError(401);
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
