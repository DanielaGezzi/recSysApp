$(document).ready(function(){
	document.getElementById('loading-img').style.display = "block";
	$.ajax({
		type: "GET",
		url: "/recSysApp/rest/services/film/askToRate/90",
		success: function(response){
			document.getElementById('loading-panel').style.display = "none";
			getFilmFromOmdb(response.slice(0,17), 1);
			getFilmFromOmdb(response.slice(18,35), 2);
			getFilmFromOmdb(response.slice(36,53), 3);
			getFilmFromOmdb(response.slice(54,71), 4);
			getFilmFromOmdb(response.slice(72,89), 5);

			document.getElementById('pagination-container').style.display = "block";

		},
		error: function(result, status, error){
			alert("Sorry, an error occurred. Please try again later");
		}
	})
	
	$("#pagination li").on('click', function(){
		var n = $(this).attr('id');
		$('.active').removeClass('active');
		$(this).addClass('active');
		$('.films-active').removeClass('films-active');
		$('#films' + n ).addClass('films-active');
	})
	
	var ratings =  new Object();
	
	$("#film-panel").on('mouseenter','.poster', function(e) {
		var _width = $($(this).data("tooltip")).outerWidth() + 100,
		    _height = $($(this).data("tooltip")).outerHeight()+ 100;
		var _outerWidth = document.getElementById('container').offsetWidth,
		    _outerHeight = document.getElementById('container').offsetHeight;
		var x = e.pageX,
			y = e.pageY;
		
	    if(x >(_outerWidth - _width)){
	        x = _outerWidth - _width;}
	    //if(y >(_outerHeight - _height)){
	      //  y = _outerHeight - _height;}
	    
	    $($(this).data("tooltip")).css({
	        left: x - $('#film-panel').offset().left +1,
	        top: y - $('#film-panel').offset().top +1
	    }).stop().show(100);
	})
	.on('mousemove', '.poster', function(e){
		var _width = $($(this).data("tooltip")).outerWidth() + 100,
	    	_height = $($(this).data("tooltip")).outerHeight()+ 100;
		var _outerWidth = document.getElementById('container').offsetWidth,
		_outerHeight = document.getElementById('container').offsetHeight;
		var x = e.pageX,
			y = e.pageY;
	
		if(x >(_outerWidth - _width)){
			x = _outerWidth - _width;}
		//if(y >(_outerHeight - _height)){
			//y = _outerHeight - _height;}
    
		$($(this).data("tooltip")).css({
			left: x - $('#film-panel').offset().left +1,
			top: y - $('#film-panel').offset().top +1
		}).stop().show(100);
	})
	.on('mouseleave','.poster',function() {
	    $($(this).data("tooltip")).hide();
	        
	})
	.on('change','#score', function () {
	    var optionSelected = $("option:selected", this);
	    var valueSelected = optionSelected.val();
	    var imdbid = $(this).data("tooltip").substring(3);
	    ratings[imdbid] = valueSelected;
	    if(Object.keys(ratings).length >= 10){
	    	document.getElementById('btn-continue').style.display = "block";
	    }else{
	    	document.getElementById('btn-continue').style.display = "none";
	    }
	})
	.on('load', '#score', function(){
	      $(this).barrating({
	        theme: 'fontawesome-stars'
	      });
		   
	});
	
	$("#btn-continue").on('click', function(){
		$.LoadingOverlay("show");
		FB.getLoginStatus(function(response) {
	        if (response.status === 'connected') {
	        	
	        	var json = {
	        			accessToken : response.authResponse.accessToken,
	    				ratings : ratings
	    				};
	        	$.ajax({
					type: "POST",
					url: "/recSysApp/rest/services/film/newRatings",
					contentType: "application/json",
					data: JSON.stringify(json),
					success: function(response){
						setTimeout(function(){
							$.LoadingOverlay("hide");
							window.location.replace("/recSysApp/map.html");
						}, 6000);
						
					},
					error: function(result, status, error){
						alert("Sorry, an error occurred. Please try again later");
						$.LoadingOverlay("hide");
					}
					
				})
	        }
			else{
				//login
	        	FB.login(function (response) {    	
	                if (response.status == 'connected') {
	                	var json = {
	    	        			accessToken : response.authResponse.accessToken,
	    	    				ratings : ratings
	    	    				};
	    	        	console.log(json);
	    	        	$.ajax({
	    					type: "POST",
	    					url: "/recSysApp/rest/services/film/newRatings",
	    					contentType: "application/json",
	    					data: JSON.stringify(json),
	    					success: function(response){
	    						$.LoadingOverlay("hide");
	    						window.location.replace("/recSysApp/map.html");
	    					},
	    					error: function(result, status, error){
	    						alert("Sorry, an error occurred. Please try again later");
	    						$.LoadingOverlay("hide");
	    					}
	    					
	    				})
	                } else {        	
	                    alert('User cancelled login or did not fully authorize.');
	                    window.location.replace("/recSysApp/");
	                }       
	            },{scope: 'public_profile,user_likes'});
			}
		
		})
	});

});


function getFilmFromOmdb(filmList, page){
	var count = 0;
	while(count<filmList.length){
		$.ajax({
			type: "GET",
			url: "http://www.omdbapi.com/?i=tt"+ filmList[count] +"&apikey="+config.OMDB_API_KEY,
			success: function(response){
				if(response.Response == 'True'){
					console.log("true" + count);
					$('#films'+page).append('<div id="film" class="film" data-tooltip=#'+ response.imdbID +'>' +
												'<div class="poster" data-tooltip=#'+ response.imdbID +'>' +
												'<a href="https:\/\/www.imdb.com\/title\/'+ response.imdbID +'" target="_blank">'+
												'<img class="poster" src='+ response.Poster +'></a>' +
												'</div>' +
												'<p>'+ response.Title +'</p>' +
											   	'<select id="score" data-tooltip=#'+ response.imdbID +'>' +
											   	'<option value="" selected disabled hidden>*</option>' +
											   	'<option value="1">1</option>' +
											   	'<option value="2">2</option>' +
											   	'<option value="3">3</option>' +
											   	'<option value="4">4</option>' +
											   	'<option value="5">5</option>' +
											   	'</select>' +
												'</div>');
					$('#film-panel').append('<div class="over-popup" id='+ response.imdbID +'>'+
											'<p><b>Title</b>: '+ response.Title +'</p>'+
											'<p><b>Year</b>: '+ response.Year +'</p>' +
											'<p><b>Genre</b>: '+ response.Genre +'</p>' +
											'<p><b>Director</b>: '+ response.Director +'</p>' +
											'<p><b>Actors</b>: '+ response.Actors +'</p>' +
											'<p><b>Plot</b>: '+ response.Plot +'</p></div>')
				}
			},
			error: function(result, status, error){
				alert("Sorry, an error occurred retrieving film information. Please try again later");
			}
		})
		count++;
	}
}