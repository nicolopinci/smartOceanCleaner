/**
 * This JS script is used to create a circle containing the profile picture.
 * When the worker goes over it with the pointer, a variable message is shown,
 * depending on the moment of the day (morning, afternoon, night).
 */

stwProfNS = function(){
  var x = null;
  return{
    x:x,
  }
}();

window.addEventListener("load", createUserPic, false);

function createUserPic() {
	stwProfNS.x = new XMLHttpRequest();
	stwProfNS.x.onreadystatechange = loadCircle;
	stwProfNS.x.open("GET", "GetProfileImage");
	stwProfNS.x.send();
}

function loadCircle() {

	if (stwProfNS.x.readyState == 4 && stwProfNS.x.status == 200) {
		var myBody = document.getElementsByTagName("body")[0];

		var a = document.createElement("a");
		a.className = "float";
		a.id = "userPic_float";

		var img = document.createElement("img");
		img.id="userpic";

		img.src="GetProfileImage";

		a.appendChild(img);

		myBody.appendChild(a);
		
		img.addEventListener("mouseover", showGreetings, false);
		img.addEventListener("mouseout", hideGreetings, false);
	}
}

function showGreetings() {
	var d = new Date();
	var n = d.getHours();
	
	var greetingSpan = document.createElement("span");
	greetingSpan.className = "greeting";
	
	if(n<12 && n>=6) {
		greetingSpan.appendChild(document.createTextNode("Good morning"));
	}
	else if(n>=12 && n<=18) {
		greetingSpan.appendChild(document.createTextNode("Good afternoon"));
	}
	else {
		greetingSpan.appendChild(document.createTextNode("Good evening"));
	}
	
	document.getElementsByTagName("body")[0].appendChild(greetingSpan);
	}


function hideGreetings() {
	var greetings = document.getElementsByClassName("greeting");
	
	for(var i=0; i<greetings.length; ++i) {
		greetings[i].remove();
	}
}