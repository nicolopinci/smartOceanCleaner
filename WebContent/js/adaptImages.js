/**
 * This JS script's purpose is to change the size of the images in order to dinamically adapt
 * them to their container. This happens on loading and when the window is resized.
 * The image's style is defined in the .css files.
 */

document.addEventListener("DOMContentLoaded", startChanging, false);
window.addEventListener("resize", startChanging, false);

function startChanging() {
	
	var imagesList = document.getElementsByClassName("leftImage");

	for(var i=0; i<imagesList.length; ++i) {
		var parentDiv = imagesList[i].parentNode;
		var parentStyle = window.getComputedStyle(parentDiv);

		// Image adaptation
		var divHeight = parentStyle.height;

		maxHeightPix = parseInt(divHeight) - 20;
		imagesList[i].style.maxHeight = maxHeightPix + "px";	

	}
}
