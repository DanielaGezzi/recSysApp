$(document).ready(function(){
	
	function getFilmsInfo(films, done){
		var count = 0;
		for(count; count<1; count++){
			var imdbId = film.get('imdbID');
			$.ajax({
				type: "GET",
				url: "http://www.omdbapi.com/?i="+ imdbID +"&apikey=8127fe15",
				success: function(response){
					$('lodPanel').append('<div id=film><img src='+ response.get('Poster') +'></div>');
				},
				error: function(result, status, error){
					alert("Sorry, an error occurred retrieving film information. Please try again later");
				}
			})       
		}	 
		
	}
	
	
});