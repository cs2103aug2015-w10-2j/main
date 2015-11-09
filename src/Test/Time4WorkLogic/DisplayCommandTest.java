package Test.Time4WorkLogic;

import org.junit.Test;

import Time4WorkData.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkData.*;

import static org.junit.Assert.*;

//@@author A0133894W
public class DisplayCommandTest {
	 Logic logic = new Logic();
	
	 /*This is the test for normal adding*/
	@Test
	public void displayTest() throws Exception{
		logic.executeClear();
		
        Tasks newFloatingTask = new FloatingTask("floating task");
		logic.executeAdd(newFloatingTask);
		
		Duration deadline1 = new Duration("011115", "2359");
		Tasks deadlineTask1 = new DeadlineTask("early deadline", deadline1);
		logic.executeAdd(deadlineTask1);
		
		Duration deadline2 = new Duration("111115", "2359");
		Tasks deadlineTask2 = new DeadlineTask("late deadline", deadline2);
		logic.executeAdd(deadlineTask2);
		
		Duration duration = new Duration("101115", "1000", "101115", "1111");
		Tasks durationTask = new DurationTask("duration task", duration);
		logic.executeAdd(durationTask);
		
		
		FeedbackMessage feedbackDDL = logic.executeDisplay("deadline");
		assertEquals(2, feedbackDDL.getIncompleteTaskList().size());
		FeedbackMessage feedbackFloating = logic.executeDisplay("floating");
		assertEquals(1, feedbackFloating.getIncompleteTaskList().size());
		FeedbackMessage feedbackDuration = logic.executeDisplay("duration");
		assertEquals(1, feedbackDuration.getIncompleteTaskList().size());
	}
	
}