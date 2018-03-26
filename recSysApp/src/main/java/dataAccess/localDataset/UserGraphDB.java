package dataAccess.localDataset;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

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
