window.fbAsyncInit = function() {
    // FB JavaScript SDK configuration and setup
    FB.init({
      appId      : config.FACEBOOK_APP_KEY, // FB App ID
      cookie     : true,  // enable cookies to allow the server to access the session
      xfbml      : true,  // parse social plugins on this page
      version    : 'v2.12' // use graph api version 2.12
    });
};

// Facebook login with JavaScript SDK
function fbLogin() {	
    FB.login(function (response) {    	
        if (response.status == 'connected') {
            // Get the user profile data
            getFbUserData(response.authResponse.accessToken);            
        } else {        	
            document.getElementById('status').innerHTML = 'User cancelled login or did not fully authorize.';
        }       
    },{scope: 'public_profile,user_likes'});    
}

//Fetch the user profile data from facebook
function getFbUserData(userAccessToken){
    FB.api('/me', {locale: 'en_US', fields: 'first_name'},
    function (response) {
		document.getElementById('btn-fb-login').style.display = "none";
        document.getElementById('text-fb-login').innerHTML = ''; 
        document.getElementById('text-fb-login').innerHTML = 'Thanks for logging in, <b>' + response.first_name + '</b>!<br>We are retrieving some info about you, please wait...';
		document.getElementById('loading-img').style.display = "block";
      
        var json = {accessToken : userAccessToken}
        
		$.ajax({
			type: "POST",
			url: "/recSysApp/rest/services/facebook/user",
			contentType: "application/json",
			data: JSON.stringify(json),
			success: function(response){
				window.location.replace("/recSysApp/asktorate.html");
			},
			error: function(result, status, error){
				alert("Sorry, an error occurred. Please try again later");
			}
		})
        //console.log(response);
    });       
}

// Logout from facebook
function fbLogout() {
    FB.logout(function(response) {
        alert('You have successfully logout from Facebook.');
        window.location.replace("/recSysApp/");
		document.getElementById('btn-fb-login').style.display = "block";

    });
}

//document ready
$(document).ready(function(){
	
	//Load the JavaScript SDK asynchronously
	(function(d, s, id) {
	    var js, fjs = d.getElementsByTagName(s)[0];
	    if (d.getElementById(id)) return;
	    js = d.createElement(s); js.id = id;
	    js.src = "//connect.facebook.net/en_US/sdk.js";
	    fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));
       
	$("#btn-fb-login").on("click", function(){		
	    // Check whether the user already logged in and accepted app
		FB.getLoginStatus(function(response) {
	        if (response.status === 'connected') {
	            getFbUserData(response.authResponse.accessToken);
	        }
	        else{
	        	//login
	        	fbLogin();
	        }
	    });
	});
	
    $('#btn-fb-logout').on("click",function(){
    	FB.getLoginStatus(function(response) {
	        if (response.status === 'connected') {
	        	fbLogout();
	        }
	    });
    });	
	
});

/*
$(document).ajaxStart(function() {
	  $("#waitGif").show();
	}).ajaxStop(function() {
	  $("#waitGif").hide();
	});
*/