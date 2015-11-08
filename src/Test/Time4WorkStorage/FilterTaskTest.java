package Test.Time4WorkStorage;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FilterTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

/* @@author A0125495Y */

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
		
		Duration tempDeadLine = new Duration("290915", "1800" , "300915", "2000");
		tempTask1 = new DurationTask("I'm a duration task", tempDeadLine );
		myList.add(tempTask1);
		
		tempDeadLine = new Duration("300915", "1200");
		tempTask2 = new DeadlineTask("I'm a deadLine task", tempDeadLine );
		myList.add(tempTask2);
		
		tempTask3 = new FloatingTask("I'm a flooooating task");
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
		tempTask4 = new FloatingTask("This doesn't start with 'I' but contains 'I'");
		myList.add(tempTask4);		
		assertEquals(myFilter.searchDescription(myList, "i").size(), 4);		
		assertEquals(myFilter.searchDescription(myList, "i t").size(), 5);
		
		//checks single match within string but not starting with
		assertEquals(myFilter.searchDescription(myList, "a").size(), 4);	
		
		//checks near matches
		assertEquals(myFilter.searchDescription(myList, "dedline").size(), 1);
		assertEquals(myFilter.searchDescription(myList, "druation").size(), 2);
		assertEquals(myFilter.searchDescription(myList, "floooo").size(), 1);
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
		
		assertEquals(myFilter.searchFloating(myList).size(), 1);
		assertEquals(myFilter.searchDeadline(myList).size(), 1);		
		assertEquals(myFilter.searchDuration(myList).size(), 1);
	}
	
	@Test
	public void testSearchDate() {
		String dateMatch = "290915";
		String dateNotMatch = "300920";
		assertEquals(myFilter.searchDate(myList, dateMatch).size(), 1);
		assertEquals(myFilter.searchDate(myList, dateNotMatch).size(), 0);
	}
	
	@Test
	public void testSearchBeforeDate() {
		String dateMatch = "290915";
		String date2Match = "300915";
		String dateAllMatch = "290920";
		String dateNoMatch = "300914";
		try {
			assertEquals(myFilter.searchBeforeDate(myList, dateMatch).size(), 1);
			assertEquals(myFilter.searchBeforeDate(myList, date2Match).size(), 2);
			assertEquals(myFilter.searchBeforeDate(myList, dateAllMatch).size(), 2);
			assertEquals(myFilter.searchBeforeDate(myList, dateNoMatch).size(), 0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSearchOverdue() {
		
		Calendar cal = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);	
		
		cal.add(Calendar.DATE, 1);
		Date tomorrow = cal.getTime();
		String nextDay = format.format(tomorrow);
		
		Duration tempDeadLine = new Duration(nextDay, "2000");		
		tempTask4 = new DeadlineTask("This doesn't fail until tomorrow", tempDeadLine);
		myList.add(tempTask4);		
		
		try {
			assertEquals(myFilter.searchOverDue(myList).size(), 2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		cal.add(Calendar.DATE, -2);
		Date yesterday = cal.getTime();
		String previousDay = format.format(yesterday);
		
		tempDeadLine = new Duration(previousDay, "2000");
		tempTask4 = new DeadlineTask("This is overdue since yesterday", tempDeadLine);
		myList.add(tempTask4);
		
		try {
			assertEquals(myFilter.searchOverDue(myList).size(), 3);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSearchBetweenDates() {
		
		try {
			assertEquals(myFilter.searchBetweenDates(myList, "290915", "290915").size(), 1);
			assertEquals(myFilter.searchBetweenDates(myList, "290915", "021115").size(), 2);
			assertEquals(myFilter.searchBetweenDates(myList, "010101", "020202").size(), 0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
