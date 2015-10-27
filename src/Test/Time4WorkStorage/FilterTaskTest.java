package Test.Time4WorkStorage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FilterTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

public class FilterTaskTest {
	
	public ArrayList<Tasks> myList = new ArrayList<Tasks>();
	public Tasks tempTask1, tempTask2, tempTask3, tempTask4 = null;
	public FilterTask myFilter = new FilterTask();
	
	@Before
	public void setUp() {
		
		myList = new ArrayList<Tasks>();
		tempTask1 = null;
		tempTask2 = null;
		tempTask3 = null;
		tempTask4 = null;
		
		Duration tempDeadLine = new Duration("300915", "1800" , "300915", "2000");
		tempTask1 = new DurationTask("I'm a duration task!", tempDeadLine );
		myList.add(tempTask1);
		
		tempDeadLine = new Duration("310915", "1200");
		tempTask2 = new DeadlineTask("I'm a deadLine task!", tempDeadLine );
		myList.add(tempTask2);
		
		tempTask3 = new FloatingTask("I'm a flooooating task!");
		myList.add(tempTask3);
	}
	
	@Test
	public void testSearchDescription() {
		
		//checking exact word matches
		assertEquals(myFilter.searchDescription(myList, "DURATION").size(), 1);
		assertTrue(myFilter.searchDescription(myList, "DURATION").get(0).equals(tempTask1));
		assertEquals(myFilter.searchDescription(myList, "exist").size(), 0);
		assertEquals(myFilter.searchDescription(myList, "task DOESN'T").size(), 3);
		assertEquals(myFilter.searchDescription(myList, "nonexistent DOESN'T").size(), 0);
		
		//added 1 more with same word 
		myList.add(tempTask1);
		assertEquals(myFilter.searchDescription(myList, "DURATION").size(), 2);
		
		//checks starting with
		assertEquals(myFilter.searchDescription(myList, "i").size(), 4);
		tempTask4 = new FloatingTask("This doesn't start with I but contains I");
		myList.add(tempTask4);		
		assertEquals(myFilter.searchDescription(myList, "i").size(), 4);		
		assertEquals(myFilter.searchDescription(myList, "i t").size(), 5);
		
		//checks near matches
		assertEquals(myFilter.searchDescription(myList, "dedline").size(), 1);
		assertEquals(myFilter.searchDescription(myList, "druation").size(), 2);
		assertEquals(myFilter.searchDescription(myList, "flooooo").size(), 1);
		assertEquals(myFilter.searchDescription(myList, "Tsk").size(), 4);
	}
	
	@Test
	public void testSearchCompleted() {
		
		
		assertEquals(myFilter.searchCompleted(myList).size(), 0);
		assertEquals(myFilter.searchNotCompleted(myList).size(), 3);
		
		myList.get(1).setCompleted(true);
		
		assertEquals(myFilter.searchCompleted(myList).size(), 1);
		assertEquals(myFilter.searchNotCompleted(myList).size(), 2);
	}
	
	
	@Test
	public void testSearchType() {
		
		assertEquals(myFilter.searchType(myList, 1).size(), 1);
		assertEquals(myFilter.searchType(myList, 2).size(), 1);		
		assertEquals(myFilter.searchType(myList, 4).size(), 1);
		assertEquals(myFilter.searchType(myList, 0).size(), 0);
	}

}
