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
import model.Location;
import utils.RegexHelper;

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
	
	private List<String> generateQuery(String endPoint, String latitude, String longitude, String name, String state, String city, String country) {
		List<String> queryList = new ArrayList<String>();
		
		/*WikiData endPoint queries*/
		if(endPoint == QueryControllerRDF4J.ENDPOINT_Wikidata) {
			
			//distance from coordinates in a radius of 5km
			String queryByDistance = "SELECT DISTINCT ?film ?imdbID ?filmLabel " + 
									 "  WHERE {" + 
									 "   	?film wdt:P915 ?place." + 
									 "   	?film wdt:P345 ?imdbID." + 
									 "   	SERVICE wikibase:around {" + 
									 "    		?place wdt:P625 ?locationCoord." + 
									 "    		bd:serviceParam wikibase:center ?loc." + 
									 "   	 	bd:serviceParam wikibase:radius \"5\"." + 
									 "   	}" + 
									 "  		VALUES (?loc) {(\"Point("+ longitude +" "+ latitude +")\"^^geo:wktLiteral)}" + 
									 "   	SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" + 
									 "   	BIND(geof:distance(?loc, ?locationCoord) AS ?dist)" + 
									 "} ORDER BY ?dist";
			
			queryList.add(queryByDistance);
			
			//filter by locations name usually [Place name, City, State, Nation]

			String queryByLocation = "SELECT DISTINCT ?film ?imdbID ?filmLabel {" + 
									 "	{" +
									 "		SELECT ?film ?imdbID {"+
									 "  		?film wdt:P915 ?place." + 
									 "  		?film wdt:P345 ?imdbID." + 
									 "  		?place rdfs:label ?locationLabel." + 
									 "  		FILTER ( str(?locationLabel) = \""+ name +"\" || " +
									 " 					 str(?locationLabel) = \""+ city +"\" ||" + 
									 " 					 str(?locationLabel) = \""+ state +"\" ||" + 
									 " 					 str(?locationLabel) = \""+ country +"\")" + 
									 "		}"+									 
									 "	}"+
									 "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" + 
									 
									 "}";
				
			queryList.add(queryByLocation);
			
			
		}//end of wikidata endPoint queries
		
		/*LinkedMDB endPoint queries*/
		if(endPoint == QueryControllerRDF4J.ENDPOINT_LinkedMDB) {

			String queryLmdb = 	"PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
						  		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						  		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
						  		"SELECT ?film ?imdbID ?filmLabel " + 
							  	"WHERE {" + 
							  	"		?film a movie:film ." + 
							  	"		?film movie:featured_film_location ?location ." + 
							  	"		?location movie:film_location_name ?placeLabel ." + 
							  	"		?film rdfs:label ?filmLabel ." +
								"  		FILTER ( str(?placeLabel) = \""+ name +"\" || " +
								" 				 str(?placeLabel) = \""+ city +"\" ||" + 
								" 				 str(?placeLabel) = \""+ state +"\" ||" + 
								" 				 str(?placeLabel) = \""+ country +"\")" + 
								"		?film foaf:page ?imdbID ." +
								"		FILTER regex(str(?imdbID), \"imdb.com\")" +
								"}";
		
			queryList.add(queryLmdb);
			
			
		}
		
		return queryList;

	}

	public List<Film> getCandidateFilms(String latitude, String longitude, String name, String city, String state, String country){
		Map<String, Film> filmMap = new LinkedHashMap<String, Film>();
		List<String> queryListWikiData = generateQuery(QueryControllerRDF4J.ENDPOINT_Wikidata, latitude, longitude, name, city, state, country);
		List<String> queryListLMDB = generateQuery(QueryControllerRDF4J.ENDPOINT_LinkedMDB, latitude, longitude, name, city, state, country);

		for(String query : queryListWikiData) {
			TupleQueryResult resultSet = evaluateQuery(QueryControllerRDF4J.ENDPOINT_Wikidata, query);
			if (resultSet!= null) {
				filmMap = mergeResults(filmMap, resultSet);
			}
		}
		
		for(String query : queryListLMDB) {
			TupleQueryResult resultSet = evaluateQuery(QueryControllerRDF4J.ENDPOINT_LinkedMDB, query);
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
		      System.out.println(soln);
		      String imdbID = RegexHelper.getRegexString("tt[0-9]+", soln.getValue("imdbID").stringValue()).get(0); //imdbID	
		      //String imdbID = soln.getValue("imdbID").stringValue();
		      if (!filmMap.containsKey(imdbID)) {
			      String filmTitle = soln.getValue("filmLabel").stringValue(); //Title
			      try {
					filmTitle = new String(filmTitle.toString().getBytes("ISO_8859_1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      Film film = new Film(imdbID, Normalizer.normalize(filmTitle,  Normalizer.Form.NFD));
			      filmMap.put(imdbID, film);
		      }
		    }
		
		return filmMap;
		
	}
	
	/*@Override
	public String generateQueryOld(String endPoint, String location) {
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
	}*/
	
	/*@Override
	public List<Film> getCandidateFilmsOld(String endPoint, String location) {
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
	}*/
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
->>>>>>> su grandi moli di film va in time out
"SELECT DISTINCT ?film ?imdbID ?filmLabel ?placeLabel ?dist WHERE {" + 
									 "  		SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" + 
									 "  		?film wdt:P915 ?place." + 
									 "  		?film wdt:P345 ?imdbID." + 
									 "  		VALUES (?loc) {(\"Point("+ longitude +" "+ latitude +")\"^^geo:wktLiteral)}" + 
									 "  		?place rdfs:label ?locationLabel." + 
									 "  		FILTER ( str(?locationLabel) = \""+ name +"\" || " +
									 " 					 str(?locationLabel) = \""+ city +"\" ||" + 
									 " 					 str(?locationLabel) = \""+ state +"\" ||" + 
									 " 					 str(?locationLabel) = \""+ country +"\")" + 
									 "  		?place wdt:P625 ?locationCoord." + 
									 "  		BIND(geof:distance(?loc, ?locationCoord) AS ?dist)" + 
									 "}ORDER BY ?dist";
									 
									 
"SELECT DISTINCT ?film ?imdbID ?filmLabel ?placeLabel ?dist WHERE {" + 
									 "	{"+
									 "  		SELECT ?film ?imdbID ?place{ " +
									 "				?film wdt:P915 ?place." + 
									 "  			?film wdt:P345 ?imdbID." + 
									 "  			?place rdfs:label ?locationLabel." + 
									 "  			FILTER ( str(?locationLabel) = \""+ name +"\" || " +
									 " 					 	str(?locationLabel) = \""+ city +"\" ||" + 
									 " 					 	str(?locationLabel) = \""+ state +"\" ||" + 
									 " 					 	str(?locationLabel) = \""+ country +"\")" + 
									 "			}" +
									 "  		?place wdt:P625 ?locationCoord." + 
									 "  		VALUES (?loc) {(\"Point("+ longitude +" "+ latitude +")\"^^geo:wktLiteral)}" + 
									 "  		BIND(geof:distance(?loc, ?locationCoord) AS ?dist)" + 
									 "  		SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" + 
									 " }"+									 
									 "}ORDER BY ?dist";


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




