package controller;

/**
 * Hello world!
 *
 */
public class Controller 
{
	public void Login() {
		
		//login with fb and retrieve info about user and user likes
	}
	
	public void getProximityRecommendation(/*@PathParam("lat") double latitude, 
											 @PathParam("lon") double longitude*/) {
		
		//take in input lat lon and return movie recommendation related to the poi and a 5km proximity
		//use case: got a monumental/cultural poi, we can recommend documentary/guide/film
		//problem: imdb non contine documentari, wikidata ne contiene pochi geolocaliccati di cui nessuno in italia 
		
	}
	
	public void getAreaRecommendation() {
		//nell area amministrativa
	}
	
	public void getCityRecommendation() {
		//nell area amministrativa
	}
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
