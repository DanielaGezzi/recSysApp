$(document).ready(function(){


var view = new ol.View({
    center: [12.5113300, 41.8919300],
    zoom: 12,
	  projection: 'EPSG:4326'

  });

var mousePositionControl = new ol.control.MousePosition({
        coordinateFormat: ol.coordinate.createStringXY(4),
        projection: 'EPSG:4326',
        // comment the following two lines to have the mouse position
        // be placed within the map.
        className: 'custom-mouse-position',
        target: document.getElementById('mouse-position'),
        undefinedHTML: '&nbsp;'
      });

      var map = new ol.Map({
        controls: ol.control.defaults({
          attributionOptions: {
            collapsible: false
          }
        }).extend([mousePositionControl]),
        layers: [
          new ol.layer.Tile({
            source: new ol.source.OSM()
          })
        ],
        target: 'map',
        view: view
      });

      map.on('click', function(event){
			var coor = event.map.getCoordinateFromPixel(event.pixel);
			
			$.ajax({
				type: "GET",
				url: "https://nominatim.openstreetmap.org/reverse",
				data: {
					format: "jsonv2",
					"accept-language": "en",
					lat: coor[1],
					lon: coor[0],
				},
				dataType: "json",
				success: function(response){
					console.log(response);
					console.log(coor);
					alert("Success!");

				},
				error: function(result, status, error){
					console.log("Sorry, an error occurred. Please try again later");
					alert("Sorry, an error occurred. Please try again later");
				}
			})
    });
      
var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');   

// popup
var popup = new ol.Overlay({
    element: container,
    autoPan: true,
    autoPanAnimation: {
      duration: 250
    }
  });

/**
 * Add a click handler to hide the popup.
 * @return {boolean} Don't follow the href.
 */
closer.onclick = function() {
  popup.setPosition(undefined);
  closer.blur();
  return false;
};
map.addOverlay(popup);

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
  	console.info(evt);
  	var coordinate = evt.coordinate;
    var hdms = ol.coordinate.toStringHDMS(ol.proj.transform(
        coordinate, 'EPSG:3857', 'EPSG:4326'));
    flyTo(coordinate, function(){});
    window.setTimeout(function () {
    content.innerHTML = '<p>You are here:</p><code>' +
    					coordinate + '<br>' +
    					evt.address.formatted +
    					'</code>';
    popup.setPosition(coordinate);
    }, 2000);
  });
  
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
      zoom: zoom - 1,
      duration: duration / 2
    }, {
      zoom: zoom,
      duration: duration / 2
    }, callback);
  }
});

