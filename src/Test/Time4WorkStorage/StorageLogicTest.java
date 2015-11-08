package Test.Time4WorkStorage;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.StorageLogic;
import Time4WorkStorage.Tasks;

/* @@author A0125495Y */

public class StorageLogicTest {
	
	StorageLogic myLogic = new StorageLogic();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	final String testFile = "testFile.txt";
	final Tasks tempTask1 = new FloatingTask(1, "I'm a floating task!");
	final String tempTask1String = gson.toJson(tempTask1);
	final Duration tempDeadLine2 = new Duration("300915", "1800" , "300915", "2000");
	final Tasks tempTask2 = new DurationTask(2, "I'm a duration task!", tempDeadLine2 );
	final String tempTask2String = gson.toJson(tempTask2);
	final Duration tempDeadLine3 = new Duration("310915", "1200");
	final Tasks tempTask3 = new DeadlineTask(3, "I'm a deadLine task!", tempDeadLine3 );
	final String tempTask3String = gson.toJson(tempTask3);
	
	File myFile;
	FileReader fr; 
	BufferedReader br;
	ArrayList<Tasks> tempList = new ArrayList<Tasks>();
	boolean fault;
	
	@Before
	public void setUp() {
		
		myFile = null;
		fault = false;
		
		try {
			myLogic.createCustomFile(testFile, false);
		} catch (IOException e1) {
			e1.printStackTrace();
			fault = true;
		}
		
		try {
			myLogic.addNewTask(tempTask1);
			myLogic.addNewTask(tempTask2);
			myLogic.addNewTask(tempTask3);
		} catch (IOException e) {
			e.printStackTrace();
			fault = true;
		}
	}
	
	//adds a new task and compares size
	@Test
	public void testAddNewTask() {

		if(!fault) {
			
			try {
				tempList = myLogic.getAllTasks();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Duration tempDeadLine = new Duration("051115", "2000");
			Tasks tempTask4 = new DeadlineTask("New task", tempDeadLine);
			
			try {
				myLogic.addNewTask(tempTask4);
				tempList = myLogic.getAllTasks();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			assertEquals(tempList.size(),4);
		}
	}
	
	//deletes last item and compares size
	@Test
	public void testDeleteLast() {
		
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		list3.add(3);		

		if(!fault) {
			
			try {
				tempList = myLogic.getAllTasks();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			assertEquals(tempList.size(),3);
			
			try {
				myLogic.delete(list3);
				tempList = myLogic.getAllTasks();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			assertEquals(tempList.size(),2);

		}

	}
	
	//sets all 3 as completed
	@Test
	public void testSetCompleted() {
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		
		if(!fault) {	
			try {
				myLogic.setCompleted(list, true);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				fault = true;
			}
		}
		
		if(!fault) {				
			try {
				tempList = myLogic.getAllTasks();
			} catch (IOException e) {
				e.printStackTrace();
				fault = true;
			}
			
			for(int i=0; i<tempList.size(); i++) {
				assertEquals(tempList.get(i).isCompleted(),true);
			}			

		}
	}
	
	//clears file contents
	@Test
	public void testClear() {
		
		if(!fault) {
			try {
				tempList = myLogic.getAllTasks();
			} catch (IOException e) {
					e.printStackTrace();
					fault = true;
			}
			
			assertTrue(tempList.size() != 0);
			
			try {
				myLogic.clear();
				tempList = myLogic.getAllTasks();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				fault = true;
			}			
	
			assertEquals(tempList.size(),0);
		}
	}
	
	
	@After
	public void cleanUp() throws InterruptedException {
		try {
			myLogic.deleteDataFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
