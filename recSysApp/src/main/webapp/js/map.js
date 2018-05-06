$(document).ready(function(){

	var position_data;
	
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
	          src:"https://raw.githubusercontent.com/jonataswalker/map-utils/master/images/marker.png"
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
		console.log(position_data);
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
	  	console.log(evt);
	  	var coordinate = evt.coordinate;
	    var hdms = ol.coordinate.toStringHDMS(ol.proj.transform(
	        coordinate, 'EPSG:3857', 'EPSG:4326'));
	    position_data = {
				coordinate : coordinate,
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
				console.log(map.getLayers());
		        return feature;
		      });
		if (feature) {
		    var geometry = feature.getGeometry();
		    var coord = geometry.getCoordinates();
		    popup.setPosition(coord);

		} else {
			var coordinate = evt.map.getCoordinateFromPixel(evt.pixel);
			markerLayer.getSource().clear();
			var newFeature = new ol.Feature({
			         geometry : new ol.geom.Point(  ol.proj.fromLonLat([coordinate[0], coordinate[1]]) ),
			         style : markerStyle
			});
			console.log(markerLayer.getSource().getFeatures());
			markerLayer.getSource().addFeature(newFeature);
			console.log(markerLayer.getSource().getFeatures());

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
					console.log(response);
					console.log(coordinate);
					position_data = {
							coordinate : coordinate,
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
	
}); //end of document ready

