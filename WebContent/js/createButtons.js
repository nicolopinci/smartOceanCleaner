/* This JS script creates the buttons for logout and edit profile.
 * Moreover, it defines the creation of the modal window opened
 * in order to confirm logout.
 */

stwCrButtonNS = function(){ // Namespace
    var myBody = document.getElementsByTagName("body")[0];
  return{
	  myBody:myBody,
  }
}();

window.addEventListener("load", createButtons, false);

function createButtons() {
	
	// Logout button
	var logoutButton = document.createElement("a");
	logoutButton.href="Logout";
	logoutButton.className="float";
	logoutButton.id="logout_float";

	var logoutIcon = document.createElement("img");
	logoutIcon.src = "css/img/logout.svg";
	logoutIcon.title = "Logout";

	logoutButton.appendChild(logoutIcon);
	logoutButton.addEventListener("click", confirmLogout, "false");
	stwCrButtonNS.myBody.appendChild(logoutButton);

	// Edit profile button
	var editButton = document.createElement("a");
	editButton.href="GoToEditPage";
	editButton.className="float";
	editButton.id="editUser_float";

	var editIcon = document.createElement("img");
	editIcon.src = "css/img/user.svg";
	editButton.title = "Edit profile";

	editButton.appendChild(editIcon);
	stwCrButtonNS.myBody.appendChild(editButton);
}

function confirmLogout(event) {
	event.preventDefault(); // the default action is logout

	hideModal(); // hide every other modal window
	
	modalDiv = document.createElement("div");
	modalDiv.id = "myModal";
	modalDiv.className = "modal";

	confirmText = document.createElement("p");
	confirmText.innerHTML = "Do you really want to logout?";
	confirmText.id = "ctext";

	buttonYes = document.createElement("button");
	buttonYes.innerHTML = "Yes";
	buttonYes.className = "effettoripple mdc-button mdc-button--raised mdc-ripple-upgraded";
	buttonYes.id = "byes";
	buttonYes.style.backgroundColor = "rgba(0, 130, 0, 0.7)";

	buttonNo = document.createElement("button");
	buttonNo.innerHTML = "No";
	buttonNo.className = "effettoripple mdc-button mdc-button--raised mdc-ripple-upgraded";
	buttonNo.id = "bno";
	buttonNo.style.backgroundColor = "rgba(130, 0, 0, 0.7)";

	modalDiv.appendChild(confirmText);
	modalDiv.appendChild(buttonYes);
	modalDiv.appendChild(buttonNo);
	stwCrButtonNS.myBody.appendChild(modalDiv);

	modalDiv.style.display = "block";

	buttonYes.addEventListener("click", logoutConfirmed, false);
	buttonNo.addEventListener("click", hideModal, false);
}

function logoutConfirmed() {
	window.location.href = "Logout";
}

function hideModal() {
	try {
		document.getElementById("myModal").remove();
	}
	catch (Exception) {
		console.log("Error while removing the modal");
	}
}