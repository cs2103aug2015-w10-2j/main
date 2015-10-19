package Time4WorkStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CustomPathLogic {
	
	private final String defPath = "pathcustom.txt";
	private File pathFile =  new File(defPath);;
	private FileWriter fw;
	private FileReader fr; 
	private BufferedWriter bw;
	private BufferedReader br;
	
	//returns if custompath file exists
	public boolean savedPathExists() {
		return (pathFile.exists() && pathFile.isFile());
	}
	
	//writes the new path into file
	public void writeCustomPath(String customPath) throws IOException {
		
		customPath = customPath.replace("\\", "\\\\");
		
		try {
			openWriter();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			bw.write(customPath);
			bw.newLine();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			closeWriter();
		} catch (IOException e) {
			throw e;
		}
	}
	
	//returns empty string if doesnt exist or file is empty
	public String readCustomPath() throws IOException {
		
		String savedPath = "";
		
		if(savedPathExists()) {
			try {
				openReader();
			} catch (IOException e) {
				throw e;
			}
			
			try {
				savedPath = br.readLine();
			} catch (IOException e) {
				throw e;
			}
			
			try {
				closeReader();
			} catch (IOException e) {
				throw e;
			}
		} 
		return savedPath;
		
	}
	
	private void openWriter() throws IOException, FileNotFoundException{

		try {
			fw = new FileWriter(pathFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			throw new IOException("Unable to create writer for file");
		}
	}
	
	private void openReader() throws IOException, FileNotFoundException{

		try {
			fr = new FileReader(pathFile.getAbsoluteFile());
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Reader cannot be created. File may be is missing");
		}
	}
	

	private void closeWriter() throws IOException {

			try {
				bw.close();
			} catch (IOException e) {
				throw new IOException("Unable to close writer");
			}
	}
	
	private void closeReader() throws IOException {

		try {
			br.close();
		} catch (IOException e) {
			throw new IOException("Unable to close reader");
		}
}

}
