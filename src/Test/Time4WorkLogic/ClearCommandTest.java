package Test.Time4WorkLogic;


import org.junit.Test;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

import static org.junit.Assert.*;
import java.io.IOException;

public class ClearCommandTest {
	private static final String MESSAGE_CLEAR = "All task cleared successfully!";
	private static final String MESSAGE_UNDO_ = "Undo %1$s!";
	Logic logic = new Logic();
	
	 /*This is the test for clear all the tasks*/
	@Test
	public void addTest() throws Exception{
		Tasks task1 = new FloatingTask("reading1.");
		logic.executeAdd(task1);
		Tasks task2 = new FloatingTask("reading2.");
		logic.executeAdd(task2);
		Tasks task3 = new FloatingTask("reading3.");
		logic.executeAdd(task3);
		
		
		FeedbackMessage feedbackMessage = logic.executeClear();
		
		assertEquals(String.format(MESSAGE_CLEAR, "successfully"), feedbackMessage.getFeedback());
		assertEquals(0, logic.getMyTaskList().size());
		
		//test undo
		feedbackMessage = logic.executeUndo();
		assertEquals(String.format(MESSAGE_UNDO_, "successfully!"), feedbackMessage.getFeedback());
		assertEquals(3, feedbackMessage.getTaskList().size());
	}
	
}