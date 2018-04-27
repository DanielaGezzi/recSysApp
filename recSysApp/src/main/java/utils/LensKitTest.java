package utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lenskit.LenskitConfiguration;
import org.lenskit.LenskitRecommender;
import org.lenskit.LenskitRecommenderEngine;
import org.lenskit.api.ItemRecommender;
import org.lenskit.api.ItemScorer;
import org.lenskit.api.RatingPredictor;
import org.lenskit.api.Recommender;
import org.lenskit.api.RecommenderBuildException;
import org.lenskit.api.Result;
import org.lenskit.api.ResultList;
import org.lenskit.baseline.BaselineScorer;
import org.lenskit.baseline.ItemMeanRatingItemScorer;
import org.lenskit.baseline.UserMeanBaseline;
import org.lenskit.baseline.UserMeanItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.data.entities.EntityType;
import org.lenskit.knn.item.ItemItemScorer;
import org.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.lenskit.transform.normalize.UserVectorNormalizer;

import com.google.common.base.Throwables;

public class LensKitTest {

	public static void test() {
		Path dataFile = Paths.get("src/main/resources/movielens.yml");
		String delimiter = ",";
		
		LenskitConfiguration config = new LenskitConfiguration();
		// Use item-item CF to score items
		config.bind(ItemScorer.class)
		      .to(ItemItemScorer.class);
		// let's use personalized mean rating as the baseline/fallback predictor.
		// 2-step process:
		// First, use the user mean rating as the baseline scorer
		config.bind(BaselineScorer.class, ItemScorer.class)
		      .to(UserMeanItemScorer.class);
		// Second, use the item mean rating as the base for user means
		config.bind(UserMeanBaseline.class, ItemScorer.class)
		      .to(ItemMeanRatingItemScorer.class);
		// and normalize ratings by baseline prior to computing similarities
		config.bind(UserVectorNormalizer.class)
		      .to(BaselineSubtractingUserVectorNormalizer.class);
		
		DataAccessObject dao;
        try {
            StaticDataSource data = StaticDataSource.load(dataFile);
            // get the data from the DAO
            dao = data.get();
        } catch (IOException e) {
            System.out.println("cannot load data" + e);
            throw Throwables.propagate(e);
        }
		
        LenskitRecommenderEngine engine = LenskitRecommenderEngine.build(config, dao);
        try (LenskitRecommender rec = engine.createRecommender(dao)) {
        	ItemRecommender irec = rec.getItemRecommender();
        	assert irec != null; // not null because we configured one
        	ResultList recs = irec.recommendWithDetails(42, 10, null, null);
        	System.out.format("Recommendations for %d:\n", 42);
            for (Result item : recs) {
            	Entity itemData = dao.lookupEntity(CommonTypes.ITEM, item.getId());
            	Entity itemId = dao.lookupEntity(EntityType.forName("item-ids"), item.getId());
            	String name = null;
                long imdbID = -1;
                if (itemData != null) {
                    name = itemData.maybeGet(CommonAttributes.NAME);
                    imdbID = (Long) itemId.maybeGet("imdbid");
                }
                System.out.format("\t%d (%s): %d %.2f\n", item.getId(), name, imdbID, item.getScore());
            }
		} catch (RecommenderBuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
	}						
	
		
	public static void main(String[] args){
		test();
	}
}
