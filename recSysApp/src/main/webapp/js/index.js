window.fbAsyncInit = function() {
    // FB JavaScript SDK configuration and setup
    FB.init({
      appId      : '1859363404087366', // FB App ID
      cookie     : true,  // enable cookies to allow the server to access the session
      xfbml      : true,  // parse social plugins on this page
      version    : 'v2.12' // use graph api version 2.12
    });
};

// Facebook login with JavaScript SDK
function fbLogin() {
    FB.login(function (response) {
        if (response.status == 'connected') {
            // Get and display the user profile data
        	var accessToken = response.authResponse.accessToken;
            getFbUserData(accessToken);
        } else {
            document.getElementById('status').innerHTML = 'User cancelled login or did not fully authorize.';
        }
    }, {scope: 'public_profile,user_likes'});
}

//Fetch the user profile data from facebook
function getFbUserData(userAccessToken){
    FB.api('/me', {locale: 'en_US', fields: 'id,first_name,last_name,link,gender,locale,picture'},
    function (response) {
        document.getElementById('buttonFacebook').setAttribute("onclick","fbLogout()");
        document.getElementById('status').innerHTML = 'Thanks for logging in, ' + response.first_name + '!';
        document.getElementById('userData').innerHTML = '<p><b>FB ID:</b> '+response.id+'</p><p><b>Name:</b> '+response.first_name+' '+response.last_name+'</p><p><b>Gender:</b> '+response.gender+'</p><p><b>Locale:</b> '+response.locale+'</p><p><b>Picture:</b> <img src="'+response.picture.data.url+'"/></p><p><b>FB Profile:</b> <a target="_blank" href="'+response.link+'">click to view profile</a></p>';
        //salvare utente
    });
    
    FB.api('/me/likes', {accessToken : userAccessToken},
    function (response) {
    	console.log(response);
    	//salvare likes
    	
    });
    
}

// Logout from facebook
function fbLogout() {
    FB.logout(function(response) {
		document.getElementById('facebookLogin').setAttribute("hidden", true);
        document.getElementById('fbLink').setAttribute("onclick","fbLogin()");
        document.getElementById('fbLink').innerHTML = '<img src="fblogin.png"/>';
        document.getElementById('userData').innerHTML = '';
        document.getElementById('status').innerHTML = 'You have successfully logout from Facebook.';
    });
}

$(document).ready(function(){
	
	//Load the JavaScript SDK asynchronously
	(function(d, s, id) {
	    var js, fjs = d.getElementsByTagName(s)[0];
	    if (d.getElementById(id)) return;
	    js = d.createElement(s); js.id = id;
	    js.src = "//connect.facebook.net/en_US/sdk.js";
	    fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));
    
    
	$("#buttonFacebook").on("click", function(){
		
		document.getElementById('facebookLogin').setAttribute("hidden", true);
	    // Check whether the user already logged in
		FB.getLoginStatus(function(response) {
	        if (response.status === 'connected') {
	        	var accessToken = response.authResponse.accessToken;
	        	//display user data
	            getFbUserData(accessToken);
	        }
	        else{
	        	fbLogin();
	        }
	    });
		
		
		
	});
});

