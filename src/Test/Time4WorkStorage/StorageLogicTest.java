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

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.StorageLogic;
import Time4WorkStorage.Tasks;

/* @@author A0125495Y */

public class StorageLogicTest {
	
	StorageLogic myLogic = new StorageLogic();
	Gson gson = new Gson();
	
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
	boolean fault;
	
	@Before
	public void setUp() {
		
		myFile = null;
		fr = null;
		br = null;
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
	
	//retrieves the last line and compares
	@Test
	public void testAddNewTask() {
		if(!fault) {	
			myFile = new File(testFile);
			try {
				fr = new FileReader(myFile.getAbsoluteFile());
				br = new BufferedReader(fr);
			} catch (IOException e) {
				e.printStackTrace();
				fault = true;
			}
			
			if(!fault) {
				String lastLine = "", tempLine = "";
				try {
					while ((tempLine = br.readLine()) != null) {
						lastLine = tempLine;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				//compares last string
				assertEquals(lastLine,tempTask3String);
			}			
		}
	}
	
	//deletes last line and compares
	@Test
	public void testDeleteLast() {
		
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		list3.add(3);
		
		if(!fault) {	
			myFile = new File(testFile);
						
			if(!fault) {
				try {
					myLogic.delete(list3);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					fault = true;
				}
			}
			if(!fault) {				
				try {
					fr = new FileReader(myFile.getAbsoluteFile());
					br = new BufferedReader(fr);
				} catch (IOException e) {
					e.printStackTrace();
					fault = true;
				}
			
				if(!fault) {
					String lastLine = "", tempLine = "";
					try {
						while ((tempLine = br.readLine()) != null) {
							lastLine = tempLine;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					//compares last string
					assertEquals(lastLine,tempTask2String);
				}			
			}
		}
	}
	
	//deletes first line and compares last line
	@Test
	public void testDeleteFirst() {
		
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		
		if(!fault) {	
			myFile = new File(testFile);
						
			if(!fault) {
				try {
					myLogic.delete(list1);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					fault = true;
				}
			}
			if(!fault) {				
				try {
					fr = new FileReader(myFile.getAbsoluteFile());
					br = new BufferedReader(fr);
				} catch (IOException e) {
					e.printStackTrace();
					fault = true;
				}
			
				if(!fault) {
					String lastLine = "", tempLine = "";
					try {
						while ((tempLine = br.readLine()) != null) {
							lastLine = tempLine;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					//compares last string
					assertEquals(lastLine,tempTask3String);
				}			
			}
		}
	}
	
	
	//sets all 3 as completed
	@Test
	public void testSetCompleted() {
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		
		ArrayList<Tasks> results = null;
		
		if(!fault) {	
			myFile = new File(testFile);
						
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
					fr = new FileReader(myFile.getAbsoluteFile());
					br = new BufferedReader(fr);		
					results = myLogic.getAllTasks();
				} catch (IOException e) {
					e.printStackTrace();
					fault = true;
				}
			
				if(!fault) {
					for(int i=0; i<results.size(); i++) {
						assertEquals(results.get(i).isCompleted(),true);
					}
				}			
			}
		}
	}
	
	//clears file contents
	@Test
	public void testClear() {
		
		ArrayList<Tasks> results = null;
		
		if(!fault) {	
			myFile = new File(testFile);
						
			if(!fault) {				
				try {
					results = myLogic.getAllTasks();
				} catch (IOException e) {
					e.printStackTrace();
					fault = true;
				}
			}
			
			if(!fault) {				
				assertTrue(results.size() != 0);
				try {
					myLogic.clear();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					fault = true;
				}
			}
			
			if(!fault) {				
				try {
					fr = new FileReader(myFile.getAbsoluteFile());
					br = new BufferedReader(fr);
				} catch (IOException e) {
					e.printStackTrace();
					fault = true;
				}
				
				if(!fault) {
					int itemCounter = 0;
					try {
						while ((br.readLine()) != null) {
							itemCounter++;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					assertEquals(itemCounter,0);
				}
			}
		}
	}
	
	
	@After
	public void cleanUp() throws InterruptedException {
		try {
			br.close();
			fr.close();
			myLogic.deleteDataFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
