package dataAccess.ExternalDataset;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import model.Film;

public class QueryControllerRDF4J implements QueryController {

	public final static String ENDPOINT_LinkedMDB = "http://data.linkedmdb.org/sparql";
	public final static String ENDPOINT_Wikidata = "https://query.wikidata.org/sparql";
	
	@Override
	public String generateQuery(String endPoint, String location) {
		String queryResult = null;
		if(endPoint == QueryControllerRDF4J.ENDPOINT_LinkedMDB) {
			queryResult = 	"PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
						  	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						  	"SELECT ?film ?film_label ?loc_label " + 
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
	
	public String generateQuery(String location) {
		return  "PREFIX lmdb: <http://data.linkedmdb.org/resource/movie/>" + 
				"PREFIX wd: <http://www.wikidata.org/entity/> \r\n" + 
				"PREFIX wdt: <http://www.wikidata.org/prop/direct/>" + 
				"PREFIX wikibase: <http://wikiba.se/ontology#>" +
				"PREFIX bd: <http://www.bigdata.com/rdf#>" +
			  	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
			  	"SELECT DISTINCT ?film ?film_label ?loc_label " + 
				  	"WHERE {" + 
				  		"SERVICE <"+ QueryControllerRDF4J.ENDPOINT_LinkedMDB +"> {" +
				  	"		?film a lmdb:film ." + 
				  	"		?film lmdb:featured_film_location ?location ." + 
				  	"		?location lmdb:film_location_name ?loc_label ." + 
				  	"		FILTER (str(?loc_label) = \""+ location +"\" || str(?loc_label) = \"Italy\" )" +
				  	"	}" + 
				  		"SERVICE <"+ QueryControllerRDF4J.ENDPOINT_Wikidata +"> {" +
						"		SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }" +
						"		?film wdt:P31 wd:Q11424." +
						"		?film wdt:P915 ?location." +
						"		?location rdfs:label ?locationLabel." +
						"		FILTER (str(?locationLabel) = \""+ location +"\")" +				  	
					"	}" + 
				  	"}";
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
				      byte[] bytes = filmTitle.toString().getBytes("ISO_8859_1");
				      String title_decoded = new String(bytes, "UTF-8");
				      Film film = new Film(Normalizer.normalize(title_decoded,  Normalizer.Form.NFD));
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

}
/*
 * 
 * query su LMDB con filter (109)
SELECT ?film (COUNT(?film) as ?count)
WHERE
{ 
?film a movie:film . 
?film movie:featured_film_location ?location .
?location movie:film_location_name ?loc_label .
FILTER (str(?loc_label) = "Rome" || str(?loc_label) = "Italy" )
}


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
* query su wikidata (155)
* 
* PREFIX wikibase: <http://wikiba.se/ontology#>
SELECT ?film ?filmLabel WHERE {
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
  ?film wdt:P31 wd:Q11424.
  ?film wdt:P915 ?location.
  ?location rdfs:label ?locationLabel.
  #FILTER (LANG(?locationLabel) = "en") .
  FILTER (str(?locationLabel) = "Rome")
}


http://sparql.uniprot.org/ uri endpoint comune


SELECT DISTINCT ?film ?filmLabel ?locationLabel
WHERE {
  SERVICE <https://query.wikidata.org/sparql> {
  SELECT ?film ?filmLabel ?locationLabel 
  WHERE{
  SERVICE wikibase:label { bd:serviceParam wikibase:language "en".}
  ?film wdt:P31 wd:Q11424.
  ?film wdt:P915 ?location.
  ?location rdfs:label ?loc_label.
  FILTER(STR(?loc_label) = "Rome")
    }
  }
}ORDER BY ?filmLabel
*/




