package dataAccess.localDataset;

import org.eclipse.rdf4j.repository.RepositoryConnection;

import model.User;

public class UserGraphDB implements UserRepository {
	
	private RepositoryConnection connection;
	
	public UserGraphDB() {
		LocalDatasetConnection dbConnection = new LocalDatasetConnection();
		this.connection = dbConnection.getGraphDBConnection();
	}

	public void saveUser(User user) {
			
		connection.begin();
		connection.clear();
		
		
		//save data
		
		connection.close();
		
		
	}

}
