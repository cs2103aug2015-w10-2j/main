package Time4WorkStorage;

import java.io.*;
import java.util.ArrayList;

public class Storage {
	
	private StorageLogic myLogic = new StorageLogic();
	
	//use storage default path, <local directory>/myTasks.txt
	public Storage() throws IOException {		
		myLogic.createDefaultFile();		
	}
	
	//custom path and filename, path has to have escape characters
	//eg. C:\\user\\Desktop\\myTasks.txt
	public Storage(String path) throws IOException {		
		myLogic.createCustomFile(path);		
	}
	
	//read from file, deserialize and returns all tasks
	public ArrayList<Tasks> readFile() throws IOException {		
		try {
			return myLogic.getAllTasks();
		} catch (IOException e) {
			throw e;
		}		
	}

	//adds new task at the end of the list, returns back the new task if successful
	public Tasks appendTask(Tasks newTask) throws IOException {		
		try {
			return myLogic.addNewTask(newTask);
		} catch (IOException e) {
			throw e;
		}
	}	
	
	//deletes the input taskID, returns the deleted task, returns null if no matching taskID
	public Tasks deleteTask(int taskID) throws IOException, InterruptedException{		
		try {
			return myLogic.delete(taskID);
		} catch (IOException | InterruptedException e) {
			throw e;
		}		
	}
	
	//search and returns entries with task description matching search String, null if no matches
	public ArrayList<Tasks> SearchTask(String searchString) throws IOException{		
		try {
			return myLogic.searchDescription(searchString);
		} catch (IOException e) {
			throw e;
		}		
	}
	
	//replaces specified taskID with updated Tasks and returns "old" updated task
	public Tasks UpdateTask(int taskID, Tasks updatedTask) throws Exception{
		
		Tasks oldTask = null;
		
		try {
			oldTask = myLogic.delete(taskID);
			myLogic.addNewTask(updatedTask);
			
		} catch (IOException | InterruptedException e) {
			throw e;
		}
		
		return oldTask;
	}
	
	//search and returns list of completed/incomplete tasks based on boolean input
	public ArrayList<Tasks> SearchCompleteStatus(boolean complete) throws IOException{		
		try {
			return myLogic.searchComplete(complete);
		} catch (IOException e) {
			throw e;
		}		
	}
	
	//search and returns list of completed/incomplete tasks based on boolean input
	public ArrayList<Tasks> SearchTaskType(int type) throws IOException{		
		try {
			return myLogic.searchTaskType(type);
		} catch (IOException e) {
			throw e;
		}		
	}
	
	//clear the file of contents, does not delete file
	public ArrayList<Tasks> ClearAll() throws Exception{		
		try {
			return myLogic.clear();
		} catch (IOException | InterruptedException e) {
			throw e;
		}		
	}
	
}
