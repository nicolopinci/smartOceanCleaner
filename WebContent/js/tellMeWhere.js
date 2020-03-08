/* 
 * Show info about the location at the current position (lat, lon). This script
 * does NOT perform a check because nominatim is not always 100% precise and because
 * some places might be "unmapped".
 */


document.getElementById("latitude").addEventListener("change", loadInfo, false);
document.getElementById("longitude").addEventListener("change", loadInfo, false);


function loadInfo() {
	var x = new XMLHttpRequest();
	x.onreadystatechange = showLoc;
	var lat = document.getElementById("latitude").value;
	var lon = document.getElementById("longitude").value;

	x.open("GET", "https://nominatim.openstreetmap.org/reverse?format=json&lat="+lat+"&lon="+lon+"&zoom=18&addressdetails=1");
	x.send();
}

function showLoc() {
	var newReq = true;
	if (this.readyState == 4) {
		if (this.status == 200) {
			if(newReq) {

			var locationDet = JSON.parse(this.responseText);
			var calcLocInfo = document.getElementById("calcLoc");

			calcLocInfo.innerHTML = "The location found at those coordinates is " + locationDet.display_name;
			calcLocInfo.style.padding = "30px";
			calcLocInfo.style.fontWeight = "bold";
			calcLocInfo.id = "calcLoc";
			
			newReq = false;
			}
		}
	}
}