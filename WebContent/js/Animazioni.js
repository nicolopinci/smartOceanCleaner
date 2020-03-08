jQuery(document).ready(function() { // The DOM is fully loaded
	jQuery('#newLocationFormButton').on('click', function() { // click on #newLocationFormButton
		jQuery('#newLocationFormButton').toggle('none'); // toggle displays or hides the selected elements (#newLocationFormButton and ...
		jQuery('#locationWizard').toggle('show'); // ... #locationWizard)
	});
});

jQuery(document).ready(function(){
  jQuery('#campaignFormButton').on('click', function(){ // click on campaignFormButton
    jQuery('#campaignForm').toggle('show');
    jQuery('#campaignFormButton').toggle('none');
  });
});

jQuery(document).ready(function(){
  jQuery('#cancel_btn').on('click', function(){ // click on cancel button
    jQuery('#campaignForm').toggle('none');
    jQuery('#campaignFormButton').toggle('show');
  });
});