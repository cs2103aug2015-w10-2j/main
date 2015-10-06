import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Storage {
	
	private File myFile;
	private String defPath = "myTasks.txt";
	private String currentPath = "";
	private FileWriter fw;
	private FileReader fr; 
	private BufferedWriter bw;
	private BufferedReader br;
	
	private int type = 0;
	private final int DeadlineType = 1;
	private final int DurationType = 2;
	private final int BlockedType = 3;
	private final int FloatingType = 4;
	
	Gson gson = new Gson(); 
	
	//default path
	public Storage() {
		setCurrentPath(defPath);
		try {
			createFile(currentPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//specified path without fileName
	public Storage(String path) {
		currentPath = path + File.separator + "myTasks.txt";
		try {
			createFile(currentPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//if file doesn't exist, returns null
	//if file exists, return contents
	private ArrayList<Tasks> createFile(String path) throws IOException {
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		//set up file
		myFile = new File(currentPath);
		
		//file not found, create file, intermediate directory? and return null
		if(!myFile.exists()) {
			//myFile.mkdirs();
			myFile.createNewFile();
		}
		//file exists, read file and return contents
		else {
			myTaskList = readFile();
		}
		return myTaskList;

	}

	private void openWriterReader() throws IOException, FileNotFoundException {
		//set up writers
		fw = new FileWriter(myFile.getAbsoluteFile(),true);
		fr = new FileReader(myFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		br = new BufferedReader(fr);
	}
	
	//read from file, deserialize and add to myTaskList
	public ArrayList<Tasks> readFile() {
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		String tempLine = "";
		Tasks tempTask;
		
		//read contents of file
		//String segments[] = path.split("\"type\":");
		String splitter = "\"type\":";
		
		
		try {
			while ((tempLine = br.readLine()) != null) {
				//extract type
				int startIndex = tempLine.indexOf(splitter)+splitter.length();
				type = Integer.parseInt( tempLine.substring(startIndex, startIndex+1));
				
				switch(type) {
					case DeadlineType: 	tempTask = gson.fromJson(tempLine, DeadlineTask.class);
										break;
					case DurationType: 	tempTask = gson.fromJson(tempLine, DurationTask.class);
										break;
					case FloatingType: 	tempTask = gson.fromJson(tempLine, FloatingTask.class);
										break;
					case BlockedType: 	tempTask = gson.fromJson(tempLine, BlockedTask.class);
										break;
					default:			tempTask = null;
										break;
				}
				//tempTask = gson.fromJson(tempLine, Tasks.class);
				myTaskList.add(tempTask);
			}
		}
		catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		//close writer and reader
		try {
			closeWriterReader();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		   
		return myTaskList;
	}

	public Tasks appendTask(Tasks newTask) {
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		//if task has no taskID, generate one!
		if(newTask.getTaskID() == 0) {
			newTask.setTaskID(GenerateTaskID());
		}
		
		try {
			closeWriterReader();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		String tempLine = gson.toJson(newTask);
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//write new Tasks into file
		try {
			bw.write(tempLine);
			bw.newLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		//close writer
		try {
			closeWriterReader();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return newTask;
	}

	
	
	public Tasks deleteTask(int taskID) throws IOException{
		
		Tasks deletedTask = null;
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		boolean needDelete = false;
		myTaskList = readFile();
		
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
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			//wait for garbage collector
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//delete old file
			if (!myFile.delete()) {
		        System.out.println("Could not delete file");
		    } 
			
			createFile(currentPath);
			
			try {
				openWriterReader();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			
			for(int i=0; i<myTaskList.size(); i++) {
				String tempLine = gson.toJson(myTaskList.get(i)); 
			
				//write new Tasks into file
				try {
					bw.write(tempLine);
					bw.newLine();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			closeWriterReader();
			System.gc();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
				/*
			//create temporary file without deleted entry
			File tempFile = new File(myFile.getAbsolutePath() + ".tmp");
			FileWriter TempFw = new FileWriter(tempFile.getAbsoluteFile(), true);
			BufferedWriter TempBw = new BufferedWriter(TempFw);
			
			for(int i=0; i<myTaskList.size(); i++) {
				String tempLine = gson.toJson(myTaskList.get(i)); 
			
				//write new Tasks into file
				try {
					TempBw.write(tempLine);
					TempBw.newLine();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//close all writers and readers
			try {
				TempBw.close();
				closeWriterReader();
				System.gc();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			//wait for garbage collector
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//delete original file
			if (!myFile.delete()) {
		        System.out.println("Could not delete file");
		      } 
		      
		    //Rename the new file to the filename the original file had.
		    if (!tempFile.renameTo(myFile)) {
		    	System.out.println("Could not rename file");
		    }
		}	
		*/
		return deletedTask;
	}
	
	//search and returns entries with description matching search String
	public ArrayList<Tasks> SearchTask(String searchString){
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		ArrayList<Tasks> tempList = readFile();
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		for(int i=0; i<tempList.size(); i++) {
			if(tempList.get(i).getDescription().contains(searchString)) {
				myTaskList.add(tempList.get(i));
			}
		}
		
		try {
			closeWriterReader();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return myTaskList;
	}
	
	//search and returns task by taskID
		public Tasks SearchTaskID(int taskID) throws FileNotFoundException, IOException{
			
			openWriterReader();

			
			ArrayList<Tasks> tempList = readFile();
			Tasks myTask = null;
			
			for(int i=0; i<tempList.size(); i++) {
				if(tempList.get(i).getTaskID() == taskID) {
					myTask = tempList.get(i);
				}
			}
			
			closeWriterReader();
			
			return myTask;
		}
	
	//replaces specified taskID with updated Tasks and returns "old" updated task
	public Tasks UpdateTask(int TaskID, Tasks updatedTask){
		
		Tasks oldTask = null;
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			oldTask = SearchTaskID(TaskID);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			deleteTask(TaskID);
			appendTask(updatedTask);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			closeWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return oldTask;
	}
	
	private int GenerateTaskID() {
		
		int largestID = 0;
		ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
		
		try {
			openWriterReader();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		myTaskList = readFile();
		for(int i=0; i<myTaskList.size(); i++) {
			if(myTaskList.get(i).getTaskID() > largestID) {
				largestID = myTaskList.get(i).getTaskID();
			}
		}
		
		try {
			closeWriterReader();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return largestID +1;
	}
	
	private void closeWriterReader() throws IOException {
		bw.close();
		br.close();
	}
	
	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}


}
