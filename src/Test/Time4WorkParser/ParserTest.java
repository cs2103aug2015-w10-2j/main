//@@author A0110920Y

package Test.Time4WorkParser;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.joestelmach.natty.CalendarSource;

import Time4WorkParser.Command;
import Time4WorkParser.Parser;
import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.Tasks;

public class ParserTest {
  
  Parser tester = new Parser();
  
  // ================================================================================
  // Test add methods
  // ================================================================================
  
  @Test
  public void testAddTaskWithoutFullStop() { 
    /**
     * test add command without full stop
     * 
     * assert that invalid command is obtained
     */
    Command result = tester.parse("add homework");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testAddFloatingTaskSingleWord() {
    //test add command for single word floating task
    Command result = tester.parse("add homework.");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("homework", task.getDescription());
  }
  
  @Test
  public void testAddFloatingTaskMultipleWords() {
    //test add command for multiple words floating task
    Command result = tester.parse("add homework for CS2103.");
    assertEquals("add", result.getCommand());
    assertEquals("homework for CS2103", result.getTask().getDescription());
  }
  
  @Test
  public void testAddDeadlineTaskWithoutTime() {
    //test add command for deadline task without time
    Command result = tester.parse("add extra homework. 211015");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("extra homework", task.getDescription());
    Duration timeDetails = ((DeadlineTask)task).getDurationDetails();
    assertEquals("211015", timeDetails.getEndDate());
    assertEquals("2359", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddDeadlineTaskWithTime() {
    //test add command for deadline task with time
    Command result = tester.parse("add homework for CS2101. 221015 1900");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("homework for CS2101", task.getDescription());
    Duration timeDetails = ((DeadlineTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getEndDate());
    assertEquals("1900", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddDurationTaskWithoutTwoDates() {
    //test add command for duration task that does not specify 2 dates
    Command result = tester.parse("add CS2103 meeting. 221015 1400 1600");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("CS2103 meeting", task.getDescription());
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getStartDate());
    assertEquals("1400", timeDetails.getStartTime());
    assertEquals("221015", timeDetails.getEndDate());
    assertEquals("1600", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddDurationTaskWithTwoDates() {
    //test add command for duration task that specifies 2 dates
    Command result = tester.parse("add super long seminar. 221015 1400 291015 1800");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("super long seminar", task.getDescription());
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getStartDate());
    assertEquals("1400", timeDetails.getStartTime());
    assertEquals("291015", timeDetails.getEndDate());
    assertEquals("1800", timeDetails.getEndTime());
  }
  
  @Test
  public void testNullDescription() {
    Command result = tester.parse("add home.work");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testAddNaturalLanguageInputForDeadlineTask() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("add finish homework. by thurs");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("finish homework", task.getDescription());
    Duration timeDetails = ((DeadlineTask)task).getDurationDetails();
    assertEquals("051115", timeDetails.getEndDate());
    assertEquals("2359", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddNaturalLanguageInputForDurationTask() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("add super long seminar. friday 2pm - 4pm");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("super long seminar", task.getDescription());
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("061115", timeDetails.getStartDate());
    assertEquals("1400", timeDetails.getStartTime());
    assertEquals("061115", timeDetails.getEndDate());
    assertEquals("1600", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddFlippedDurationInput() {
    Command result = tester.parse("add super long seminar. 111115 1800 101115 1200");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("super long seminar", task.getDescription());
    assertEquals("101115", timeDetails.getStartDate());
    assertEquals("1200", timeDetails.getStartTime());
    assertEquals("111115", timeDetails.getEndDate());
    assertEquals("1800", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddSameDayDurationFlippedInput() {
    Command result = tester.parse("add super long seminar. 111115 1800 111115 1200");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("super long seminar", task.getDescription());
    assertEquals("111115", timeDetails.getStartDate());
    assertEquals("1200", timeDetails.getStartTime());
    assertEquals("111115", timeDetails.getEndDate());
    assertEquals("1800", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddSameDaySameHourDurationFlippedInput() {
    Command result = tester.parse("add super long seminar. 111115 1859 111115 1801");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("super long seminar", task.getDescription());
    assertEquals("111115", timeDetails.getStartDate());
    assertEquals("1801", timeDetails.getStartTime());
    assertEquals("111115", timeDetails.getEndDate());
    assertEquals("1859", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddSameDaySameHourDurationInput() {
    Command result = tester.parse("add super long seminar. 111115 1801 111115 1859");
    assertEquals("add", result.getCommand());
    Tasks task = result.getTask();
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("super long seminar", task.getDescription());
    assertEquals("111115", timeDetails.getStartDate());
    assertEquals("1801", timeDetails.getStartTime());
    assertEquals("111115", timeDetails.getEndDate());
    assertEquals("1859", timeDetails.getEndTime());
  }
  
  @Test
  public void testAddInvalidTime() {
    Command result = tester.parse("add super long seminar. 111115 2410 111115 2411");
    assertEquals("invalid", result.getCommand());
  }
  
  // ================================================================================
  // Test update methods
  // ================================================================================
  
  @Test
  public void testUpdateWithoutFullStop() {
    /**
     * test update command without full stop
     * 
     * assert that invalid command is obtained
     */
    Command result = tester.parse("update 2 homework");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testUpdateFloatingTask() {
    //test update for floating tasks
    Command result = tester.parse("update 2 homework for CS2103.");
    assertEquals("update", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("homework for CS2103", task.getDescription());
    assertEquals(Integer.parseInt("2"), task.getTaskID());
  }
  
  @Test
  public void testUpdateDeadlineTaskWithoutTime() {
    //test update for deadline task that does not specify the time
    Command result = tester.parse("update 2 homework for CS2103. 221015");
    assertEquals("update", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("homework for CS2103", task.getDescription());
    Duration timeDetails = ((DeadlineTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getEndDate());
    assertEquals("2359", timeDetails.getEndTime());
    assertEquals(Integer.parseInt("2"), task.getTaskID());
  }
  
  @Test
  public void testUpdateDeadlineTaskWithTime() {
    //test update for deadline task that specifies the time
    Command result = tester.parse("update 2 homework for CS2103. 221015 2000");
    assertEquals("update", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("homework for CS2103", task.getDescription());
    Duration timeDetails = ((DeadlineTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getEndDate());
    assertEquals("2000", timeDetails.getEndTime());
    assertEquals(Integer.parseInt("2"), task.getTaskID());
  }
  
  @Test
  public void testUpdateDurationTaskWithoutTwoDates() {
    //test update command for duration task that does not specify 2 dates
    Command result = tester.parse("update 2 CS2103 meeting. 221015 1400 1600");
    assertEquals("update", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("CS2103 meeting", task.getDescription());
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getStartDate());
    assertEquals("1400", timeDetails.getStartTime());
    assertEquals("221015", timeDetails.getEndDate());
    assertEquals("1600", timeDetails.getEndTime());
    assertEquals(Integer.parseInt("2"), task.getTaskID());
  }
  
  @Test
  public void testUpdateDurationTaskWithTwoDates() {
    //test update command for duration task that specifies 2 dates
    Command result = tester.parse("update 2 super long seminar. 221015 1400 291015 1800");
    assertEquals("update", result.getCommand());
    Tasks task = result.getTask();
    assertEquals("super long seminar", task.getDescription());
    Duration timeDetails = ((DurationTask)task).getDurationDetails();
    assertEquals("221015", timeDetails.getStartDate());
    assertEquals("1400", timeDetails.getStartTime());
    assertEquals("291015", timeDetails.getEndDate());
    assertEquals("1800", timeDetails.getEndTime());
    assertEquals(Integer.parseInt("2"), task.getTaskID());
  }
  
  @Test
  public void testUpdateNullDescription() {
    Command result = tester.parse("update 4 home.work");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testUpdateNullTime() {
    Command result = tester.parse("update 4 homework. 181515 1400");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testNormalDisplay() {
    Command result = tester.parse("display");
    assertEquals("display", result.getCommand());
    assertEquals(null, result.getStoreSearchAndDisplayStrings());
  }
  
  // ================================================================================
  // Test delete methods
  // ================================================================================
  
  @Test
  public void testDeleteOneItem() {
    //test deleting of one item
    Command result = tester.parse("delete 1");
    assertEquals("delete", result.getCommand());
    ArrayList<Integer> inputDeleteIndexes = result.getSelectedIndexNumbers();
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDeleteIndexes.get(i));
    }
    
  }
  
  @Test
  public void testDeleteMultipleItems() {
    //test deleting of multiple input items
    Command result = tester.parse("delete 1 5 9");
    assertEquals("delete", result.getCommand());
    ArrayList<Integer> inputDeleteIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    items.add(5);
    items.add(9);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDeleteIndexes.get(i));
    }
  }
  
  @Test
  public void testDeleteRangeOfItems() {
    //test deleting of a range of items, where the end is greater than start
    Command result = tester.parse("delete 1-3");
    assertEquals("delete", result.getCommand());
    ArrayList<Integer> inputDeleteIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    items.add(2);
    items.add(3);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDeleteIndexes.get(i));
    }
  }
  
  @Test
  public void testDeleteRangeOfItemsReversed() {
    //test deleting of a range of items, where the start is greater than end
    Command result = tester.parse("delete 3-1");
    assertEquals("delete", result.getCommand());
    ArrayList<Integer> inputDeleteIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    items.add(2);
    items.add(3);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDeleteIndexes.get(i));
    }
  }
  
  @Test
  public void testEmptyDelete() {
    Command result = tester.parse("delete");
    assertEquals("invalid", result.getCommand());
  }
  
  
  @Test
  public void testMultipleTypesTogetherDelete() {
    Command result = tester.parse("delete 5-8 10");
    assertEquals("invalid", result.getCommand());
  }
  
  // ================================================================================
  // Test done methods
  // ================================================================================
  
  @Test
  public void testDoneOneItem() {
    //test marking of one item as done
    Command result = tester.parse("done 1");
    assertEquals("done", result.getCommand());
    ArrayList<Integer> inputDoneIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDoneIndexes.get(i));
    }
  }
  
  @Test
  public void testDoneMultipleItems() {
    //test marking multiple items as done
    Command result = tester.parse("done 2 6 8");
    assertEquals("done", result.getCommand());
    ArrayList<Integer> inputDoneIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(2);
    items.add(6);
    items.add(8);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDoneIndexes.get(i));
    }
  }
  
  @Test
  public void testDoneRangeOfItems() {
    //test marking a range of items as done, where the end is greater than start
    Command result = tester.parse("done 1-3");
    assertEquals("done", result.getCommand());
    ArrayList<Integer> inputDoneIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    items.add(2);
    items.add(3);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDoneIndexes.get(i));
    }
  }
  
  @Test
  public void testDoneRangeOfItemsReversed() {
    //test marking a range of items as done, where the start is greater than end
    Command result = tester.parse("done 3-1");
    assertEquals("done", result.getCommand());
    ArrayList<Integer> inputDoneIndexes = result.getSelectedIndexNumbers();
    
    ArrayList<Integer> items = new ArrayList<Integer>();
    items.add(1);
    items.add(2);
    items.add(3);
    
    for(int i = 0; i < items.size(); i++) {
      assertEquals(items.get(i), inputDoneIndexes.get(i));
    }
  }
  
  @Test
  public void testEmptyDone() {
    Command result = tester.parse("done");
    assertEquals("invalid", result.getCommand());
  }
  
  
  // ================================================================================
  // Test search methods
  // ================================================================================
  
  @Test
  public void testSearchOneWord() {
    //test search for a single keyword
    Command result = tester.parse("search homework");
    assertEquals("search", result.getCommand());
    assertEquals("homework", result.getStoreSearchAndDisplayStrings());
  }
  
  @Test
  public void testSearchStringOfWords() {
    //test search for a string
    Command result = tester.parse("search homework for CS2103");
    assertEquals("search", result.getCommand());
    assertEquals("homework for CS2103", result.getStoreSearchAndDisplayStrings());
  }
  
  // ================================================================================
  // Test store methods
  // ================================================================================
  
  @Test
  public void testStoreCommand() {
    //test the store command, input has been escaped to prevent errors
    Command result = tester.parse("store C:\\Users\\XXXX\\Desktop\\myfile.txt");
    assertEquals("store", result.getCommand());
    //final string of directory has added backslashes to account for escaping
    assertEquals("C:\\\\Users\\\\XXXX\\\\Desktop\\\\myfile.txt", result.getStoreSearchAndDisplayStrings());
  }
  
  @Test
  public void testStoreForwardSlash() {
    Command result = tester.parse("store C:/Users/alan/Downloads");
    assertEquals("C:/Users/alan/Downloads", result.getStoreSearchAndDisplayStrings());
  }
  
  // ================================================================================
  // Test display methods
  // ================================================================================
  
  @Test
  public void testTooManyDatesDisplayNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display 111115 141115 151115");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testInvalidSecondTermDisplay() {
    Command result = tester.parse("display hahaha");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testKeywordDisplay() {
    Command result = tester.parse("display archive");
    assertEquals("display", result.getCommand());
    assertEquals("archive", result.getStoreSearchAndDisplayStrings());
    Command result2 = tester.parse("display floating");
    assertEquals("display", result2.getCommand());
    assertEquals("floating", result2.getStoreSearchAndDisplayStrings());
    Command result3 = tester.parse("display deadline");
    assertEquals("display", result3.getCommand());
    assertEquals("deadline", result3.getStoreSearchAndDisplayStrings());
    Command result4 = tester.parse("display duration");
    assertEquals("display", result4.getCommand());
    assertEquals("duration", result4.getStoreSearchAndDisplayStrings());
  }
  
  @Test
  public void testDisplayOnDate() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display on monday");
    assertEquals("display", result.getCommand());
    assertEquals(1, result.getDisplayType());
    assertEquals("091115", result.getTimeArray().get(0));
  }
  
  @Test
  public void testDisplayOnDateNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display 111115");
    assertEquals("display", result.getCommand());
    assertEquals(1, result.getDisplayType());
    assertEquals("111115", result.getTimeArray().get(0));
  }
  
  @Test
  public void testDisplayOnDateInvalidDateNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display 331115");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testDisplayOnDateWithOnKeywordNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display on 131115");
    assertEquals("display", result.getCommand());
    assertEquals(1, result.getDisplayType());
    assertEquals("131115", result.getTimeArray().get(0));
  }
  
  @Test
  public void testDisplayByDateInvalidDateNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display by 331115");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testDisplayByDate() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display by monday");
    assertEquals("display", result.getCommand());
    assertEquals(2, result.getDisplayType());
    assertEquals("091115", result.getTimeArray().get(0));
  }
  
  @Test
  public void testDisplayByDateNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display by 111115");
    assertEquals("display", result.getCommand());
    assertEquals(2, result.getDisplayType());
    assertEquals("111115", result.getTimeArray().get(0));
  }
  
  @Test
  public void testDisplayDateRange() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display from monday to wednesday");
    assertEquals("display", result.getCommand());
    assertEquals(3, result.getDisplayType());
    assertEquals("091115", result.getTimeArray().get(0));
    assertEquals("111115", result.getTimeArray().get(1));
  }
  
  @Test
  public void testDisplayFlippedDateRangeNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display from 131115 to 111115");
    assertEquals("display", result.getCommand());
    assertEquals(3, result.getDisplayType());
    assertEquals("111115", result.getTimeArray().get(0));
    assertEquals("131115", result.getTimeArray().get(1));
  }
  
