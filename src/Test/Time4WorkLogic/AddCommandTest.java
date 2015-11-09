package Test.Time4WorkLogic;


import org.junit.Test;

import Time4WorkData.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkData.FloatingTask;
import Time4WorkData.Tasks;

import static org.junit.Assert.*;

//@@author A0133894W
public class AddCommandTest {
	 private static final String MESSAGE_ADDED_ = "Task added %1$s !";
	 private static final String MESSAGE_UNDO_ = "Undo %1$s!";
	 Logic logic = new Logic();
	
	 /*This is the test for normal adding*/
	@Test
	public void addTest() throws Exception {
		logic.executeClear();
		
		Tasks newTask = new FloatingTask("reading.");
		newTask.setTaskID(0);
		
		FeedbackMessage feedbackMessage = logic.executeAdd(newTask);
		
		assertEquals(String.format(MESSAGE_ADDED_, "successfully"), feedbackMessage.getFeedback());
		assertEquals(1, logic.getIncompleteTaskList().size());
		assertEquals(newTask.getDescription(), feedbackMessage.getIncompleteTaskList().get(0).getDescription());
		assertEquals(newTask.getTaskID(), feedbackMessage.getIncompleteTaskList().get(0).getTaskID());
		

		//test undo for adding
		feedbackMessage = logic.executeUndo();
		assertEquals(String.format(MESSAGE_UNDO_, "successfully!"), feedbackMessage.getFeedback());
		assertEquals(0, logic.getIncompleteTaskList().size());
	}
	

	
}