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
				       .add(FOAF.NAME, fbPage.getName())
				       .add(RDFS.LABEL, fbPage.getName())
				       .add("fbpage:has_vector", fbPage.getVector())
				       .add("fbpage:has_category", "fbcategory:"+ fbPage.getCategory())
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
