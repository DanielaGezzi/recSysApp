package dataAccess;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import model.Film;


public class QueryControllerSPARQL implements QueryController {
	
	public final static String ENDPOINT_LinkedMDB = "http://data.linkedmdb.org/sparql"; //endPoint URL LinkedMDB
	//private String endPointDBPedia = "http://data.linkedmdb.org/sparql"; //endPoint URL DBPedia
	//private String endPointWikiData = "http://data.linkedmdb.org/sparql"; //endPoint URL WikiData	

	
	private String generateQuery(String endPoint, String location) {
		String queryResult = null;
		if(endPoint == QueryControllerSPARQL.ENDPOINT_LinkedMDB) {
			queryResult = "PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
						  "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						  "SELECT DISTINCT ?film ?film_label ?loc_label " + 
						  "WHERE { ?film a movie:film ." + 
						  "        ?film movie:featured_film_location ?location ." + 
						  "        {?location movie:film_location_name \""+ location +"\"} " +
						  "		   		UNION {?location movie:film_location_name \"Italy\"}"+
						  "		   ?location movie:film_location_name ?loc_label ."+
					   	  "		   ?film rdfs:label ?film_label" +
						  "}";
		}	
		return queryResult;		
	}
	
	@Override
	public List<Film> getCandidateFilms(String endPoint, String location){
		List<Film> result = new ArrayList<Film>();
		String queryString = generateQuery(endPoint, location);
		Query query;
		
		try {	
			query = QueryFactory.create(queryString);
			QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(endPoint, query);
			ResultSet resultSet = qexec.execSelect();
			
			for (;resultSet.hasNext();) {
			      QuerySolution soln = resultSet.nextSolution();
			      //RDFNode filmUri = soln.get("film");
			      Literal filmTitle = soln.getLiteral("film_label");
			      //Literal filmLocation = soln.getLiteral("loc_label");
			      byte[] bytes = filmTitle.toString().getBytes("ISO_8859_1");
			      String title_decoded = new String(bytes, "UTF-8");
			      Film film = new Film(Normalizer.normalize(title_decoded,  Normalizer.Form.NFD).replaceAll(" ", "_"));
			      result.add(film);
			    }
		} 
		catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return result;
	}
	
	

}
