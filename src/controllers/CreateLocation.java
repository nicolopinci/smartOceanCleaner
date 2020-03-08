package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import beans.Image;
import beans.Location;
import dao.CampaignDAO;
import dao.ImageDAO;
import dao.LocationDAO;
import dto.LoginResult;
import utils.Resolution;
import utils.Status;
import utils.TypeOfUser;

/* This servlet is used to collect data from the image and location
 * multistep wizard, Several checks has to be performed:
 * - The fields are all compulsory, therefore they mustn't be empty
 * - Latitude and longitude have to be valid
 * - It mustn't be possible to insert a future date
 * - The uploaded file has to be an image
 * - Workers are not allowed to create a new location nor to add images
 * - A manager is not allowed to add images to a location he or she doesn't own
 * - It's forbidden to add images in case the status is not 'created'
 * - The image shouldn't be extremely large
 */

@MultipartConfig
@WebServlet("/CreateLocation")
public class CreateLocation extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	private String filePath;
	private int maxFileSize = 500 * 1024 * 1024;  // 500 MB
	private File file;

	public CreateLocation() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LoginResult sessionUser = null;
		String loginpath = getServletContext().getContextPath() + "/login.html";

		HttpSession session = request.getSession();	
		if(session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
		} else {

			sessionUser = (LoginResult) session.getAttribute("user");
			if(!sessionUser.getUserType().equals(TypeOfUser.manager))
			{
				response.sendRedirect(loginpath);
				return;
			}
		} 

		formElaboration(request, response, sessionUser);

	}

	private void formElaboration(HttpServletRequest request, HttpServletResponse response, LoginResult sessionUser) throws IOException
	{
		Integer idc = 0;
		Location location = new Location();
		Image image = new Image();
		LocationDAO locationDAO = new LocationDAO(connection);
		ImageDAO iDAO = new ImageDAO(connection);
		CampaignDAO cDAO = new CampaignDAO(connection);

		// A temporary file will be created.
		// The file will be moved in the appropriate directory afterwards with a predictable name.

		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		List<FileItem> fileItems = new ArrayList<FileItem>();

		FileItem imageItem = null; // the object that will contain the uploaded image
		String idLocation = "";

		java.util.Date date = null;

		/* fieldCounter's purpose is to count the fields in the
		 * form, in order to avoid errors (for instance in case
		 * the user manages to delete some of them from the HTML code)
		 */

		Integer fieldCounter = 0;

		Boolean def = false; // def is true if the default option ('Create a new location' is selected, otherwise it's false)

		try {
			fileItems = upload.parseRequest(request); // reads the request, elaborated on the basis of the form's content

			for(FileItem item : fileItems) { // for every item (every field of the form is a FileItem, since the form is multipart)
				if(!item.isFormField()) { // if the item is not a form field
					if(isImage(item)) { // if the item is an image
						imageItem = item; // then this is imageItem!
						++fieldCounter; // this was a field, therefore it has to be counted
					}
				}
				else // the item is a form field (e.g. text, number, select...)
				{
					String fieldName = item.getFieldName(); // this is its name (attribute name in HTML)
					String fieldValue = item.getString(); // and this is its value

					if(fieldValue.contentEquals("")) { // if the field is empty...
						response.sendError(400); //... error
						return;
					}

					if(fieldName.contentEquals("existingLocation")) { // field for location selection

						++fieldCounter; // increment the number of the fields
						idLocation = fieldValue; // its value is the location id
						if(!fieldValue.contentEquals("def")) // if you don't want to create a new location
						{
							if(locationDAO.isUserTheLocationOwner(Integer.parseInt(fieldValue), sessionUser.getId())!=1) { // verify that the user is the location owner
								response.sendError(400); // otherwise... error
								return;
							}	
							location.setID(Integer.parseInt(fieldValue)); // this is the locatio id
						}
						else {
							def = true; // you want to create a new location
						}
					}
					else if(fieldName.contentEquals("location_name")) { // Get the name
						location.setName(fieldValue);
						++fieldCounter;
					}
					else if(fieldName.contentEquals("city")) { // Get the city
						location.setCity(fieldValue);
						++fieldCounter;
					}
					else if(fieldName.contentEquals("region")) { // Get the region
						location.setRegion(fieldValue);
						++fieldCounter;
					}
					else if(fieldName.contentEquals("latitude")) { // Get the latitude
						location.setLatitude(fieldValue);
						++fieldCounter;

						// Note: since the latitude is a number and 
						// its values are limited, you should verify whether the inserted value is ok

						if(fieldValue.contentEquals("") || Float.parseFloat(fieldValue)>90 || Float.parseFloat(fieldValue)<-90) {
							response.sendError(400);
							return;
						}
					}

					else if(fieldName.contentEquals("longitude")) { // Get the longitude
						location.setLongitude(fieldValue);
						++fieldCounter;

						// Note: since the longitude is a number and 
						// its values are limited, you should verify whether the inserted value is ok
						if(fieldValue.contentEquals("") || Float.parseFloat(fieldValue)>180 || Float.parseFloat(fieldValue)<-180) {
							response.sendError(400);
							return;
						}

					}

					else if(fieldName.contentEquals("source")) { // Get source
						image.setSource(fieldValue);
						++fieldCounter;
					}

					else if(fieldName.contentEquals("date")) { // Get date
						++fieldCounter;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // conversion to string
						try {
							date = sdf.parse(fieldValue); // the field contains a string, that MUST be parsed appropriately
							image.setDate(new java.sql.Date(date.getTime())); // this date refers to the image
						} catch (Exception e) { // there has been an exception
							response.sendError(400); // show error
							return;
						}
					}

					else if(fieldName.contentEquals("resolution")) { // Get resolution
						image.setResolution(Resolution.valueOf(fieldValue)); // The string MUST be converted in Resolution to be inserted
						++fieldCounter;

						// Note: if the content is not low, standard or high there is a problem!
						if(!fieldValue.contentEquals("low") && !fieldValue.contentEquals("standard") && !fieldValue.contentEquals("high")) {
							response.sendError(400); // show error
							return;
						}
					}

					else if(fieldName.contentEquals("idc")) { // This is an hidden field
						idc = Integer.parseInt(fieldValue); // and MUST be converted in Integer
						++fieldCounter;

						// Check if I am allowed to perform this operation (an user might change this field value in HTML code)
						if(cDAO.isUserTheCampaignOwner(idc, sessionUser.getId())==0 || cDAO.getCampaignStatus(idc).equals(Status.closed)) {
							response.sendError(400); // show error
							return;
						}
					}
				}
			}

			if(fieldCounter!=11) { // The user has deleted one field or there has been another problem
				response.sendError(400); // show error
				return;
			}

			if(imageItem == null) { // The user hasn't uploaded the image, but it's quite fundamental to upload it
				response.sendError(400); // show error
				return;
			}

			java.util.Date todayDate = new java.util.Date(); // today

			if(date.after(todayDate)) { // You cannot travel back in time to upload pictures before having taken them!
				response.sendError(400); // show error
				return;
			}

			if(def) {
				location.setID(locationDAO.createLocation(location, idc));
				image.setLocation_id(location.getID());
			}
			else
			{
				if(locationDAO.isThisLocationInThisCampaign(location.getID(), idc)!=0) { // verify the location is in my campaign
					// (the user might have modified the HTML code)
					image.setLocation_id(Integer.parseInt(idLocation));
				}
				else
				{
					response.sendError(500); // show error
					return;
				}
			}

			Integer imageID = iDAO.addImage(image);
			String fileName = imageID.toString();

			// Write the file
			if(fileName.lastIndexOf("\\") >= 0 ) {
				file = new File(filePath + fileName.substring( fileName.lastIndexOf("\\"))) ;
			} else {
				file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\")+1)) ;
			}
			imageItem.write(file) ;

		} catch (FileUploadException e1) {
			
			response.sendError(400);
			return;
		} catch (Exception e1) {

			response.sendError(400);
			return;
		}

		response.sendRedirect("GoToDetailsPage?idc="+idc); // everything went well: show me the details of the campaign
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