package utils;

import java.io.FileWriter;
import java.io.IOException;

public class CsvFileWriter {
	
	//Delimiter used in CSV file
	@SuppressWarnings("unused")
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	public static void writeCsvFile(String fileName, String string) {
		
		FileWriter fileWriter = null;
				
		try {
			fileWriter = new FileWriter(fileName,true);

			//Write new data
			
			fileWriter.append(string);
			fileWriter.append(NEW_LINE_SEPARATOR);
						
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {		
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				
                e.printStackTrace();
                
			}
			
		}
	}
}