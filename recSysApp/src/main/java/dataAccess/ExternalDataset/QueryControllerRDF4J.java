package dataAccess.ExternalDataset;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import model.Film;

public class QueryControllerRDF4J implements QueryController {

	public final static String ENDPOINT_LinkedMDB = "http://data.linkedmdb.org/sparql";
	public final static String ENDPOINT_Wikidata = "https://query.wikidata.org/sparql";
	
	private TupleQueryResult evaluateQuery(String endpoint, String query) {
		TupleQueryResult resultSet = null;
		SPARQLRepository repository = new SPARQLRepository(endpoint,endpoint);
	    repository.initialize();
	    
		try {	
		    RepositoryConnection connection = repository.getConnection();
		    try{
		    	resultSet = connection.prepareTupleQuery(QueryLanguage.SPARQL,query).evaluate();
		    }finally{
					connection.close();
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		
		return resultSet;
	}
	
	@Override
	public String generateQuery(String endPoint, String location) {
		String queryResult = null;
		if(endPoint == QueryControllerRDF4J.ENDPOINT_LinkedMDB) {
			queryResult = 	"PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
						  	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						  	"SELECT ?film ?film_label ?loc_label ?genre_label" + 
							  	"WHERE {" + 
							  	"		?film a movie:film ." + 
							  	"		?film movie:featured_film_location ?location ." + 
							  	"		?location movie:film_location_name ?loc_label ." + 
							  	"		?film rdfs:label ?film_label" +
							  	"		FILTER (str(?loc_label) = \""+ location +"\" || str(?loc_label) = \"Italy\" )" +
							  	"}";
		}
		else if(endPoint == QueryControllerRDF4J.ENDPOINT_Wikidata) {
			queryResult = 	"PREFIX wd: <http://www.wikidata.org/entity/> \r\n" + 
							"PREFIX wdt: <http://www.wikidata.org/prop/direct/>" +
							"PREFIX wikibase: <http://wikiba.se/ontology#>" +
							"PREFIX bd: <http://www.bigdata.com/rdf#>" +
						  	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
							"SELECT ?film ?filmLabel ?locationLabel"  +
								"WHERE {" +
								"		SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" +
								"		?film wdt:P31 wd:Q11424." +
								"		?film wdt:P915 ?location." +
								"		?location rdfs:label ?locationLabel." +
								"		FILTER (str(?locationLabel) = \""+ location +"\")" +
								"}";
		}
		return queryResult;	
	}
	
	public List<String> generateQuery(String endPoint, String latitude, String longitude, List<String> locationList) {
		List<String> queryList = new ArrayList<String>();
		
		/*WikiData endPoint queries*/
		if(endPoint == QueryControllerRDF4J.ENDPOINT_Wikidata) {
			
			//distance from coordinates in a radius of 2km
			String queryByDistance = "SELECT DISTINCT ?film ?imdbID ?filmLabel ?placeLabel ?dist" + 
									 "  WHERE {" + 
									 "   	?film wdt:P915 ?place." + 
									 "   	?film wdt:P345 ?imdbID." + 
									 "   	SERVICE wikibase:around {" + 
									 "    		?place wdt:P625 ?locationCoord." + 
									 "    		bd:serviceParam wikibase:center ?loc." + 
									 "   	 	bd:serviceParam wikibase:radius \"2\"." + 
									 "   	}" + 
									 "  		VALUES (?loc) {(\"Point("+ longitude +" "+ latitude +")\"^^geo:wktLiteral)}" + 
									 "   	SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" + 
									 "   	BIND(geof:distance(?loc, ?locationCoord) AS ?dist)" + 
									 "} ORDER BY ?dist";
			
			queryList.add(queryByDistance);
			
			//filter by locations name usually [City, Region, Nation]
			if(!locationList.isEmpty()) {
				String filterContent = "str(?locationLabel) = \""+ locationList.get(0) +"\" ";
				for(ListIterator<String> lit = locationList.listIterator(1); lit.hasNext();) {
					filterContent.concat("|| str(?locationLabel) = \""+ lit.next() +"\" ");	
				}
				String queryByLocation = "SELECT DISTINCT ?film ?imdbID ?filmLabel ?placeLabel ?dist WHERE {" + 
						"  		SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" + 
						"  		?film wdt:P915 ?place." + 
						"  		?film wdt:P345 ?imdbID." + 
						"  		VALUES (?loc) {(\"Point("+ longitude +" "+ latitude +")\"^^geo:wktLiteral)}" + 
						"  		?place rdfs:label ?locationLabel." + 
						"  		FILTER (" + filterContent + ")" + 
						"  		?place wdt:P625 ?locationCoord." + 
						"  		BIND(geof:distance(?loc, ?locationCoord) AS ?dist)" + 
						"	}ORDER BY ?dist";
				
				queryList.add(queryByLocation);
			}
			
		}//end of wikidata endPoint queries
		
		/*LinkedMDB endPoint queries*/
		if(endPoint == QueryControllerRDF4J.ENDPOINT_LinkedMDB) {
			if(!locationList.isEmpty()) {
				String filterStr = "str(?loc_label) = \""+ locationList.get(0) +"\" ";
				for(ListIterator<String> lit = locationList.listIterator(1); lit.hasNext();) {
					filterStr.concat("|| str(?loc_label) = \""+ lit.next() +"\" ");	
				}
				String queryLmdb = 	"PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
							  		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
							  		"SELECT ?film ?film_label ?loc_label" + 
								  	"WHERE {" + 
								  	"		?film a movie:film ." + 
								  	"		?film movie:featured_film_location ?location ." + 
								  	"		?location movie:film_location_name ?loc_label ." + 
								  	"		?film rdfs:label ?film_label" +
								  	"		FILTER ("+ filterStr +")" +
								  	"}";
			
				queryList.add(queryLmdb);
			
			}
		}
		
		return queryList;

	}

	@Override
	public List<Film> getCandidateFilms(String endPoint, String location) {
		List<Film> result = new ArrayList<Film>();
		String query = generateQuery(endPoint, location);
		SPARQLRepository repository = new SPARQLRepository(endPoint,endPoint);
	    repository.initialize();

		try {	
		    RepositoryConnection connection = repository.getConnection();
		    try{
		    	TupleQueryResult resultSet = connection.prepareTupleQuery(QueryLanguage.SPARQL,query).evaluate();
			
				for (;resultSet.hasNext();) {
				      BindingSet soln = resultSet.next();
				      String filmTitle = soln.getValue("film_label").stringValue();
				      List<String> filmLocation = new ArrayList<String>();
				      filmLocation.add(soln.getValue("loc_label").stringValue());
				      byte[] bytes = filmTitle.toString().getBytes("ISO_8859_1");
				      String title_decoded = new String(bytes, "UTF-8");
				      Film film = new Film(Normalizer.normalize(title_decoded,  Normalizer.Form.NFD), filmLocation);
				      result.add(film);
				    }
		    }
			finally{
				connection.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return result;
	}

	public List<Film> getCandidateFilmsTest(String latitude, String longitude, List<String> locationList){
		Map<String, Film> filmMap = new LinkedHashMap<String, Film>();
		List<String> queryListWikiData = generateQuery(QueryControllerRDF4J.ENDPOINT_Wikidata, latitude, longitude, locationList);
		List<String> queryListLMDB = generateQuery(QueryControllerRDF4J.ENDPOINT_LinkedMDB, latitude, longitude, locationList);

		for(String query : queryListWikiData) {
			TupleQueryResult resultSet = evaluateQuery(QueryControllerRDF4J.ENDPOINT_Wikidata, queryListWikiData.get(0));
			if (resultSet!= null) {
				filmMap = mergeResults(filmMap, resultSet);
			}
		}
		List<Film> resultList = new ArrayList<Film>(filmMap.values());  
		return resultList;
	}
	
	private Map<String, Film> mergeResults(Map<String,Film> filmMap, TupleQueryResult resultSet){
		
		for (;resultSet.hasNext();) {
		      BindingSet soln = resultSet.next();
		      String imdbID = soln.getValue("imdbID").stringValue(); //imdbID
		      if (!filmMap.containsKey(imdbID)) {
			      String filmTitle = soln.getValue("filmLabel").stringValue(); //Title
			      try {
					filmTitle = new String(filmTitle.toString().getBytes("ISO_8859_1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      List<String> filmLocation = new ArrayList<String>();
			      filmLocation.add(soln.getValue("placeLabel").stringValue()); //Location
			      double distance = Double.parseDouble(soln.getValue("dist").stringValue()); //Distance
			      Film film = new Film(imdbID, Normalizer.normalize(filmTitle,  Normalizer.Form.NFD), filmLocation, distance);
			      filmMap.put(imdbID, film);
		      } else {
		    	  List<String> filmLocation = filmMap.get(imdbID).getFilmingLocation();
		    	  if(!filmLocation.contains(soln.getValue("placeLabel").stringValue()))
		    		  filmLocation.add(soln.getValue("placeLabel").stringValue());
		    	  filmMap.get(imdbID).setFilmingLocation(filmLocation);
		      }
		    }
		
		return filmMap;
		
	}
}







/*
 * 
 ***linkedmdb*************
 "PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
						  	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						  	"SELECT ?film ?film_label ?loc_label ?genre_label" + 
							  	"WHERE {" + 
							  	"		?film a movie:film ." + 
							  	"		?film movie:featured_film_location ?location ." + 
							  	"		?location movie:film_location_name ?loc_label ." + 
							  	"		?film rdfs:label ?film_label" +
							  	"		FILTER (str(?loc_label) = \""+ location +"\" || str(?loc_label) = \"Italy\" )" +
							  	"		OPTIONAL {" + 
							  	"				  ?film movie:genre ?genre.\r\n" + 
							  	"				  ?genre movie:film_genre_name ?genre_label.\r\n" + 
							  	"		}"+
							  	"}";


"SELECT DISTINCT ?film ?film_label ?loc_label " + 
						  	"WHERE { ?film a movie:film ." + 
						  	"        ?film movie:featured_film_location ?location ." + 
						  	"        {?location movie:film_location_name \""+ location +"\"} " +
						  	"		   		UNION {?location movie:film_location_name \"Italy\"}"+
						  	"		   ?location movie:film_location_name ?loc_label ."+
						  	"		   ?film rdfs:label ?film_label" +
						  	"}"

*
*
*** query su wikidata (155)************************
* 
PREFIX wikibase: <http://wikiba.se/ontology#>
SELECT ?film ?filmLabel WHERE {
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
  ?film wdt:P31 wd:Q11424.
  ?film wdt:P915 ?location.
  ?location rdfs:label ?locationLabel.
  #FILTER (LANG(?locationLabel) = "en") .
  FILTER (str(?locationLabel) = "Rome")
}

***i film con location cinematograica entro 1km dalle coordinate della fonta di trevi
***PREFIX geof: <http://www.opengis.net/def/geosparql/function/>

SELECT ?film ?filmLabel ?locationCoord ?instanceLabel WHERE {
  wd:Q185382 wdt:P625 ?loc.
  ?film wdt:P915 ?place.
  SERVICE wikibase:around {
    ?place wdt:P625 ?locationCoord.
    bd:serviceParam wikibase:center ?loc.
    bd:serviceParam wikibase:radius "1".
  }
  #OPTIONAL { ?place wdt:P31 ?instance. }
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
  BIND(geof:distance(?loc, ?locationCoord) AS ?dist)
}
ORDER BY ?dist

***con coordinate

SELECT DISTINCT ?film ?imdbID ?filmLabel ?placeLabel ?dist WHERE {
  #wd:Q185382 wdt:P625 ?loc.
  ?film wdt:P915 ?place.
  ?film wdt:P345 ?imdbID.
  SERVICE wikibase:around {
    ?place wdt:P625 ?locationCoord.
    bd:serviceParam wikibase:center ?loc.
    bd:serviceParam wikibase:radius "3".
  }
  VALUES (?loc) {("Point(12.483166666667 41.900875)"^^geo:wktLiteral)}    
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
  BIND(geof:distance(?loc, ?locationCoord) AS ?dist)
}
ORDER BY ?dist

**federate queries
http://sparql.uniprot.org/ uri endpoint comune


PREFIX up:<http://purl.uniprot.org/core/> 
PREFIX keywords:<http://purl.uniprot.org/keywords/> 
PREFIX uniprotkb:<http://purl.uniprot.org/uniprot/> 
PREFIX taxon:<http://purl.uniprot.org/taxonomy/> 
PREFIX ec:<http://purl.uniprot.org/enzyme/> 
PREFIX skos:<http://www.w3.org/2004/02/skos/core#> 
PREFIX owl:<http://www.w3.org/2002/07/owl#> 
PREFIX bibo:<http://purl.org/ontology/bibo/> 
PREFIX dc:<http://purl.org/dc/terms/> 
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> 
PREFIX faldo:<http://biohackathon.org/resource/faldo#> 
PREFIX lmdb: <http://data.linkedmdb.org/resource/movie/>
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX wdt: <http://www.wikidata.org/prop/direct/>
PREFIX wikibase: <http://wikiba.se/ontology#>
PREFIX bd: <http://www.bigdata.com/rdf#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT DISTINCT ?film (?filmLabel AS ?filmlab) (?locationLabel AS ?loclab)
WHERE {
  {SERVICE <https://query.wikidata.org/sparql> {
    SELECT ?film ?filmLabel ?locationLabel 
      WHERE{
        SERVICE wikibase:label { bd:serviceParam wikibase:language "en".}
        ?film wdt:P31 wd:Q11424.
        ?film wdt:P915 ?location.
        ?location rdfs:label ?loc_label.
        FILTER(STR(?loc_label) = "Rome")
      }
  }}UNION
  {SERVICE <http://data.linkedmdb.org/sparql> {
    SELECT ?film ?filmLabel ?locationLabel 
      WHERE{
        ?film a lmdb:film .
        ?film lmdb:featured_film_location ?location .
        ?location lmdb:film_location_name ?locationLabel .
        ?film rdfs:label ?filmLabel .
        FILTER (str(?locationLabel) = "Rome" || str(?locationLabel) = "Italy" )
      }
  }}
}ORDER BY ?filmlab
*/




