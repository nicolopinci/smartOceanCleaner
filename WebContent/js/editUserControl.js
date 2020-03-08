/**
 * This JS script checks the fields in the edit profile form
 * in order to tell the user if he or she has inserted an existing
 * username, an existing or bad-formatted email address or if he or
 * she hasn't repeated the password correctly.
 */

stwEditNS = function(){
	var x = null;
	return{
		x:x,
	}
}();

document.getElementById("submitBtn").addEventListener("click", loadInfo, false);

function loadInfo(event) {

	event.preventDefault();

	stwEditNS.x = new XMLHttpRequest();
	stwEditNS.x.onreadystatechange = checkFields;
	stwEditNS.x.open("GET", "/smartOceanCleaner/CheckUsernameEmailForEdit?username="+document.getElementById("username").value+"&email="+document.getElementById("email").value);

	stwEditNS.x.send();
}

function checkFields() {	
	if (this.readyState == 4) {
		if (this.status == 200) {

			var msg = "";
			var pwd = document.getElementById("pwd").value;
			var repeatpwd = document.getElementById("repeatpwd").value;
			var email = document.getElementById("email").value;

			var userMail = JSON.parse(this.responseText);

			if(parseInt(userMail.username) > 0) {
				msg += "This username has already been registered.<br/>";
			}

			if(parseInt(userMail.email) > 0) {
				msg += "This e-mail address has already been registered.<br/>";
			}		

			if(!isValid(email)) {
				msg += "This e-mail address is not formatted properly.<br/>";
			}

			if(pwd !== repeatpwd) {
				msg += "Passwords don't match!";
			}
			if(msg!=='') {
				document.getElementsByClassName("errorBox")[0].style.padding = "30px";
				document.getElementsByClassName("errorBox")[0].innerHTML = msg;
			}
			else {
				let myForm = document.getElementById("userinfo");
				myForm.submit();
			}
		}
	}
}

function isValid(email) {
	var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return re.test(String(email).toLowerCase());
}


