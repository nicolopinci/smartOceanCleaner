/** This button is used to go to the map page.
 */


window.addEventListener("load", createMapButton, false);

function createMapButton() {
	
	var myBody = document.getElementsByTagName("body")[0];
	var url = new URL(location.href);
	var idc = url.searchParams.get("idc");

	var mapButton = document.createElement("a");
	mapButton.href="Map?idc="+idc;
	mapButton.className="float";
	mapButton.id="map_float";

	var mapIcon = document.createElement("img");
	mapIcon.src="css/img/map.png";
	mapIcon.title = "Map";

	mapButton.appendChild(mapIcon);
	myBody.appendChild(mapButton);


}