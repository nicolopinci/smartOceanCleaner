/*
 * This JS script is used to show the map and the markers related to
 * the coordinates of the locations, with their popups.
 */

stwMapNS = function(){ // Namespace
	var mymap = L.map('mapid').setView([ 51.505, -0.09 ], 2);
	return{
		mymap:mymap,
	}
}();

window.addEventListener("load", loadLocations, false);

function loadLocations() {
	var x;

	x = new XMLHttpRequest();
	x.onreadystatechange = insertLocations;
	var url = new URL(window.location.href);
	var c = url.searchParams.get("idc");

	x.open("GET", "/smartOceanCleaner/GoToMap?idc="+c); // Map data (JSON format)

	x.send();
}

function insertLocations() {

	var first = true; // This is the first location inserted (used for the rectangle definition)

	// Define the tile layer (the token is necessary in order to request the tiles)
	L.tileLayer('https://api.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1Ijoibmljb2xvcGluY2kiLCJhIjoiY2p2djYyc2NvMXVtMjN6b3pybTF0bTJ3ayJ9.lP7ZhjJYbdLVXIWEvOPHgw',
			{
		attribution : 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		maxZoom : 18,
		id : 'mapbox.streets',
		accessToken : 'pk.eyJ1Ijoibmljb2xvcGluY2kiLCJhIjoiY2p2djYyc2NvMXVtMjN6b3pybTF0bTJ3ayJ9.lP7ZhjJYbdLVXIWEvOPHgw'
			}).addTo(stwMapNS.mymap);

	// Define the different icons (green, yellow, red and black)
	var greenIcon = new L.Icon(
			{
				iconUrl : 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
				shadowUrl : 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
				iconSize : [ 25, 41 ],
				iconAnchor : [ 12, 41 ],
				popupAnchor : [ 1, -34 ],
				shadowSize : [ 41, 41 ]
			});

	var yellowIcon = new L.Icon(
			{
				iconUrl : 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-yellow.png',
				shadowUrl : 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
				iconSize : [ 25, 41 ],
				iconAnchor : [ 12, 41 ],
				popupAnchor : [ 1, -34 ],
				shadowSize : [ 41, 41 ]
			});

	var redIcon = new L.Icon(
			{
				iconUrl : 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
				shadowUrl : 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
				iconSize : [ 25, 41 ],
				iconAnchor : [ 12, 41 ],
				popupAnchor : [ 1, -34 ],
				shadowSize : [ 41, 41 ]
			});

	var blackIcon = new L.Icon(
			{
				iconUrl : 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-black.png',
				shadowUrl : 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
				iconSize : [ 25, 41 ],
				iconAnchor : [ 12, 41 ],
				popupAnchor : [ 1, -34 ],
				shadowSize : [ 41, 41 ]
			});

	var topC, bottom, right, left; // variables for the rectangle coordinates

	var locationInfo;
	var marker;

	var rect;

	if (this.readyState == 4) {
		if (this.status == 200) { // data received from my servlet
			var locData = JSON.parse(this.responseText);
			
			for (var g = 0; g < locData.green.length; ++g) { // green markers

				let loc = locData.green[g];
				marker = L.marker(
						[ parseFloat(loc.latitude),
							parseFloat(loc.longitude) ], {
							icon : greenIcon
						}).addTo(stwMapNS.mymap);

				// compare with the current coordinates
				if (parseFloat(loc.latitude) > parseFloat(topC)) {
					topC = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.latitude) < parseFloat(bottom)) {
					bottom = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.longitude) > parseFloat(right)) {
					right = parseFloat(loc.longitude);
				}
				if (parseFloat(loc.longitude) < parseFloat(left)) {
					left = parseFloat(loc.longitude);
				}

				// Write some details
				locationInfo = loc.name + ", " + loc.region;
				locationInfo += "<br/><a href=\"GoToLocationDetails?idl="; // More details clicking on the link
				locationInfo += loc.ID;
				locationInfo += "\">Details</a>";

				marker.bindPopup(locationInfo); // add a popup containing the details

				if (first) { // first location = define rectangle coordinates
					topC = parseFloat(loc.latitude);
					bottom = parseFloat(loc.latitude);
					right = parseFloat(loc.longitude);
					left = parseFloat(loc.longitude);
					first = false; // the next location won't be the first anymore
				}
			}


			for (var y = 0; y < locData.yellow.length; ++y) {

				let loc = locData.yellow[y];
				marker = L.marker(
						[ parseFloat(loc.latitude),
							parseFloat(loc.longitude) ], {
							icon : yellowIcon
						}).addTo(stwMapNS.mymap);

				if (parseFloat(loc.latitude) > parseFloat(topC)) {
					topC = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.latitude) < parseFloat(bottom)) {
					bottom = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.longitude) > parseFloat(right)) {
					right = parseFloat(loc.longitude);
				}
				if (parseFloat(loc.longitude) < parseFloat(left)) {
					left = parseFloat(loc.longitude);
				}

				locationInfo = loc.name + ", " + loc.region;
				locationInfo += "<br/><a href=\"GoToLocationDetails?idl=";
				locationInfo += loc.ID;
				locationInfo += "\">Details</a>";

				marker.bindPopup(locationInfo);

				if (first) {
					topC = parseFloat(loc.latitude);
					bottom = parseFloat(loc.latitude);
					right = parseFloat(loc.longitude);
					left = parseFloat(loc.longitude);
					first = false;
				}
			}


			for (var r = 0; r < locData.red.length; ++r) {

				let loc = locData.red[r];
				marker = L.marker(
						[ parseFloat(loc.latitude),
							parseFloat(loc.longitude) ], {
							icon : redIcon
						}).addTo(stwMapNS.mymap);

				if (parseFloat(loc.latitude) > parseFloat(topC)) {
					topC = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.latitude) < parseFloat(bottom)) {
					bottom = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.longitude) > parseFloat(right)) {
					right = parseFloat(loc.longitude);
				}
				if (parseFloat(loc.longitude) < parseFloat(left)) {
					left = parseFloat(loc.longitude);
				}

				locationInfo = loc.name + ", " + loc.region;
				locationInfo += "<br/><a href=\"GoToLocationDetails?idl=";
				locationInfo += loc.ID;
				locationInfo += "\">Details</a>";

				marker.bindPopup(locationInfo);

				if (first) {
					topC = parseFloat(loc.latitude);
					bottom = parseFloat(loc.latitude);
					right = parseFloat(loc.longitude);
					left = parseFloat(loc.longitude);
					first = false;
				}
			}

			for (var b = 0; b < locData.black.length; ++b) { // Black markers for workers

				let loc = locData.black[b];
				marker = L.marker(
						[ parseFloat(loc.latitude),
							parseFloat(loc.longitude) ], {
							icon : blackIcon
						}).addTo(stwMapNS.mymap);

				if (parseFloat(loc.latitude) > parseFloat(topC)) {
					topC = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.latitude) < parseFloat(bottom)) {
					bottom = parseFloat(loc.latitude);
				}
				if (parseFloat(loc.longitude) > parseFloat(right)) {
					right = parseFloat(loc.longitude);
				}
				if (parseFloat(loc.longitude) < parseFloat(left)) {
					left = parseFloat(loc.longitude);
				}

				locationInfo = loc.name + ", " + loc.region;
				locationInfo += "<br/><a href=\"GoToLocationDetails?idl=";
				locationInfo += loc.ID;
				locationInfo += "\">Details</a>";

				marker.bindPopup(locationInfo);

				if (first) {
					topC = parseFloat(loc.latitude);
					bottom = parseFloat(loc.latitude);
					right = parseFloat(loc.longitude);
					left = parseFloat(loc.longitude);
					first = false;
				}
			}

		}

		rect = L.polygon([ // Define the rectangle
			[topC, right],
			[topC, left],
			[bottom, left],
			[bottom, right]
			]);

		rect.options.fillColor = "#000000"; // Fill the rectangle
		rect.options.color = "#000000"; // Define its border color

		rect.addTo(stwMapNS.mymap); // Draw the rectangle

		setTimeout(function () { // After 1000 ms restrict the view to the interesting area
			stwMapNS.mymap.fitBounds(rect.getBounds());
		}, 1000);
	}
}
