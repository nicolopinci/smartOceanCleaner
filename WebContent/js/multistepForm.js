/*
 * This script is used to handle the wizard behaviour and to check
 * the values inserted in its fields.
 */

stwMultiNS = function(){
	var currentTab = 0;
	return{
		currentTab:currentTab,
	}
}();

window.addEventListener("load", resetAll, false);
document.getElementById("newLocationFormButton").addEventListener("click", showTab(0), false);
document.getElementById("reset_btn").addEventListener("click", resetForm, false);
document.getElementById("back_btn").addEventListener("click", prevTab, false);
document.getElementById("next_btn").addEventListener("click", nextTab, false);
window.addEventListener("DOMContentLoaded", addFormListeners, false);

function addFormListeners() {
	var forms = document.getElementsByClassName('form__field');

	for (i = 0; i < forms.length; ++i) {
		forms[i].oninput = function() {
			this.className = this.className.replace(' invalid', '');
		}
	}
}


function resetAll() {
	document.getElementById("newLocationForm").reset(); 
	document.getElementById("image_input").className += ' invalid';
}

function prevTab() {
	return nextPrev(-1);
}

function nextTab() {
	return nextPrev(1);
}

function showTab(n) {

	var tabs = document.getElementsByClassName("wizard_tab"); // ALl the tabs

	tabs[n].style.display = "block"; // show the n-th tab

	document.getElementById("reset_btn").innerHTML = "Reset"; // the reset button's text is 'Reset'

	if(n!=0) {
		document.getElementById("back_btn").innerHTML = "Previous";
		document.getElementById("back_btn").style = "";
	}
	else {
		document.getElementById("back_btn").innerHTML = "Cancel";
	}

	if (n == (tabs.length - 1)) { // you have arrived at the last tab -> there is no next tab
		document.getElementById('next_btn').innerHTML = 'Submit';
	} else { // -> there is next tab
		document.getElementById('next_btn').innerHTML = 'Next';
	}
	//... and run a function that will display the correct step indicator:
	fixStepIndicator(n)
}


function nextPrev(n) {

	// This function will figure out which tab to display
	var tabs = document.getElementsByClassName("wizard_tab");
	// Exit the function if any field in the current tab is invalid:

	if (n == 1 && !validateForm()) { // you want to go to the next step but some data are ot good
		return false; // show error!
	}

	//hide the current tab
	tabs[stwMultiNS.currentTab].style.display = 'none';
	//increments tabs (or decrease if n = -1)

	if (stwMultiNS.currentTab + n < 0) { // hides the form if you want to go back and you end up with a negative number
		jQuery('#locationWizard').toggle('none');
		jQuery('#newLocationFormButton').toggle('show');
	} else {
		if (stwMultiNS.currentTab + n <= tabs.length) {
			stwMultiNS.currentTab += n; // otherwise go to the right tab
		} else {
			return false;
		}
	}

	if (stwMultiNS.currentTab >= tabs.length) { //if current tab is >= tabs.length the submit button has been pressed
		document.getElementById("newLocationForm").submit(); // submit the form
	} else {
		showTab(stwMultiNS.currentTab);
	}
}

function validateForm() {
	var tabs, inputs, i, valid = true;

	tabs = document.getElementsByClassName("wizard_tab");
	inputs = tabs[stwMultiNS.currentTab].getElementsByTagName("input");

	for (i = 0; i < inputs.length; i++) { // empty fields are not valid
		if (inputs[i].value == '') {
			inputs[i].className += ' invalid';
			valid = false;
		}

		else if(inputs[i].name === 'latitude' && (inputs[i].value < -90 || inputs[i].value > 90)) { // check latitude value...
			inputs[i].className += ' invalid';
			valid = false;
		}

		else if(inputs[i].name === 'longitude' && (inputs[i].value < -180 || inputs[i].value > 180)) { //... and longitude value
			inputs[i].className += ' invalid';
			valid = false;
		}

		else if(inputs[i].name === 'date') { // This is the date field: don't allow to set a future date
			var todayDate = new Date();
			var insertedDate = new Date(inputs[i].value);
			if(todayDate < insertedDate) {
				inputs[i].className += ' invalid';
				valid = false;
			}
		}
		
		else if(inputs[i].name === 'location_name') {
			if(inputs[i].value.length > 200) {
				inputs[i].className += ' invalid';
				valid = false;
			}
		}
		
		else if(inputs[i].name === 'city') {
			if(inputs[i].value.length > 200) {
				inputs[i].className += ' invalid';
				valid = false;
			}
		}
		
		else if(inputs[i].name === 'region') {
			if(inputs[i].value.length > 200) {
				inputs[i].className += ' invalid';
				valid = false;
			}
		}
		
		else if(inputs[i].name === 'city') {
			if(inputs[i].value.length > 200) {
				inputs[i].className += ' invalid';
				valid = false;
			}
		}
		
		else if(inputs[i].name === 'source') {
			if(inputs[i].value.length > 200) {
				inputs[i].className += ' invalid';
				valid = false;
			}
		}
	}


	if (valid) {
		document.getElementsByClassName("step")[stwMultiNS.currentTab].className += " finish";
	}

	return valid;
}

