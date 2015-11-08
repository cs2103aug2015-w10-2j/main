package Test.Time4WorkLogic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Time4WorkLogic.Logic;
import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

//@@ author A0133894W
public class SortTaskTest {
	 Logic logic = new Logic();
	
	 /*Test for sorting tasks after adding*/
	@Test
	public void addTest() throws Exception{
		logic.executeClear();
	
		Tasks newFloatingTask = new FloatingTask("floating task");
		logic.executeAdd(newFloatingTask);
		
		Duration earlyDeadline = new Duration("011115", "2359");
		Tasks earlyDeadlineTask = new DeadlineTask("early deadline", earlyDeadline);
		logic.executeAdd(earlyDeadlineTask);
		
		Duration lateDeadline = new Duration("111115", "2359");
		Tasks lateDeadlineTask = new DeadlineTask("late deadline", lateDeadline);
		logic.executeAdd(lateDeadlineTask);
		
		Duration duration = new Duration("101115", "1000", "101115", "1111");
		Tasks durationTask = new DurationTask("duration task", duration);
		logic.executeAdd(durationTask);
		

		assertEquals(2, logic.getIncompleteTaskList().get(0).getTaskID());
		assertEquals(4, logic.getIncompleteTaskList().get(1).getTaskID());
		assertEquals(3, logic.getIncompleteTaskList().get(2).getTaskID());
		assertEquals(1, logic.getIncompleteTaskList().get(3).getTaskID());

		
	}
	


}
