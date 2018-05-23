package dataAccess.LensKit;

import java.util.List;

import org.lenskit.data.dao.DataAccessObject;

public interface LensKitRepository {
	
	void loadData();
	DataAccessObject loadRemoteData();
	void saveRating(Long userId, String imdbId, double rating, Long timestamp);
	List<String> getRecommendations(long userId, int n, List<String> imdbIdList);
	List<String> getRecommendationsTest(long userId, int n, List<String> imdbIdList);
	List<String> getLogPopularityEntropyFilms();
	double getLogPopularityEntropyScore(Long movieId);
	double getEntropyScore(Long movieId);
	double getPopularityScore(Long movieId);

}
