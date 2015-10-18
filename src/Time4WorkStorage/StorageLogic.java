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
	private final int BlockedType = 3;
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
	public void createCustomFile(String path) throws IOException {
		
		currentPath = path;
		
		try {
			createFile(currentPath);
		} catch (IOException e) {
			throw e;
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
		
		//find if taskID to be deleted is found
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
				System.gc();
			} catch (IOException e) {
				throw e;
			}
			
			//wait for garbage collector
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw e;
			}
			
			//delete old file
			if (!myFile.delete()) {
		        System.out.println("Could not delete file");
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
			
				//write new Tasks into file
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
			System.gc();
		} catch (IOException e) {
			throw e;
		}
			
		return deletedTask;
		
	}

	//searches task description for matching task and returns all matches, null if no match
	public ArrayList<Tasks> searchDescription(String searchString) throws IOException {
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		
		ArrayList<Tasks> tempList;
		try {
			tempList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		for(int i=0; i<tempList.size(); i++) {
			if(tempList.get(i).getDescription().toUpperCase().contains(searchString.toUpperCase())) {
				myTaskList.add(tempList.get(i));
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return myTaskList;
	}
	
	//search and returns task by taskID
	private Tasks SearchTaskID(int taskID) throws FileNotFoundException, IOException{
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}

		
		ArrayList<Tasks> tempList = getAllTasks();
		Tasks myTask = null;
		
		for(int i=0; i<tempList.size(); i++) {
			if(tempList.get(i).getTaskID() == taskID) {
				myTask = tempList.get(i);
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return myTask;
	
	}
	/*
	//AKA update, replaces task with same taskID
	public Tasks replaceTask(int taskID, Tasks updatedTask) throws IOException, InterruptedException {

		Tasks oldTask = null;
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			oldTask = SearchTaskID(taskID);
		} catch (IOException e) {
			throw e;
		}
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		try {
			delete(taskID);
			addNewTask(updatedTask);
		} catch (IOException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return oldTask;
	}
	*/
	
	//returns list of completed/incomplete tasks based on boolean input
	public ArrayList<Tasks> searchComplete(boolean complete) throws IOException {
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}

		
		ArrayList<Tasks> tempList = null;
		try {
			tempList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		
		for(int i=0; i<tempList.size(); i++) {
			if(tempList.get(i).isCompleted() == complete) {
				myTaskList.add(tempList.get(i));
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return myTaskList;
	}

	//returns list of specified tasks type
	public ArrayList<Tasks> searchTaskType(int type) throws IOException {
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		try {
			openWriterReader();
		} catch (IOException e) {
			throw e;
		}

		
		ArrayList<Tasks> tempList = null;
		try {
			tempList = getAllTasks();
		} catch (IOException e) {
			throw e;
		}
		
		for(int i=0; i<tempList.size(); i++) {
			if(tempList.get(i).getType() == type) {
				myTaskList.add(tempList.get(i));
			}
		}
		
		try {
			closeWriterReader();
		} catch (IOException e) {
			throw e;
		}
		
		return myTaskList;
	}
	
	//deletes the file and recreates it, returning all the deleted tasks
	public ArrayList<Tasks> clear() throws IOException, InterruptedException {
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
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
			System.gc();
		} catch (IOException e) {
			throw e;
		}
		
		//wait for garbage collector
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw e;
		}
		
		//delete old file
		if (!myFile.delete()) {
	        throw new IOException("File cannot be deleted!");
	    } 
		
		try {
			createFile(currentPath);
		} catch (IOException e) {
			throw e;
		}
					
		return myTaskList;
		
	}	
}
