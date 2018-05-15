$(document).ready(function(){
	document.getElementById('loading-img').style.display = "block";
	$.ajax({
		type: "GET",
		url: "/recSysApp/rest/services/film/askToRate/10",
		success: function(response){
			document.getElementById('loading-panel').style.display = "none";
			var count = 0;
			while(count<response.length){
				$.ajax({
					type: "GET",
					url: "http://www.omdbapi.com/?i=tt"+ response[count] +"&apikey="+config.OMDB_API_KEY,
					success: function(response){
						if(response.Response == 'True'){
							$('#film-panel').append('<div class="film" data-tooltip=#'+ response.imdbID +'>' +
														'<div class="poster" data-tooltip=#'+ response.imdbID +'>' +
														'<a href="https:\/\/www.imdb.com\/title\/'+ response.imdbID +'" target="_blank">'+
														'<img src='+ response.Poster +' style="width:150px; height:auto"></a>' +
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
		},
		error: function(result, status, error){
			alert("Sorry, an error occurred. Please try again later");
		}
	})
	
	var ratings =  new Object();
	
	$("#film-panel").on('mouseenter','.poster', function(e) {
	    $($(this).data("tooltip")).css({
	        left: e.pageX - $('#film-panel').offset().left +1 ,
	        top: e.pageY - $('#film-panel').offset().top  +1
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
	    if(Object.keys(ratings).length==10){
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
		FB.getLoginStatus(function(response) {
	        if (response.status === 'connected') {
	        	
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
						window.location.replace("/recSysApp/map.html");
					},
					error: function(result, status, error){
						alert("Sorry, an error occurred. Please try again later");
					}
				})
	        }
			else{
				//login
				fbLogin();
			}
		
		})
	});

});