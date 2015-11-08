package Test.Time4WorkLogic;

import org.junit.Test;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

import static org.junit.Assert.*;
import java.util.ArrayList;

//@@author A0133894W
public class DoneCommandTest {
	private static final String MESSAGE_UNDO_ = "Undo %1$s!";
	Logic logic = new Logic();
	
	 /*This is the test for mark one task as done*/
	@Test
	public void markOneTest() throws Exception{
		
		logic.executeClear();
		Tasks task1 = new FloatingTask("reading1.");
		logic.executeAdd(task1);
		Tasks task2 = new FloatingTask("reading2.");
		logic.executeAdd(task2);
		Tasks task3 = new FloatingTask("reading3.");
		logic.executeAdd(task3);
		
		ArrayList<Integer> indexToBeMarked = new ArrayList<Integer>();
		indexToBeMarked.add(2);
		FeedbackMessage feedbackMessage = logic.executeMarkTaskAsDone(indexToBeMarked);
		assertEquals("Mark task 2  as done successfully !", feedbackMessage.getFeedback());
		
		assertEquals(2, logic.getIncompleteTaskList().size());
		assertEquals(1, logic.getCompleteTaskList().size());
		assertFalse(logic.getIncompleteTaskList().contains(task2));
		
		//test undo
		feedbackMessage = logic.executeUndo();
		assertEquals(String.format(MESSAGE_UNDO_, "successfully!"), feedbackMessage.getFeedback());
		assertEquals(3, feedbackMessage.getIncompleteTaskList().size());
		assertEquals(0, feedbackMessage.getCompleteTaskList().size());
	}
	
	/*Test for mark multiple tasks as done*/
	@Test
	public void markMulTest() throws Exception{
		
		logic.executeClear();
		Tasks task1 = new FloatingTask("reading1.");
		logic.executeAdd(task1);
		Tasks task2 = new FloatingTask("reading2.");
		logic.executeAdd(task2);
		Tasks task3 = new FloatingTask("reading3.");
		logic.executeAdd(task3);
		
		ArrayList<Integer> indexToBeMarked = new ArrayList<Integer>();
		indexToBeMarked.add(2);
		indexToBeMarked.add(3);
		FeedbackMessage feedbackMessage = logic.executeMarkTaskAsDone(indexToBeMarked);
		assertEquals("Mark task 2 3  as done successfully !", feedbackMessage.getFeedback());
		
		assertEquals(1, logic.getIncompleteTaskList().size());
		assertEquals(2, logic.getCompleteTaskList().size());
		assertFalse(logic.getIncompleteTaskList().contains(task2));
		assertFalse(logic.getIncompleteTaskList().contains(task3));
		
		//test undo 
		feedbackMessage = logic.executeUndo();
		assertEquals(String.format(MESSAGE_UNDO_, "successfully!"), feedbackMessage.getFeedback());
		assertEquals(3, feedbackMessage.getIncompleteTaskList().size());
		assertEquals(0, feedbackMessage.getCompleteTaskList().size());
	}
	
	/*Test for mark task as done failed (index out of range)*/
	@Test
	public void  markFailedTest() throws Exception{
		
		logic.executeClear();
		Tasks task1 = new FloatingTask("reading1.");
		logic.executeAdd(task1);
		Tasks task2 = new FloatingTask("reading2.");
		logic.executeAdd(task2);
		Tasks task3 = new FloatingTask("reading3.");
		logic.executeAdd(task3);
		
		ArrayList<Integer> indexToBeMarked = new ArrayList<Integer>();
		indexToBeMarked.add(5);
		FeedbackMessage feedbackMessage = logic.executeMarkTaskAsDone(indexToBeMarked);
		assertEquals("Mark task 5  as done failed !", feedbackMessage.getFeedback());
		
		assertEquals(3, logic.getIncompleteTaskList().size());
		assertEquals(0, logic.getCompleteTaskList().size());
		
	}
	
}