package Test.Time4WorkLogic;


import org.junit.Test;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class SortCommandTest {
	private static final String MESSAGE_SORTED = "Task sorted successfully!";
	Logic logic = new Logic();
	
	 /*This is the test for sort command*/
	@Test
	public void searchTaskSuccessfullyTest() throws Exception{
		
		logic.executeClear();
		Tasks task1 = new FloatingTask("reading1.");
		logic.executeAdd(task1);
		Tasks task2 = new FloatingTask("reading book.");
		logic.executeAdd(task2);
		Tasks task3 = new FloatingTask("playing.");
		logic.executeAdd(task3);
		
		ArrayList<Tasks> taskList = new ArrayList<Tasks>();
		taskList = logic.getMyTaskList();
		FeedbackMessage feedbackMessage = logic.executeSort();
		assertEquals(String.format(MESSAGE_SORTED, "successfully"), feedbackMessage.getFeedback());
		
		assertEquals(task1.getTaskID(), feedbackMessage.getTaskList().get(1));
		assertEquals(task2.getTaskID(), feedbackMessage.getTaskList().get(2));
		assertEquals(task3.getTaskID(), feedbackMessage.getTaskList().get(0));
	}
	
}