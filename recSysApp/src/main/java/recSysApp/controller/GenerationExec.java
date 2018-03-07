package recSysApp.controller;

import java.util.ArrayList;
import java.util.List;

import dataAccess.QueryController;
import dataAccess.QueryControllerSPARQL;

public class GenerationExec {
	
	/**
	 *  
	 * @param     	
	 * @return list of candidate resources from LOD
	 */
	public List<String> getRelatedResources(String Location) {
		List<String> result = new ArrayList<String>();
		QueryController queryController = new QueryControllerSPARQL();
		result = queryController.getCandidateFilms(QueryControllerSPARQL.ENDPOINT_LinkedMDB, Location);

		return result;
		
	}

}
