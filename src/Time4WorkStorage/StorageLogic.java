package Time4WorkStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class StorageLogic {
		
	private String currentPath = "";
	
	private File myFile;
	private FileWriter fw;
	private FileReader fr; 
	private BufferedWriter bw;
	private BufferedReader br;
	
	private final int DeadlineType = 1;
	private final int DurationType = 2;
	//private final int BlockedType = 3;
	private final int FloatingType = 4;
	private final String defPath = "myTasks.txt";
	
	Gson gson = new Gson(); 
	
	//creates file at default directory, <local directory>/myTasks.txt
	public void createDefaultFile() throws IOException {
		
		currentPath = defPath;
		try {
			createFile(currentPath);
		} catch (IOException e) {
			throw e;
		}
	}	
	
	//custom path and filename, path has to have escape characters
	//eg. C:\\user\\Desktop\\myTasks.txt
	public void createCustomFile(String path, boolean transfer) throws IOException {
			
		ArrayList<Tasks> tempList = new ArrayList<Tasks>();
		
		if(transfer) {
			tempList = getAllTasks();
			try {
				deleteDataFile();
			} catch (IOException e) {
				throw e;
			}
		}
		
		currentPath = path;		
		try {
			createFile(currentPath);
		} catch (IOException e) {
			throw e;
		}
		
		if(transfer) {
			for(int i=0; i<tempList.size(); i++) {
				addNewTask(tempList.get(i));
			}
		}
		
	}
	
	//if file doesn't exist, creates file and returns null
	//if file exists, read and return contents
	private ArrayList<Tasks> createFile(String currentPath) throws IOException {
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		myFile = new File(currentPath);
		
		try {
			CreateDirectories(myFile);
		}
		catch (IOException e) {
			throw e;
		}
		
		
		if(myFile.exists() && myFile.isFile()) {
			myTaskList = getAllTasks();
		} else {			
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				throw new IOException("File already exists or directory not made");
			}
		}
		
		return myTaskList;
	}
	
	//checks if file contains path or just fileName.
	//Creates whole path if it's a path. Returns true on success file+directory creation
	private void CreateDirectories(File theFile) throws IOException {
		
		File parentFile = theFile.getParentFile();
		

		if(parentFile != null) {
			if(!parentFile.mkdirs()) {
				if(!parentFile.exists()) {
					throw new IOException("Unable to create directories.");
				}
			}
		}
	}
	
	//reads and return all tasks in file
	public ArrayList<Tasks> getAllTasks() throws IOException {
		
		if(!myFile.exists()) {
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		String tempLine = "";
		Tasks tempTask;
		
		//read contents of file		
		try {
			while ((tempLine = br.readLine()) != null) {
				int type = getTypeFromGsonString(tempLine);
				
				switch(type) {
					case DeadlineType: 	tempTask = gson.fromJson(tempLine, DeadlineTask.class);
										break;
					case DurationType: 	tempTask = gson.fromJson(tempLine, DurationTask.class);
										break;
					case FloatingType: 	tempTask = gson.fromJson(tempLine, FloatingTask.class);
										break;
					/*case BlockedType: 	tempTask = gson.fromJson(tempLine, BlockedTask.class);
										break;*/
					default:			tempTask = null;
										break;
				}
				myTaskList.add(tempTask);
			}
		} catch (JsonSyntaxException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		   
		return myTaskList;
	}

	//retrieve type number from Gson string
	private int getTypeFromGsonString(String gsonString) {
		
		int type = 0;
		String splitter = "\"type\":";
		
		int startIndex = gsonString.indexOf(splitter)+splitter.length();
		type = Integer.parseInt( gsonString.substring(startIndex, startIndex+1));
		
		return type;
	}
	
	
	//adds new task at the end of file, generates taskID if not present
	public Tasks addNewTask(Tasks newTask) throws IOException {
		
		if(!myFile.exists()) {
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
				
		//if task has no taskID, generate one!
		if(newTask.getTaskID() == 0) {
			try {
				newTask.setTaskID(GenerateTaskID());
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		String tempLine = gson.toJson(newTask);
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		

		try {
			bw.write(tempLine);
			bw.newLine();
		} catch (IOException e) {
			throw e;
		}
		

		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return newTask;
	}
	
	//generate a valid taskID for requests
	private int GenerateTaskID() throws IOException {
		
		int largestID = 0;
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		

		if(!myFile.exists()) {
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			myTaskList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		for(int i=0; i<myTaskList.size(); i++) {
			if(myTaskList.get(i).getTaskID() > largestID) {
				largestID = myTaskList.get(i).getTaskID();
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return largestID +1;
	}
	

	private void openWriterReader() throws IOException, FileNotFoundException{

		try {
			fw = new FileWriter(myFile.getAbsoluteFile(),true);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			throw new IOException("Unable to create writer for file");
		}
		try {
			fr = new FileReader(myFile.getAbsoluteFile());
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Reader cannot be created. File may be is missing");
		}
	}
	

	private void closeWriterReader() throws IOException {

			try {
				bw.close();
			} catch (IOException e) {
				throw new IOException("Unable to close writer");
			}
			try {
				br.close();
			} catch (IOException e) {
				throw new IOException("Unable to close reader");
			}
	}

	//searches and deletes the indicated taskID, if not found, returns null, if deleted returns deleted task 
	public Tasks delete(int taskID) throws IOException, InterruptedException {
		Tasks deletedTask = null;
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		if(!myFile.exists()) {
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		boolean needDelete = false;
		try {
			myTaskList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		

		for(int i=0; i<myTaskList.size(); i++) {
			if(myTaskList.get(i).getTaskID() == taskID) {
				needDelete = true;
				deletedTask = myTaskList.get(i);
				myTaskList.remove(i);
				break;
			}
		}
		
		//taskID to be deleted is found
		if(needDelete) {
			
			try {
				closeWriterReader();
			} catch (IOException e) {
				throw e;
			}
			try {
				deleteDataFile();
			} catch (IOException e) {
				throw e;
			}
			
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
			
			try {
				openWriterReader();
			} catch (IOException e) {
				throw e;
			}
			
			
			
			for(int i=0; i<myTaskList.size(); i++) {
				String tempLine = gson.toJson(myTaskList.get(i)); 
			
				try {
					bw.write(tempLine);
					bw.newLine();
				}  catch (IOException e) {
					throw e;
				}
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
			
		return deletedTask;
		
	}

	//sets task as completed, returns the target task
	public Tasks setCompleted(int taskID, boolean status) throws IOException, InterruptedException {
		Tasks editedTask = null;
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		if(!myFile.exists()) {
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
		}
				
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		boolean needUpdate = false;
		try {
			myTaskList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		
		for(int i=0; i<myTaskList.size(); i++) {
			if(myTaskList.get(i).getTaskID() == taskID) {
				needUpdate = true;
				myTaskList.get(i).setCompleted(status);
				editedTask = myTaskList.get(i);				
			}
			
		}
		
		//taskID to be deleted is found
		if(needUpdate) {
			
			try {
				closeWriterReader();
			} catch (IOException e) {
				throw e;
			}
			try {
				deleteDataFile();
			} catch (IOException e) {
				throw e;
			}
			
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
			
			try {
				openWriterReader();
			} catch (IOException e) {
				throw e;
			}
			
			
			
			for(int i=0; i<myTaskList.size(); i++) {
				String tempLine = gson.toJson(myTaskList.get(i)); 
			
				try {
					bw.write(tempLine);
					bw.newLine();
				}  catch (IOException e) {
					throw e;
				}
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
			
		return editedTask;
		
	}

	//deletes the file and recreates it, returning all the deleted tasks
	public ArrayList<Tasks> clear() throws IOException, InterruptedException {
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		

		if(!myFile.exists()) {
			try {
				createFile(currentPath);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			myTaskList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			deleteDataFile();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			createFile(currentPath);
		} catch (IOException e) {
			throw e;
		}
					
		return myTaskList;
		
	}
	
	public void deleteDataFile() throws IOException {
		
		try {
			closeWriterReader();
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
		if (!myFile.delete()) {
			throw new IOException("Could not delete file");
	    } 
	}
	
	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

}
