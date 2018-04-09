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
import model.User;

public class UserGraphDB implements UserRepository {
	
	private Repository repository;
	
	public UserGraphDB() {
		repository = new HTTPRepository("http://localhost:7200/","Test");
		repository.initialize();
	}
	

	public void saveUser(User user) {

		ModelBuilder builder = new ModelBuilder();
		Model model = builder
		                  .setNamespace("db", "http://test/resource/")
		                  .setNamespace("user","http://test/resource/user/")
				  .subject("user:" + user.getFacebookID())
				       .add(RDF.TYPE, "db:user")
				       .add("user:userid", user.getFacebookID())
				       .add(RDFS.LABEL, user.getName() + " " + user.getSurname())
				       .add(FOAF.FIRST_NAME, user.getName())
				       .add(FOAF.SURNAME, user.getSurname())
				  .build();
		
		execute(model);
		
	}

	public void saveUserLike(String userid, String facebookID) {
		
		ModelBuilder builder = new ModelBuilder();
		Model model = builder
		                  .setNamespace("fbpage", "http://test/resource/fbpage/")
		                  .setNamespace("user","http://test/resource/user/")
				  .subject("user:" + userid)
				       .add("user:likes_fbpage", "fbpage:"+facebookID)
				  .build();
		
		execute(model);
		
	}
	
	public List<FacebookPage> getUserLikes(String userID){
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