  @Test
  public void testDisplayDateRangeNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display from 111115 to 131115");
    assertEquals("display", result.getCommand());
    assertEquals(3, result.getDisplayType());
    assertEquals("111115", result.getTimeArray().get(0));
    assertEquals("131115", result.getTimeArray().get(1));
  }
  
  @Test
  public void testDisplayDateRangeInvalidStartDateNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display from 331115 to 131115");
    assertEquals("invalid", result.getCommand());
  }
  
  @Test
  public void testDisplayDateRangeInvalidEndDateNoNaturalLanguage() throws Exception {
    Date reference = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse("11/4/2015 12:00 am");
    CalendarSource.setBaseDate(reference);
    Command result = tester.parse("display from 111115 to 331115");
    assertEquals("invalid", result.getCommand());
  }
  
  // ================================================================================
  // Test other methods
  // ================================================================================
  
  @Test
  public void testUndoCommand() {
    Command result = tester.parse("undo");
    assertEquals("undo", result.getCommand());
  }
  
  @Test
  public void testClearCommand() {
    Command result = tester.parse("clear");
    assertEquals("clear", result.getCommand());
  }
  
  @Test
  public void testExitCommand() {
    Command result = tester.parse("exit");
    assertEquals("exit", result.getCommand());
  }
  
  @Test
  public void testDrunkenClearCommand() {
    Command result = tester.parse("cLeAr");
    assertEquals("clear", result.getCommand());
  }
  
  @Test
  public void testRandomCommand() {
    Command result = tester.parse("lol");
    assertEquals("invalid", result.getCommand());
  }
}
