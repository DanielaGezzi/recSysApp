package data.externalDataSet;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class LodAccess {
	
	private String endpointLinkedMDB = "http://data.linkedmdb.org/sparql"; //endpoint URL LinkedMDB
	private String endpointDBPedia = "http://data.linkedmdb.org/sparql"; //endpoint URL DBPedia
	private String endpointWikiData = "http://data.linkedmdb.org/sparql"; //endpoint URL WikiData
	
	
	/**
	 * Run SPARQL query 
	 * @param     uri  resource uri
	 */
	protected List<String> executeQuery(String queryString, String endpointURI){
				
			List<String> results = new ArrayList<String>();
			Query query;
			
			queryString = 
					"PREFIX movie: <http://data.linkedmdb.org/resource/movie/>" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
					"SELECT DISTINCT ?film ?film_label ?loc_label " + 
					"WHERE { ?film a movie:film ." + 
					"        ?location a movie:film_location ." + 
					"        ?film movie:featured_film_location ?location ." + 
					"        {?location movie:film_location_name \"Rome\"} UNION {?location movie:film_location_name \"Italy\"}"+
					"		 ?location movie:film_location_name ?loc_label ."+
					"		 ?film rdfs:label ?film_label" +
					"}";
			
			try {	
				query = QueryFactory.create(queryString);
				QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(endpointLinkedMDB, query);
				ResultSet resultSet = qexec.execSelect();
				
				for (;resultSet.hasNext();) {
				      QuerySolution soln = resultSet.nextSolution();
				      RDFNode filmUri = soln.get("film");
				      Literal title = soln.getLiteral("film_label");
				      Literal location = soln.getLiteral("loc_label");
				      byte[] bytes = title.toString().getBytes("ISO_8859_1");
				      String title_decoded = new String(bytes, "UTF-8");
				      results.add(Normalizer.normalize(title_decoded,  Normalizer.Form.NFD).replaceAll(" ", "_"));
				      Path file = Paths.get("src/main/resources/data/LODMovieList.txt");
				      Files.write(file, results, Charset.forName("UTF-8"));
				    }
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
	
			return results;
		}
	
	
	//query wiki data per film con filming location un comune italiano
	/*SELECT ?film ?filmLabel ?location
WHERE {
  SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en". }
  ?film wdt:P31 wd:Q11424.
  ?film wdt:P915 ?location.
  ?location wdt:P31 wd:Q747074
}*/
	
	
}