package dataAccess.localDataset;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

import model.FacebookPage;

public class FacebookPageGraphDB implements FacebookPageRepository {
	
	private Repository repository;
	
	public FacebookPageGraphDB() {
		repository = new HTTPRepository("http://localhost:7200/","Test");
		repository.initialize();
	}

	@Override
	public void saveFacebookPage(FacebookPage fbPage) {
		
		ModelBuilder builder = new ModelBuilder();
		Model model = builder
		                .setNamespace("db", "http://test/resource/")
		                .setNamespace("fbpage","http://test/resource/fbpage/")
		                .setNamespace("fbcategory","http://test/resource/fbcategory/")
				  .subject("fbcategory:"+ fbPage.getCategory())
			  			.add(RDF.TYPE, "db:fbcategory" )
			  			.add(RDFS.LABEL, fbPage.getCategory())
				  .subject("fbpage:" + fbPage.getFacebookID())
				       .add(RDF.TYPE, "db:fbpage")
				       .add("fbpage:fbpageid", fbPage.getFacebookID())
				       .add(FOAF.NAME, fbPage.getName())
				       .add(RDFS.LABEL, fbPage.getName())
				       .add("fbpage:has_category", "fbcategory:"+ fbPage.getCategory())
				  .build();
		
		execute(model);

	}
	
	public List<FacebookPage> getFacebookPageByUser(String userID){
		List<FacebookPage> userLikes = new ArrayList<FacebookPage>();
		String query = 	"PREFIX db: <http://test/resource/>" + 
				"PREFIX user: <http://test/resource/user/>" + 
				"PREFIX fbpage: <http://test/resource/fbpage/>" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + 
				"SELECT ?user ?fbPage ?fbPage_id ?fbPage_label ?fbPage_cat " +
					"WHERE {" + 
					"    ?user rdf:type db:user." + 
					"    ?user user:userid \"" + userID +"\"." + 
					"    ?user user:likes_fbpage ?fbPage." + 
				    "	 ?fbPage fbpage:fbpageid ?fbPage_id." +					
					"    ?fbPage rdfs:label ?fbPage_label." + 
					"    ?fbPage fbpage:has_category ?fbPage_cat" + 
				"}";
		
		try {	
		    RepositoryConnection connection = repository.getConnection();
		    try{
		    	TupleQueryResult resultSet = connection.prepareTupleQuery(QueryLanguage.SPARQL,query).evaluate();
		    
				
				for (;resultSet.hasNext();) {
				      BindingSet soln = resultSet.next();
				      String id = soln.getValue("fbPage_id").stringValue();
				      String name = soln.getValue("fbPage_label").stringValue();
				      String category = soln.getValue("fbPage_cat").stringValue();
				      FacebookPage fbPage = new FacebookPage(id, name, category);
				      userLikes.add(fbPage);
			    }
		    }
			finally{
				connection.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return userLikes;
			
	}
	
	private void execute(Model model) {
			
			try {
				RepositoryConnection connection = repository.getConnection();
				try {
					connection.add(model);
				}
				finally {
					connection.close();
				}
			}catch (RDF4JException e) {
				
				e.printStackTrace();
				
			}catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}

}
