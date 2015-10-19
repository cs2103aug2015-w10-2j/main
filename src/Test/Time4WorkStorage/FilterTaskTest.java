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
	public Tasks tempTask1, tempTask2, tempTask3 = null;
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
		
		assertEquals(myFilter.searchCompleted(myList, true).size(), 0);
		assertEquals(myFilter.searchCompleted(myList, false).size(), 3);
		
		myList.get(1).setCompleted(true);
		
		assertEquals(myFilter.searchCompleted(myList, true).size(), 1);
		assertEquals(myFilter.searchCompleted(myList, false).size(), 2);
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
