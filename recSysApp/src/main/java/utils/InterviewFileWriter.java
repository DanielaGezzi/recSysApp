package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import model.User;

public class InterviewFileWriter {
	
	public static void write(User user, Map<String,Double> w2vNov ,Map<String,Double> w2vDCG, Map<String,Double> lkNov, Map<String,Double> lkDCG) {
		
		System.out.println(w2vNov);
		System.out.println(w2vDCG);
		System.out.println(lkNov);
		System.out.println(lkDCG);
		
		Map<Double,String> noveltyMap = new HashMap<Double,String>();
		noveltyMap.put((double) 0, "I don't know it");
		noveltyMap.put((double) 1, "I know it but I've never seen it");
		noveltyMap.put((double) 2, "I've already seen it");
		
		Map<Double,String> DCGMap = new HashMap<Double,String>();
		DCGMap.put((double) 0, "not interesting");
		DCGMap.put((double) 1, "interesting");
		DCGMap.put((double) 2, "very interesting");
		
		
		PrintWriter writer;
		
		try {
			//intestazione
			File file = new File(user.getId() + ".txt");
			writer = new PrintWriter(new FileOutputStream(file,true));
			
			if(file.length() == 0) {
			
			writer.println("FacebookId = "  + user.getFacebookID());
			writer.println("UserId = " + user.getId());
			writer.println("--------------------------------------------------------------------------------------------");
			
			}
			
			
			writer.println("--- Word2Vect ---");
			writer.println();
			
			double DCG_word2vect = 0;
			double interesting_film_count_w2v = 0;
			double unknown_film_count_w2v = 0;
			double unseen_film_count_w2v = 0;
			for(String s : w2vNov.keySet()) {
				writer.print("idfilm: " + s + " ---> ");
				writer.println("Novelty-score: " + w2vNov.get(s) + " - " + noveltyMap.get(w2vNov.get(s)) +
							   " --- DCG score: " + w2vDCG.get(s) + " - " + DCGMap.get(w2vDCG.get(s)));	
				//novelty
				if(w2vDCG.get(s) == 1 || w2vDCG.get(s) == 2) {
					interesting_film_count_w2v++;
					if(w2vNov.get(s) == 0)
						unknown_film_count_w2v++;
					else if(w2vNov.get(s) == 1)
						unseen_film_count_w2v++;
				}
				
				//dcg
				List<String> keyset = new ArrayList<String>(w2vDCG.keySet());
				DCG_word2vect += w2vDCG.get(s)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2));
				writer.println("DCG film = " + w2vDCG.get(s)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2)));

			}
			
			Map<String, Double> sorted_w2vDCG = 
					w2vDCG.entrySet().stream()
				    .sorted(Entry.<String,Double>comparingByValue().reversed())
				    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
				                              (e1, e2) -> e1, LinkedHashMap::new));
			double IDCG_word2vect = 0;
			for(String s : sorted_w2vDCG.keySet()) {
				List<String> keyset = new ArrayList<String>(sorted_w2vDCG.keySet());
				System.out.println(sorted_w2vDCG.get(s));
				IDCG_word2vect += (Math.pow(2, w2vDCG.get(s))-1)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2));
				writer.println("IDCG film = " + w2vDCG.get(s)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2)));
				writer.println();
			}
			
			writer.println();	
			writer.println("Novelty(u/k items) w2v = " + unknown_film_count_w2v/interesting_film_count_w2v);
			writer.println("Novelty(uu/k items) w2v = " + (unknown_film_count_w2v+unseen_film_count_w2v)/interesting_film_count_w2v);
			writer.println("nDCG w2v = " + DCG_word2vect/IDCG_word2vect);

			writer.println();
			writer.println();
			writer.println();
			
			
			
			writer.println("--- Lenskit ---");
			writer.println();

			double DCG_lenskit = 0;
			double interesting_film_count_lk = 0;
			double unknown_film_count_lk = 0;
			double unseen_film_count_lk = 0;
			for(String s : lkNov.keySet()) {
				writer.print("idfilm: " + s + " ---> ");
				writer.println("Novelty-score: " + lkNov.get(s) + " - " + noveltyMap.get(lkNov.get(s)) +
						       " --- DCG score: " + lkDCG.get(s) + " - " + DCGMap.get(lkDCG.get(s)));

				//novelty
				if(lkDCG.get(s) == 1 || lkDCG.get(s) == 2) {
					interesting_film_count_lk++;
					if(lkNov.get(s) == 0)
						unknown_film_count_lk++;
					else if(lkNov.get(s) == 1)
						unseen_film_count_lk++;
				}
				
				//dcg
				List<String> keyset = new ArrayList<String>(lkDCG.keySet());
				DCG_lenskit += lkDCG.get(s)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2));
				writer.println("DCG film = " + lkDCG.get(s)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2)));

			}
			
			Map<String, Double> sorted_lkDCG = 
					lkDCG.entrySet().stream()
				    .sorted(Entry.<String,Double>comparingByValue().reversed())
				    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
				                              (e1, e2) -> e1, LinkedHashMap::new));
			double IDCG_lenskit = 0;
			for(String s : sorted_lkDCG.keySet()) {
				List<String> keyset = new ArrayList<String>(sorted_lkDCG.keySet());
				System.out.println(sorted_lkDCG.get(s));
				IDCG_lenskit += (Math.pow(2, lkDCG.get(s))-1)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2));
				writer.println("IDCG film = " + lkDCG.get(s)/(Math.log(keyset.indexOf(s)+1+1)/Math.log(2)));
			}
			
			writer.println();	
			writer.println("Novelty(u/k items) lenskit = " + unknown_film_count_lk/interesting_film_count_lk);
			writer.println("Novelty(uu/k items) lenskit = " + (unknown_film_count_lk+unseen_film_count_lk)/interesting_film_count_lk);
			writer.println("nDCG lenskit = " + DCG_lenskit/IDCG_lenskit);

			
			writer.println();
			writer.println();
			writer.println("--------------------------------------------------------------------------------------------");
			writer.println();

			
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
