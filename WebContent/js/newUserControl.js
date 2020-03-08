/**
 * This JS script is used to check the information inserted in the signup form before
 * submitting it, in order to give some user-friendly information.
 */

document.getElementById("submitBtn").addEventListener("click", loadInfo, false);

function loadInfo(event) {
	
	event.preventDefault();
	var x = new XMLHttpRequest();
	x.onreadystatechange = checkFields;
	x.open("GET", "/smartOceanCleaner/CheckUsernameEmail?username="+document.getElementById("username").value+"&email="+document.getElementById("email").value);
	x.send();
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