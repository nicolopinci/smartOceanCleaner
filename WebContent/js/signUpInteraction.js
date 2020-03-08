/*
 * This JS script adjust the size of the signup card dinamically and shows or hides the
 * button used to upload a profile picture. 
 */

stwSignUp = function(){
	var smallCard = true;
	return{
		smallCard:smallCard,
	}
}();


document.getElementById("radio-2").onclick = showLess;
document.getElementById("radio-1").onclick = showMore;
document.getElementById('fileInput_btn').onclick = triggerUpload;

function showMore(){
	var card;
	if(stwSignUp.smallCard){
		stwSignUp.smallCard = false;
		card = document.getElementsByClassName('signupCard');
		document.getElementById('fileInput').style.display = "block";

	}
}

function showLess(){
	var card;
	if(!stwSignUp.smallCard){
		stwSignUp.smallCard=true;
		card = document.getElementsByClassName('signupCard');
		document.getElementById('fileInput').style.display = "none";
	}
}

function triggerUpload() {
	document.getElementById('user_image_input').click();
}