function fixStepIndicator(n) {
	// This function removes the "active" class of all steps...
	var i, steps = document.getElementsByClassName("step");
	for (i = 0; i < steps.length; i++) {
		steps[i].className = steps[i].className.replace(" active", "");
	}
	//... and adds the "active" class on the current step:
	steps[n].className += " active";
}

document.getElementById("chooseLocation").onchange = reloadWizard; // reloads the wizard

function reloadWizard() {
	var addNewBox = document.getElementById("addNew");

	if (document.getElementById("chooseLocation").value == "def") {
		addNewBox.style.visibility = "visible";
		document.getElementById("newLocation_card").style.height = "auto";
		document.getElementById("addNew").style.height = "auto";
		document.getElementsByName("location_name")[0].value = "";
		document.getElementsByName("city")[0].value = "";
		document.getElementsByName("region")[0].value = "";
		document.getElementsByName("latitude")[0].value = "";
		document.getElementsByName("longitude")[0].value = "";
	} else { // put some default values that will never be read
		addNewBox.style.visibility = "hidden";
		document.getElementById("newLocation_card").style.height = "auto";
		document.getElementById("addNew").style.height = "0px";

		document.getElementsByName("location_name")[0].value = " ";
		document.getElementsByName("city")[0].value = " ";
		document.getElementsByName("region")[0].value = " ";
		document.getElementsByName("latitude")[0].value = 0;
		document.getElementsByName("longitude")[0].value = 0;
	}
}

function resetForm(event) { // Do you want to reset the form?
	event.preventDefault();

	var myBody = document.getElementsByTagName("body")[0];

	hideModal(); // hide other modals

	modalDiv = document.createElement("div");
	modalDiv.id = "myModal";
	modalDiv.className = "modal";

	confirmText = document.createElement("p");
	confirmText.innerHTML = "Do you really want to reset this form?";
	confirmText.id = "ctext";

	buttonYes = document.createElement("button");
	buttonYes.innerHTML = "Yes";
	buttonYes.className = "effettoripple mdc-button mdc-button--raised mdc-ripple-upgraded";
	buttonYes.id = "byes";
	buttonYes.style.backgroundColor = "rgba(0, 130, 0, 0.7)";

	buttonNo = document.createElement("button");
	buttonNo.innerHTML = "No";
	buttonNo.className = "effettoripple mdc-button mdc-button--raised mdc-ripple-upgraded";
	buttonNo.id = "bno";
	buttonNo.style.backgroundColor = "rgba(130, 0, 0, 0.7)";

	modalDiv.appendChild(confirmText);
	modalDiv.appendChild(buttonYes);
	modalDiv.appendChild(buttonNo);
	myBody.appendChild(modalDiv);

	modalDiv.style.display = "block";

	buttonYes.addEventListener("click", resetConfirmed, false);
	buttonNo.addEventListener("click", hideModal, false);
}

function resetConfirmed() { // confirm reset
	document.getElementById("newLocationForm").reset(); // reset
	document.getElementById("addNew").style.visibility = "visible";
	document.getElementById("addNew").style.height = "auto";
	document.getElementById("newLocation_card").style.height = "auto";
	document.getElementsByName("img")[0].className += ' invalid';

	nextPrev(stwMultiNS.currentTab*(-1)); // go back to the first tab

	hideModal();
}

function hideModal() {
	try {
		document.getElementById("myModal").remove();
	}
	catch (Exception) {
		console.log("Exception");
	}
}