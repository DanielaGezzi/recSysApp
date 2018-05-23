package facade;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lenskit.data.dao.DataAccessObject;

import dataAccess.LensKit.LensKitDAO;
import dataAccess.LensKit.LensKitRepository;
import model.Film;
import utils.LensKitRecommender;

public class FacadeLensKitImpl implements FacadeLensKit {
	
	public void saveRatings(long userId, Map<String,String> imdbIdScoreList) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Long timestampMillis = timestamp.getTime();
		LensKitRepository lkRepo = new LensKitDAO();
		DataAccessObject dao = lkRepo.loadRemoteData();
		for(String imdbId : imdbIdScoreList.keySet()) {
			double score = Double.parseDouble(imdbIdScoreList.get(imdbId));
			lkRepo.saveRating(userId, imdbId, score, timestampMillis);
		}
		LensKitRecommender lkr = LensKitRecommender.getLensKitRecommender();
		lkr.setLkr(dao);
	}
	
	public List<String> getLogPopularityEntropyFilms() {
		LensKitRepository lkRepo = new LensKitDAO();
		lkRepo.loadData();
		return lkRepo.getLogPopularityEntropyFilms();
	}
	
	public List<String> getRecommendations(long userId, int n, List<Film> filmList){
		LensKitRepository lkRepo = new LensKitDAO();
		List<String> imdbIdList = new ArrayList<String>();
		for(Film f : filmList){
			imdbIdList.add(f.getImdbId().substring(2));
		}
		return lkRepo.getRecommendationsTest(userId, n, imdbIdList);
	}

}
