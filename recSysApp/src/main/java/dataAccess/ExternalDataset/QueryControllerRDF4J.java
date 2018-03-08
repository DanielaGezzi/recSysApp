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
	
	@Override
	public String generateQuery(String endPoint, String location) {
		String queryResult = null;
		if(endPoint == QueryControllerRDF4J.ENDPOINT_LinkedMDB) {
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
	public List<Film> getCandidateFilms(String endPoint, String location) {
		List<Film> result = new ArrayList<Film>();
		String query = generateQuery(endPoint, location);
		SPARQLRepository repository = new SPARQLRepository(endPoint,endPoint);
	    repository.initialize();

		try {	
		    RepositoryConnection connection = repository.getConnection();
		    TupleQueryResult resultSet = connection.prepareTupleQuery(QueryLanguage.SPARQL,query).evaluate();
			
			for (;resultSet.hasNext();) {
			      BindingSet soln = resultSet.next();
			      //RDFNode filmUri = soln.get("film");
			      String filmTitle = soln.getValue("film_label").stringValue();
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

//"title":"\"The_Italian_Job\"^^<http://www.w3.org/2001/XMLSchema#string>"
