/**
 * This JS script is used to create a square containing the user scores.
 */

window.addEventListener("load", createScore, false);

function createScore() {
	var x = new XMLHttpRequest();
	x.onreadystatechange = loadSquare;
	x.open("GET", "/smartOceanCleaner/GetScores");
	x.send();
}

function loadSquare() {

	if (this.readyState == 4) {
		if (this.status == 200) {
		
		console.log("Loading points...");
		
		var scores = JSON.parse(this.responseText);

		var myBody = document.getElementsByTagName("body")[0];

		var a = document.createElement("a");
		a.className = "square";
		a.id = "square_float";
		a.color = "red";
		a.innerHTML = scores.points + " points<br/><br/>Precision: " + scores.percentage + " %";
			
		myBody.appendChild(a);
		}
	}
}