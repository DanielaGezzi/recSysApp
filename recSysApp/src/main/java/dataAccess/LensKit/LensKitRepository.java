package dataAccess.LensKit;

import java.util.List;

public interface LensKitRepository {
	
	void loadData();
	void saveRating(Long userId, String imdbId, double rating, Long timestamp);
	List<String> getRecommendations(long userId, int n, List<String> imdbIdList);
	List<String> getLogPopularityEntropyFilms();
	double getLogPopularityEntropyScore(Long movieId);
	double getEntropyScore(Long movieId);
	double getPopularityScore(Long movieId);

}
