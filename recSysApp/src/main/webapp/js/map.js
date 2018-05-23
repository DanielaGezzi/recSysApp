$(document).ready(function(){

	var positionData;
	
	/**
	 * Map configuration
	 */
	var view = new ol.View({
	    center: [12.5113300, 41.8919300], //centered on Rome
	    zoom: 12,
		projection: 'EPSG:4326' //or EPSG:3857
	
	  });
	
	var mousePositionControl = new ol.control.MousePosition({
        coordinateFormat: ol.coordinate.createStringXY(4),
        projection: 'EPSG:4326', //or EPSG:3857
        // comment the following two lines to have the mouse position
        // be placed within the map.
        className: 'custom-mouse-position',
        target: document.getElementById('mouse-position'),
        undefinedHTML: '&nbsp;'
      });
	
	var mapLayer = new ol.layer.Tile({
		name: 'mapLayer',
        source: new ol.source.OSM()
    });
	
	var markerLayer = new ol.layer.Vector({
		  name : 'markerLayer',
		  source: new ol.source.Vector({
		  features: []
		  })
		});
	
	var markerStyle = new ol.style.Style({
	      image: new ol.style.Icon(
	        ({
	          scale: 0.7,
	          src:"./img/marker.png"
	        }))});
	
	var map = new ol.Map({
	    controls: ol.control.defaults({
	      attributionOptions: {
	        collapsible: false
	      }
	    }).extend([mousePositionControl]),
	    layers: [
	      mapLayer,
	      markerLayer
	    ],
	    target: document.getElementById('map'),
	    view: view
	});
	/** 
	 * End of Map configuration
	 */
	
	
	/** 
	 * Popup configuration
	 */     
	var container = document.getElementById('popup');
	var content = document.getElementById('popup-content');
	var closer = document.getElementById('popup-closer'); 
	var search = document.getElementById('popup-search');
	
	var popup = new ol.Overlay({
	    element: container,
	    autoPan: true,
	    autoPanAnimation: {
	      duration: 250
	    }
	  });
	
	/**
	 * A click handler to hide the popup
	 * @return {boolean} Don't follow the href.
	 */
	closer.onclick = function(){
	  popup.setPosition(undefined);
	  closer.blur();
	  return false;
	};
	
	/**
	 * A click handler to start searching films and hide the Popup
	 * 
	 */
	search.onclick = function(){
		popup.setPosition(undefined);
		closer.blur();
		searchFilms();
	}
	
	map.addOverlay(popup);
	
	/** 
	 * End of Popup configuration
	 */ 
	
	
	//Instantiate with some options and add the Control
	var geocoder = new Geocoder('nominatim', {
	    provider: 'osm',
	    lang: 'en',
	    placeholder: 'Search for ...',
	    limit: 5,
	    debug: false,
	    autoComplete: true,
	    keepOpen: true
	  });
	map.addControl(geocoder);
	    
	//Listen when an address is chosen
	geocoder.on('addresschosen', function (evt) {
	  	map.getLayers().getArray()[2].getSource().clear();
	  	console.info(evt);
	  	//console.log(evt);
	  	var coordinate = evt.coordinate;
	    var hdms = ol.coordinate.toStringHDMS(ol.proj.transform(
	        coordinate, 'EPSG:3857', 'EPSG:4326'));
	    positionData = {
				latitude : coordinate[1],
				longitude : coordinate[0],
				place : evt.address.original.details.attraction,
				city : evt.address.original.details.city,
				state : evt.address.original.details.state,
				country : evt.address.original.details.country
		};
	    flyTo(coordinate, function(){});
	    window.setTimeout(function () {
	    content.innerHTML = '<p>You are here:</p><code>' +
	    					coordinate + '<br>' +
	    					evt.address.formatted +
	    					'</code>';
	    popup.setPosition(coordinate);
	    }, 2000);
	  });
	
	//Listen when map is clicked
	map.on('click', function(evt){
		var feature = map.forEachFeatureAtPixel(evt.pixel,
		      function(feature, layer) {
				//console.log(map.getLayers());
		        return feature;
		      });
		if (feature) {
		    var geometry = feature.getGeometry();
		    var coord = geometry.getCoordinates();
		    popup.setPosition(coord);

		} else {
			markerLayer.getSource().clear();
			var coordinate = evt.map.getCoordinateFromPixel(evt.pixel);
			var newFeature = new ol.Feature({
			         geometry : new ol.geom.Point(  ol.proj.fromLonLat([coordinate[0], coordinate[1]]) ),
			         style : markerStyle
			});
			markerLayer.getSource().addFeature(newFeature);
			//console.log(markerLayer.getSource().getFeatures());

			var hdms = ol.coordinate.toStringHDMS(ol.proj.transform(
			        coordinate, 'EPSG:3857', 'EPSG:4326'));
			flyTo(coordinate, function(){});
			$.ajax({
				type: "GET",
				url: "https://nominatim.openstreetmap.org/reverse",
				data: {
					format: "jsonv2",
					"accept-language": "en",
					lat: coordinate[1],
					lon: coordinate[0],
				},
				dataType: "json",
				success: function(response){
					//console.log(response);
					//console.log(coordinate);
					positionData = {
							latitude : coordinate[1],
							longitude : coordinate[0],
							place : response.name,
							city : response.address.city,
							state : response.address.state,
							country : response.address.country
					};
					window.setTimeout(function () {
					    content.innerHTML = '<p>You are here:</p><code>' +
					    					'Coordinates: [' +
					    					Number(coordinate[0]).toFixed(3) + ' - ' + 
					    					Number(coordinate[1]).toFixed(3) +']<br>' +
					    					'<span class="gcd-road">'+ response.display_name +
					    					'</span>'
					    					response.address.formatted +
					    					'</code>';
					    popup.setPosition(coordinate);
					    }, 2000);
	
				},
				error: function(result, status, error){
					console.log("Sorry, an error occurred. Please try again later");
					alert("Sorry, an error occurred. Please try again later");
				}
			})//end of ajax call
		}//end of else
	}); 
	
	/**
	 * A function to create the animation "zoom out-in" 
	 * when poi is chosen
	 */
	function flyTo(location, done) {
	    var duration = 2000;
	    var zoom = view.getZoom();
	    var parts = 2;
	    var called = false;
	    function callback(complete) {
	      --parts;
	      if (called) {
	        return;
	      }
	      if (parts === 0 || !complete) {
	        called = true;
	        done(complete);
	      }
	    }
	    view.animate({
	      center: location,
	      duration: duration
	    }, callback);
	    view.animate({
	      zoom: zoom - 1, //zoom-out -1
	      duration: duration / 2
	    }, {
	      zoom: 17, //zoom-in 17
	      duration: duration / 2
	    }, callback);
	  }

	//change mouse cursor when over marker
	$(map.getViewport()).on('mousemove', function(e) {
	  var pixel = map.getEventPixel(e.originalEvent);
	  var hit = map.forEachFeatureAtPixel(pixel, function(feature, layer) {
	    return true;
	  });
	  if (hit) {
	    map.getTarget().style.cursor = 'pointer';
	  } else {
	    map.getTarget().style.cursor = '';
	  }
	});
	
	

	function searchFilms(){
		$.LoadingOverlay("show");
	    // Check whether the user already logged in
		FB.getLoginStatus(function(response) {
	        if (response.status === 'connected') {    		        
				$.ajax({
					type: "GET",
					url: "http://localhost:8080/recSysApp/rest/services/film/location",
					data: {
						accessToken : response.authResponse.accessToken,
						latitude: positionData.latitude,
						longitude: positionData.longitude,
						place : positionData.place,
						city : positionData.city,
						state : positionData.state,
						country : positionData.country
					},
					dataType: "json",
					success: function(response){
						console.log(response);
						document.getElementById('film-panel').style.display = "flex";
						getFilmsInfoW2V(response.w2v, function(){});
						getFilmsInfoLK(response.lk, function(){});
						document.getElementById('btn-save').style.display = "block";
						$.LoadingOverlay("hide");
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
	                	window.location.replace("/recSysApp/map.html");
	                } else {        	
	                    alert('User cancelled login or did not fully authorize.');
	                    window.location.replace("/recSysApp/");
	                }       
	            },{scope: 'public_profile,user_likes'});  
	        	
	        }
	    });

	};
	

	var w2v_map =  new Object();
	function getFilmsInfoW2V(films, done){
		$('#film-panel-w2v').empty();
		var count = 0;
		var max = 5;
		while(count<max){
			w2v_map[films[count].imdbId.substring(2)] = 0;
			$.ajax({
				type: "GET",
				url: "http://www.omdbapi.com/?i="+ films[count].imdbId +"&apikey="+config.OMDB_API_KEY,
				success: function(response){
					if(response.Response == 'True'){
						$('#film-panel-w2v').append('<div class="film" data-tooltip=#'+ response.imdbID +'>' +
														'<div class="poster" data-tooltip=#'+ response.imdbID +'>' +
														'<a href="https:\/\/www.imdb.com\/title\/'+ response.imdbID +'" target="_blank">'+
														'<img class="poster" src='+ response.Poster +'></a>' +
														'</div>' +
														'<p>'+ response.Title +'</p>' +	
													   	'<select id="w2v-score" data-tooltip=#'+ response.imdbID +'>' +
													   	'<option value="" selected disabled hidden>*</option>' +
													   	'<option value="1">1</option>' +
													   	'<option value="2">2</option>' +
													   	'<option value="3">3</option>' +
													   	'<option value="4">4</option>' +
													   	'<option value="5">5</option>' +
													   	'</select>' +														
														'</div>');
						$('#film-panel-w2v').append('<div class="over-popup" id='+ response.imdbID +'>'+
													'<p><b>Title</b>: '+ response.Title +'</p>'+
													'<p><b>Year</b>: '+ response.Year +'</p>' +
													'<p><b>Genre</b>: '+ response.Genre +'</p>' +
													'<p><b>Director</b>: '+ response.Director +'</p>' +
													'<p><b>Actors</b>: '+ response.Actors +'</p>' +
													'<p><b>Plot</b>: '+ response.Plot +'</p></div>')
					}else{ max++; }
				},
				error: function(result, status, error){
					alert("Sorry, an error occurred retrieving film information. Please try again later");
					$.LoadingOverlay("hide");

				}
			})  
			count++;
		}	 
		
	}
	var lk_map = new Object();
	function getFilmsInfoLK(list, done){
		$('#film-panel-lk').empty();		
		var count = 0;
		var max = 5;
		while(count<max){
			lk_map[list[count]] =  0;
			$.ajax({
				type: "GET",
				url: "http://www.omdbapi.com/?i=tt"+ list[count] +"&apikey="+config.OMDB_API_KEY,
				success: function(response){
					if(response.Response == 'True'){
						$('#film-panel-lk').append('<div class="film" data-tooltip=#'+ response.imdbID +'>' +
														'<div class="poster" data-tooltip=#'+ response.imdbID +'>' +
														'<a href="https:\/\/www.imdb.com\/title\/'+ response.imdbID +'" target="_blank">'+
														'<img class="poster" src='+ response.Poster +' ></a>' +
														'</div>' +
														'<p>'+ response.Title +'</p>' +
													   	'<select id="lk-score" data-tooltip=#'+ response.imdbID +'>' +
													   	'<option value="" selected disabled hidden>*</option>' +
													   	'<option value="1">1</option>' +
													   	'<option value="2">2</option>' +
													   	'<option value="3">3</option>' +
													   	'<option value="4">4</option>' +
													   	'<option value="5">5</option>' +
													   	'</select>' +														
														'</div>');
						$('#film-panel-lk').append('<div class="over-popup" id='+ response.imdbID +'>'+
													'<p><b>Title</b>: '+ response.Title +'</p>'+
													'<p><b>Year</b>: '+ response.Year +'</p>' +
													'<p><b>Genre</b>: '+ response.Genre +'</p>' +
													'<p><b>Director</b>: '+ response.Director +'</p>' +
													'<p><b>Actors</b>: '+ response.Actors +'</p>' +
													'<p><b>Plot</b>: '+ response.Plot +'</p></div>')
					}else{ max++; }
				},
				error: function(result, status, error){
					alert("Sorry, an error occurred retrieving film information. Please try again later");
					$.LoadingOverlay("hide");

				}
			})  
			count++;
		}	 
		
	}
		
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
	        left: x - $('#film-panel').offset().left + 1,
	        top: y - $('#film-panel').offset().top + 1
	    }).stop().show(100);
	})
	.on('mousemove', '.poster', function(e){
		var _width = $($(this).data("tooltip")).outerWidth(),
	    	_height = $($(this).data("tooltip")).outerHeight();
		var _outerWidth = document.getElementById('container').offsetWidth,
		_outerHeight = document.getElementById('container').offsetHeight;
		var x = e.pageX,
			y = e.pageY;
	
		if(x >(_outerWidth - _width)){
			x = _outerWidth -  _width;}
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
	.on('change','#w2v-score', function () {
	    var optionSelected = $("option:selected", this);
	    var valueSelected = parseInt(optionSelected.val());
	    var imdbid = $(this).data("tooltip").substring(3);
	    w2v_map[imdbid] = valueSelected;

	})
	.on('change','#lk-score', function () {
	    var optionSelected = $("option:selected", this);
	    var valueSelected = parseInt(optionSelected.val());
	    var imdbid = $(this).data("tooltip").substring(3);
	    lk_map[imdbid] = valueSelected;
	});
	
	$("#btn-save").on('click', function(){
		FB.getLoginStatus(function(response) {       	
        	var json = {
	        			accessToken : response.authResponse.accessToken,
	        			w2v : w2v_map,
	    				lk : lk_map
    					};
        	$.ajax({
				type: "POST",
				url: "/recSysApp/rest/services/interview",
				contentType: "application/json",
				data: JSON.stringify(json),
				success: function(response){
					alert('Ratings saved! Select a new POI on the map to continue.')
					lk_map = new Object();
					w2v_map = new Object();
				},
				error: function(result, status, error){
					alert("Sorry, an error occurred. Please try again later");
				}
				
			})
		})
	})
	
		
}); //end of document ready



