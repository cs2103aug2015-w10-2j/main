package Time4WorkStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* @@author A0125495Y */

public class CustomPathLogic {
	
	private final String defPath = "pathcustom.txt";
	private File pathFile =  new File(defPath);
	private FileWriter fw;
	private FileReader fr; 
	private BufferedWriter bw;
	private BufferedReader br;
	
	private static final String MSG_WRITEROPEN_FAILED = "Unable to create writer for file";
	private static final String MSG_READEROPEN_FAILED = "Unable to create reader for file";
	private static final String MSG_WRITERCLOSE_FAILED = "Unable to close writer";
	private static final String MSG_READERCLOSE_FAILED = "Unable to close reader";
	private static final String MSG_FILEDELETION_FAILED = "Unable to delete file";
	private static final String BACKSLASH = "\\";
	

	
	//returns if custompath file exists
	public boolean savedPathExists() {
		return (pathFile.exists() && pathFile.isFile());
	}
	
	//writes the new path into file
	public void writeCustomPath(String customPath) throws IOException {
		
		customPath = customPath.replace(BACKSLASH, BACKSLASH+BACKSLASH);
		
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
	
	//delete path file
	public void deleteCustomPathFile() throws IOException {
		
		try {
			closeReader();
			closeWriter();
			fr.close();
			br.close();
		} catch (IOException e) {
			throw e;
		}
		
		System.gc();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (!pathFile.delete()) {
			throw new IOException(MSG_FILEDELETION_FAILED);
	    } 
	}
	
	private void openWriter() throws IOException, FileNotFoundException{

		try {
			fw = new FileWriter(pathFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			throw new IOException(MSG_WRITEROPEN_FAILED);
		}
	}
	
	private void openReader() throws IOException, FileNotFoundException{

		try {
			fr = new FileReader(pathFile.getAbsoluteFile());
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(MSG_READEROPEN_FAILED);
		}
	}	

	private void closeWriter() throws IOException {

			try {
				bw.close();
			} catch (IOException e) {
				throw new IOException(MSG_WRITERCLOSE_FAILED);
			}
	}
	
	private void closeReader() throws IOException {

		try {
			br.close();
		} catch (IOException e) {
			throw new IOException(MSG_READERCLOSE_FAILED);
		}
	}

}
