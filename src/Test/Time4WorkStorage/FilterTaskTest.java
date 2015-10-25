package Test.Time4WorkStorage;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
	
	@Test
	public void testSearchDescription() {		
		
		Duration tempDeadLine = new Duration("300915", "1800" , "300915", "2000");
		tempTask1 = new DurationTask("I'm a duration task!", tempDeadLine );
		myList.add(tempTask1);
		
		tempDeadLine = new Duration("310915", "1200");
		tempTask2 = new DeadlineTask("I'm a deadLine task!", tempDeadLine );
		myList.add(tempTask2);
		
		tempTask3 = new FloatingTask("I'm a floating task!");
		myList.add(tempTask3);
		
		assertEquals(myFilter.searchDescription(myList, "DURATION").size(), 1);
		assertTrue(myFilter.searchDescription(myList, "DURATION").get(0).equals(tempTask1));
		assertEquals(myFilter.searchDescription(myList, "exist").size(), 0);
		
		myList.add(tempTask1);
		assertEquals(myFilter.searchDescription(myList, "DURATION").size(), 2);
		
		assertEquals(myFilter.searchDescription(myList, "i").size(), 4);
		tempTask4 = new FloatingTask("This doesn't start with I but contains I");
		myList.add(tempTask4);
		
		assertEquals(myFilter.searchDescription(myList, "i").size(), 4);
		assertEquals(myFilter.searchDescription(myList, "i t").size(), 5);
		assertEquals(myFilter.searchDescription(myList, "task DOESN'T").size(), 5);
		assertEquals(myFilter.searchDescription(myList, "nonexistent DOESN'T").size(), 1);
	}
	
	@Test
	public void testSearchCompleted() {
		
		Duration tempDeadLine = new Duration("300915", "1800" , "300915", "2000");
		tempTask1 = new DurationTask("I'm a duration task!", tempDeadLine );
		myList.add(tempTask1);
		
		tempDeadLine = new Duration("310915", "1200");
		tempTask2 = new DeadlineTask("I'm a deadLine task!", tempDeadLine );
		myList.add(tempTask2);
		
		tempTask3 = new FloatingTask("I'm a floating task!");
		myList.add(tempTask3);
		
		assertEquals(myFilter.searchCompleted(myList).size(), 0);
		assertEquals(myFilter.searchNotCompleted(myList).size(), 3);
		
		myList.get(1).setCompleted(true);
		
		assertEquals(myFilter.searchCompleted(myList).size(), 1);
		assertEquals(myFilter.searchNotCompleted(myList).size(), 2);
	}
	
	@Test
	public void testSearchType() {
		
		Duration tempDeadLine = new Duration("300915", "1800" , "300915", "2000");
		tempTask1 = new DurationTask("I'm a duration task!", tempDeadLine );
		myList.add(tempTask1);
		
		tempDeadLine = new Duration("310915", "1200");
		tempTask2 = new DeadlineTask("I'm a deadLine task!", tempDeadLine );
		myList.add(tempTask2);
		
		tempTask3 = new FloatingTask("I'm a floating task!");
		myList.add(tempTask3);
		
		assertEquals(myFilter.searchType(myList, 1).size(), 1);
		assertEquals(myFilter.searchType(myList, 2).size(), 1);		
		assertEquals(myFilter.searchType(myList, 4).size(), 1);
		assertEquals(myFilter.searchType(myList, 0).size(), 0);
	}

}
