/*
  JavaScript file which contains the functions related to the
  button of the worker page.
 */

//Hides the worker's campaigns and shows the new campaigns
function showNewCampaigns(){
	document.getElementById('campaigns').style.display = "none";
	var button = document.getElementById('button1');
	button.innerHTML = "YOUR CAMPAIGNS";
	document.getElementById('newCampaigns').style.display = "inline-block";

	//Switch the response to the button click event
	button.removeEventListener("click", showNewCampaigns);
	button.addEventListener("click", showCampaigns);
}

//Hides the new campaigns and shows the worker's campaigns
function showCampaigns(){
	document.getElementById('newCampaigns').style.display = "none";
	var button = document.getElementById('button1');
	button.innerHTML = "NEW CAMPAIGNS";
	document.getElementById('campaigns').style.display = "inline-block"; // inline-block is used to show elements side-by-side

	//Switch the Response to the Button Click Event
	button.removeEventListener("click", showCampaigns);
	button.addEventListener("click", showNewCampaigns);
}

//Sets the first function related to the Button Click Event
function setEventHandler(){
	var button = document.getElementById('button1');
	button.addEventListener("click", showCampaigns);
}

document.addEventListener("DOMContentLoaded", setEventHandler, false);
