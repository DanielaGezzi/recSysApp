package dataAccess.localDataset;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;


public class LocalDatasetConnection {
	
	public RepositoryConnection getGraphDBConnection() {

	HTTPRepository repository = new HTTPRepository("http://localhost:7200/graphdb/repositories/Test");
    RepositoryConnection connection = repository.getConnection();
	
    return connection;
	
	}

	
}
