/**
 * This JS script is used to show a modal window containing the image over which
 * the user has clicked. The image in the modal window will be bigger then the one
 * shown in the image list, in case the user wants to see it more in detail.
 */

window.addEventListener("load", addImageListeners, false);
document.addEventListener("keydown", escHide, false);


function addImageListeners() {
	var allImages = document.getElementsByTagName("img");
	for(var i=0; i<allImages.length; ++i) {
		var parent = allImages[i].parentNode;
		if(parent.id!=='logout_float' && parent.id!=='map_float' && parent.id!=='stat_float' && parent.id!=='editUser_float') {
			allImages[i].addEventListener("click", enlarge(allImages[i]), false);
		}
	}
}


function enlarge(image) {
	return function() {

		var myBody = document.getElementsByTagName("body")[0];

		hideAll();

		modalDiv = document.createElement("div");
		modalDiv.id = "myModal";
		modalDiv.className = "modal";

		closeButton = document.createElement("span");
		closeButton.className = "close";
		closeButton.appendChild(document.createTextNode("×"));
		closeButton.style.zIndex = "100";

		myImage = document.createElement("img");
		myImage.className = "modal-content";
		myImage.id = "modalImg";
		myImage.style.zIndex = "100";

		modalDiv.appendChild(closeButton);
		modalDiv.appendChild(myImage);
		myBody.appendChild(modalDiv);

		modalDiv.style.display = "block";
		myImage.src = image.src;

		closeButton.addEventListener("click", hideAll, false);
	}
}

function escHide(e) { // You might exit from the modal window using the Escape key
	if(e.key === "Escape") {
		hideAll();
	}
}
function hideAll() { 
	try {
		document.getElementById("myModal").remove();
	}
	catch (Exception) {

	}}