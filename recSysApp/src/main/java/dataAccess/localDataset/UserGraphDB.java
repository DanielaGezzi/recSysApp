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
		repository = new HTTPRepository("http://localhost:7200/","recsysapp");
		repository.initialize();
	}
	

	public void saveUser(User user) {

		ModelBuilder builder = new ModelBuilder();
		Model model = builder
		                  .setNamespace("db", "http://recsysapp/resource/")
		                  .setNamespace("user","http://recsysapp/resource/user/")
				  .subject("user:" + user.getFacebookID())
				       .add(RDF.TYPE, "db:user")
				       .add("user:userId", user.getId())
				       .add("user:userFbId", user.getFacebookID())
				       .add(RDFS.LABEL, user.getName() + " " + user.getSurname())
				       .add(FOAF.FIRST_NAME, user.getName())
				       .add(FOAF.SURNAME, user.getSurname())
				  .build();
		
		execute(model);
		
	}
	
	public User getUser(String userFbId) {
		User user = null;
		String query = "PREFIX db: <http://recsysapp/resource/>" + 
					   "PREFIX user: <http://recsysapp/resource/user/>" + 
					   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + 
					   "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
					   "SELECT ?user ?user_id ?label" +
							"WHERE {" + 
							"    ?user rdf:type db:user." + 
							"    ?user user:userFbId \"" + userFbId +"\"." + 
							"    ?user user:userId ?user_id." + 
							"    ?user rdfs:label ?label." + 
					   "}";
		
		try {	
		    RepositoryConnection connection = repository.getConnection();
		    try{
		    	TupleQueryResult resultSet = connection.prepareTupleQuery(QueryLanguage.SPARQL,query).evaluate();
		    		
				for (;resultSet.hasNext();) {
				      BindingSet soln = resultSet.next();
				      String id = soln.getValue("user_id").stringValue();
				      user = new User();
				      user.setFacebookID(userFbId);
				      user.setId(id);
			    }
		    }
			finally{
				connection.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public List<String> getUsersIds(){
		List<String> idsList = new ArrayList<String>();
		String query = "PREFIX user: <http://recsysapp/resource/user/>" +  
					   "SELECT DISTINCT ?user ?user_id" +
						"	WHERE {" + 
						"    ?user user:userId ?user_id." + 
						"} ORDER BY ?user_id";
		
		try {	
		    RepositoryConnection connection = repository.getConnection();
		    try{
		    	TupleQueryResult resultSet = connection.prepareTupleQuery(QueryLanguage.SPARQL,query).evaluate();
				for (;resultSet.hasNext();) {
				      BindingSet soln = resultSet.next();
				      String id = soln.getValue("user_id").stringValue();
				      idsList.add(id);
			    }
		    }
			finally{
				connection.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return idsList;
	}

	public void saveUserLike(String userFbID, String facebookPageID) {
		
		ModelBuilder builder = new ModelBuilder();
		Model model = builder
		                  .setNamespace("fbpage", "http://recsysapp/resource/fbpage/")
		                  .setNamespace("user","http://recsysapp/resource/user/")
				  .subject("user:" + userFbID)
				       .add("user:likes_fbpage", "fbpage:"+facebookPageID)
				  .build();
		
		execute(model);
		
	}
	
	public List<FacebookPage> getUserLikes(String userFbID){
		List<FacebookPage> userLikes = new ArrayList<FacebookPage>();
		String query = 	"PREFIX db: <http://recsysapp/resource/>" + 
						"PREFIX user: <http://recsysapp/resource/user/>" + 
						"PREFIX fbpage: <http://recsysapp/resource/fbpage/>" + 
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + 
						"SELECT ?user ?fbPage ?fbPage_id ?fbPage_label ?fbPage_cat " +
							"WHERE {" + 
							"    ?user rdf:type db:user." + 
							"    ?user user:userFbId \"" + userFbID +"\"." + 
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
