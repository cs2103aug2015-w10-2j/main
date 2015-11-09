package Time4WorkStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkData.Tasks;

/* @@author A0125495Y */

public class Storage {
	
	//as constructor may try exception, create object when getInstance is called
	private static Storage theStorage = null; 
	private StorageLogic myLogic = new StorageLogic();
	private CustomPathLogic myPath = new CustomPathLogic();
	private static final String defFile = "myTasks.txt";
	
	private static final String BACKSLASH = "\\";
	private static final String FORWARDSLASH = "/";
	
	private static final Logger logger = Logger.getLogger(Storage.class.getName());

	//use storage default path, <local directory>/myTasks.txt unless there's a previous saved path
	private Storage() throws IOException {
		logger.log(Level.INFO, "Storage initialized");
		String customPath = "";
		if(myPath.savedPathExists()) {
			customPath = myPath.readCustomPath();
			myLogic.createCustomFile(customPath, false);
			logger.log(Level.INFO, "Custom path found during init");
		} else {
			myLogic.createDefaultFile();
			logger.log(Level.INFO, "No custom path found during init");
		}
	}
	
	//custom path, path has to have escape characters
	//eg. C:\\user\\Desktop\\myTasks.txt
	public void setCustomPath(String path) throws IOException {
		logger.log(Level.INFO, "Setting custom path");
		String customPath = "";
		if(myPath.savedPathExists()) {
			customPath = myPath.readCustomPath();
		}
		
		File tempFile = new File(path);
		path = tempFile.getAbsolutePath();
		
		String tempPath = tempFile.toString();
		String parentPath = "";
		if(tempFile.getParentFile() != null) {
			parentPath = tempFile.getParentFile().toString();
		}
		String lastArgs = tempPath.substring(parentPath.length(), tempPath.length());
		
		if(!lastArgs.contains(".")) {
			String lastChar = path.substring(path.length() - 1);
			if(lastChar.equals(BACKSLASH) || lastChar.equals(FORWARDSLASH)) {
				path = path + defFile;
			}
			else {
				path = path + BACKSLASH + defFile;
			}
		}
		
		String newPath = path.replace(BACKSLASH, BACKSLASH+BACKSLASH);
		String pathChanged = "";
		
		if(!newPath.equalsIgnoreCase(customPath)) {
			pathChanged = myLogic.createCustomFile(path, true);	
			myPath.writeCustomPath(path);
		}
		
		if(!pathChanged.equals(customPath) && !pathChanged.equals("")){
			customPath = pathChanged;
			myPath.writeCustomPath(customPath);
		}
	}
	
	//read from file, deserialize and returns all tasks
	public ArrayList<Tasks> readFile() throws IOException {
		logger.log(Level.INFO, "Storage reading data");
		try {
			return myLogic.getAllTasks();
		} catch (IOException e) {
			throw e;
		}		
	}

	//adds new task at the end of the list, returns back the new task if successful
	public Tasks appendTask(Tasks newTask) throws IOException {
		logger.log(Level.INFO, "Storage adding task");
		try {
			return myLogic.addNewTask(newTask);
		} catch (IOException e) {
			throw e;
		}
	}	
	
	//deletes the input taskID, returns the deleted task, returns null if no matching taskID
	public ArrayList<Tasks> deleteTask(ArrayList<Integer> taskID) throws IOException, InterruptedException{
		logger.log(Level.INFO, "Storage deleting tasks");
		try {
			return myLogic.delete(taskID);
		} catch (IOException | InterruptedException e) {
			throw e;
		}		
	}
	
	//replaces specified taskID with updated Tasks and returns "old" updated task
	public Tasks UpdateTask(int taskID, Tasks updatedTask) throws Exception{
		logger.log(Level.INFO, "Storage updating task");
		ArrayList<Tasks> oldTask = null;
		ArrayList<Integer> myID = new ArrayList<Integer>();
		myID.add(taskID);
		
		try {
			oldTask = myLogic.delete(myID);
			myLogic.addNewTask(updatedTask);
			
		} catch (IOException | InterruptedException e) {
			throw e;
		}
		
		if(oldTask.size() == 0) {
			return null;
		} else {				
			return oldTask.get(0);
		}
	}
	
	//sets tasks as complete and returns the tasks
	public ArrayList<Tasks> SetCompleted(ArrayList<Integer> taskID) throws Exception{
		logger.log(Level.INFO, "Storage setting tasks as complete");
		try {
			return myLogic.setCompleted(taskID, true);
		} catch (IOException | InterruptedException e) {
			throw e;
		}		
	}
	
	//sets tasks as incomplete and returns the tasks
	public ArrayList<Tasks> SetIncompleted(ArrayList<Integer> taskID) throws Exception{
		logger.log(Level.INFO, "Storage setting tasks as incomplete");
		try {
			return myLogic.setCompleted(taskID, false);
		} catch (IOException | InterruptedException e) {
			throw e;
		}		
	}
	
	//clear the file of contents, does not delete file
	public ArrayList<Tasks> ClearAll() throws Exception{
		logger.log(Level.INFO, "Storage recreating");
		try {
			return myLogic.clear();
		} catch (IOException | InterruptedException e) {
			throw e;
		}		
	}
	
	public static Storage getInstance() throws IOException {
		
		if(theStorage == null) {
			try {
				theStorage = new Storage();
			} catch (IOException e) {
				throw e;
			}
		}
		return theStorage;
	}
	
	public void deleteCustomPathFile() throws IOException {
		logger.log(Level.INFO, "Storage deleting custom path file");
		try {
			myPath.deleteCustomPathFile();
		} catch (IOException e) {
			throw e;
		}
	}
	
	public void deleteDataFile() throws IOException {
		logger.log(Level.INFO, "Storage deleting data file");
		try {
			theStorage.deleteDataFile();
		} catch (IOException e) {
			throw e;
		}
	}
	
}
