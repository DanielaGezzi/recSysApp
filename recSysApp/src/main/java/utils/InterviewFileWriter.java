package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import model.User;

public class InterviewFileWriter {
	
	public static void write(User user, Map<String,Double> w2v, Map<String,Double> lk) {
		
		System.out.println(w2v);
		System.out.println(lk);
		
		PrintWriter writer;
		
		try {
			File file = new File(user.getId() + ".txt");
			writer = new PrintWriter(new FileOutputStream(file,true));
			
			if(file.length() == 0) {
			
			writer.println("FacebookId = "  + user.getFacebookID());
			writer.println("UserId = " + user.getId());
			writer.println("--------------------------------------------------------------------------------------------");
			
			}
			
			double w2vCount = 0;
			double lkCount = 0;
			
			writer.println("--- Word2Vect ---");
			writer.println();

			for(String s : w2v.keySet()) {
				double score = w2v.get(s);
				w2vCount += score;
				writer.print("idfilm: " + s + " ---> ");
				writer.println("score: " + w2v.get(s));				
			}
			
			writer.println();
			writer.println("AVG : " + w2vCount/w2v.size());
			
			writer.println();
			writer.println();
			writer.println();
			
			writer.println("--- Lenskit ---");
			writer.println();

			
			for(String s : lk.keySet()) {
				double score = lk.get(s);
				lkCount += score;
				writer.print("idfilm: " + s + " ---> ");
				writer.println("score: " + lk.get(s));
			}
			
			writer.println();
			writer.println("AVG : " + lkCount/lk.size());
			
			if(lkCount/lk.size() > w2vCount/w2v.size()) {
				writer.println("WINNER: LENSKIT");
			}
			else if(lkCount/lk.size() < w2vCount/w2v.size()) {
				writer.println("WINNER: WORD2VECT");
			}
			else {
				writer.println("WINNER: TIE");
			}
			
			writer.println();
			writer.println("--------------------------------------------------------------------------------------------");
			writer.println();

			
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
