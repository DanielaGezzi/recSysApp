$(document).ready(function(){


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
        view: new ol.View({
          center: [12.5113300, 41.8919300],
          zoom: 12,
      	  projection: 'EPSG:4326'

        })
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
					alert("Success!");

				},
				error: function(result, status, error){
					console.log("Sorry, an error occurred. Please try again later");
					alert("Sorry, an error occurred. Please try again later");
				}
			})
    });
      
});