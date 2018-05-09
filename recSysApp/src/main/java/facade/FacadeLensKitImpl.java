package facade;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import dataAccess.LensKit.LensKitDAO;
import dataAccess.LensKit.LensKitRepository;

public class FacadeLensKitImpl implements FacadeLensKit {
	
	public void saveRatings(Long userId, Map<String,Double> imdbIdScoreList) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Long timestampMillis = timestamp.getTime();
		LensKitRepository lkRepo = new LensKitDAO();
		lkRepo.loadData();
		for(String imdbId : imdbIdScoreList.keySet()) {
			lkRepo.saveRating(userId, imdbId, imdbIdScoreList.get(imdbId), timestampMillis);
		}
	}
	
	public List<String> getLogPopularityEntropyFilms() {
		LensKitRepository lkRepo = new LensKitDAO();
		lkRepo.loadData();
		return lkRepo.getLogPopularityEntropyFilms();
	}

}
