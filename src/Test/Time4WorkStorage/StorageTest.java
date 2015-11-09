package Test.Time4WorkStorage;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import Time4WorkStorage.Storage;

public class StorageTest {
	
	Storage myStorage = null;
	
	@Test
	public void testCustomPath() {
		
		String dataFile = "newName.txt";
		final String pathFile = "pathCustom.txt";
		String dataPath = "/new folder/";
		String dataPathWithFile = "/new folder/newText.txt";
		
		File checkDataFile = new File(dataFile);
		File checkPathFile = new File(pathFile);
		File checkDataPath = new File(dataPath);
		File checkDataPathWithFile = new File(dataPathWithFile);
		
		assertFalse(checkDataFile.exists());
		assertFalse(checkPathFile.exists());
		
		try {
			myStorage = Storage.getInstance();
			myStorage.setCustomPath(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertTrue(checkDataFile.exists());
		assertTrue(checkPathFile.exists());
		
		checkDataFile.delete();
		checkPathFile.delete();	
		
		try {
			myStorage = Storage.getInstance();
			myStorage.setCustomPath(dataPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertTrue(checkDataPath.exists());
		assertTrue(checkPathFile.exists());
		
		checkDataPath.delete();
		checkPathFile.delete();	
			
		try {
			myStorage = Storage.getInstance();
			myStorage.setCustomPath(dataPathWithFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertTrue(checkDataPathWithFile.exists());
		assertTrue(checkPathFile.exists());
		
		checkDataPathWithFile.delete();
		checkPathFile.delete();
		
	}

}
