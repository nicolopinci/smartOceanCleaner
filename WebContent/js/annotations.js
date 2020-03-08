/*	This JS script prepares the annotations related to an image, in order to show them.
 * It's called when a manager clicks on an image in the conflict list.
 */

stwAnnNS = function(){ // Namespace (according to W3C Best Practices)
  var x = null;
  return{
    x:x,
  }
}();

window.addEventListener("load", addAnnEvents, false); // On load

function addAnnEvents() { // Add a listener for every image with a conflict
	var statImages = document.getElementsByClassName("statImage");

	for(var i=0; i<statImages.length; ++i) { // create a listener passing the image
		statImages[i].addEventListener("click", addAnnotationsBox(statImages[i]), false);

	}
}

// Note: the passed variable would be hoisted. To solve this problem preserving compatibility with older
// browsers the following might be a possible solution:

function addAnnotationsBox(image) { // The function return the actual handler, that is an anonymous function
	return function() { // Source: https://stackoverflow.com/questions/10000083/javascript-event-handler-with-parameters
		
		// Request data (-> servlet)
		stwAnnNS.x = new XMLHttpRequest();
		stwAnnNS.x.onreadystatechange = createBox;
		// You only need the image number, not the rest of the source
		stwAnnNS.x.open("GET", "/smartOceanCleaner/GetAnnotationsForImage?idi="+image.src.split("=")[1]);
		stwAnnNS.x.send();
	}
}


function createBox() {
	var image = document.getElementById("modalImg");

	if (this.readyState == 4) {
		if (this.status == 200) {
			var annData = JSON.parse(this.responseText);
			image.style.float = "left";
			image.style.marginLeft = "5vw";
			image.style.maxWidth = "45vw";
			image.style.paddingBottom = "25px";
			var myModal = document.getElementById("myModal");
			var annList = document.createElement("div");
			annList.style.marginTop = "15px";
			annList.style.marginRight = "5vw";
			annList.style.float = "right";
			annList.style.width = "40vw";
			annList.overflow = "scroll";
			myModal.style.position = "absolute";
			myModal.style.height = "auto";
			
			for(var user in annData.annotations) {
				let username = document.createElement("p");
				username.innerHTML = "On " + annData.annotations[user].date + " " + user + " wrote: ";
				username.style.fontWeight = "bold";
				username.style.textAlign = "left";
				username.style.paddingLeft = "20px";
				username.style.whiteSpace = "normal";

				let ann = document.createElement("p");
				ann.innerHTML = annData.annotations[user].notes + "<br/> The image has been classified as " + annData.annotations[user].validity + " and its trust level is " + annData.annotations[user].trust + ".";
				ann.style.textAlign = "left";
				ann.style.paddingLeft = "20px";
				ann.style.whiteSpace = "normal";

				let newLine = document.createElement("br");

				annList.appendChild(username);
				annList.appendChild(newLine);
				annList.appendChild(newLine);

				annList.appendChild(ann);
				annList.appendChild(newLine);
				annList.appendChild(newLine);
			}
			myModal.appendChild(annList);
		}
	}
}