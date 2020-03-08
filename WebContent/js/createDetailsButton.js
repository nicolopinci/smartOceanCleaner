/** This JS script is used to create the button that goes back to the details page.
 * The button is shown on the map page.
 */

window.addEventListener("load", createDetailsButton, false);

function createDetailsButton() {
	var myBody = document.getElementsByTagName("body")[0];
    var url = new URL(location.href);
    var idc = url.searchParams.get("idc");
    
	var detailsButton = document.createElement("a");
	detailsButton.href="GoToDetailsPage?idc="+idc;
	detailsButton.className="float";
	detailsButton.id="map_float";

	var detailsIcon = document.createElement("img");
	detailsIcon.src="css/img/details.png";
	detailsIcon.title = "Details";

	detailsButton.appendChild(detailsIcon);
	myBody.appendChild(detailsButton);
}