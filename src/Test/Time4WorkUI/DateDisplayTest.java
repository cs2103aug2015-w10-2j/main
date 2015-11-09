package Test.Time4WorkUI;

import static org.junit.Assert.*;

import org.junit.Test;

import Time4WorkLogic.Logic;
import Time4WorkData.Tasks;
import Time4WorkUI.DateDisplay;

//@@author A0112077N
public class DateDisplayTest {
	private DateDisplay display = new DateDisplay();
	private Logic logic= new Logic();

	private String FLOATING_TASK = "I am a floating task.";

	private String DEADLINE_TASK1 = "I am a deadline task. by 12 dec noon";
	private String DEADLINE_TASK2 = "I am a deadline task. by 12 dec";
	private String DEADLINE_TASK3 = "I am a deadline task. 121215";
	private String DEADLINE_TASK4 = "I am a deadline task. 121215 1200";

	private String DURATION_TASK1 = "I am a duration task. from 10 oct noon to 11 oct 2pm";
	private String DURATION_TASK2 = "I am a duration task. 101015 1200 111015 1400";
	private String DURATION_TASK3 = "I am a duration task. 10 oct noon to 2pm";
	private String DURATION_TASK4 = "I am a duration task. 101015 1200 1400";

	@Test
	public void TestFloatingTask() throws Exception{
		logic.executeCommand("clear");
		logic.executeCommand("add " + FLOATING_TASK);
		Tasks task = logic.getIncompleteTaskList().get(0);
		assertEquals("", display.getStartDuration(task));
		assertEquals("", display.getEndDuration(task));
	}

	@Test
	public void TestDeadlineTask() throws Exception{
		logic.executeCommand("clear");
		logic.executeCommand("add " + DEADLINE_TASK1);
		Tasks task1 = logic.getIncompleteTaskList().get(0);
		assertEquals("", display.getStartDuration(task1));
		assertEquals("12 Dec 15, 12:00", display.getEndDuration(task1));

		logic.executeCommand("clear");
		logic.executeCommand("add " + DEADLINE_TASK2);
		Tasks task2 = logic.getIncompleteTaskList().get(0);
		assertEquals("", display.getStartDuration(task2));
		assertEquals("12 Dec 15, 23:59", display.getEndDuration(task2));

		logic.executeCommand("clear");
		logic.executeCommand("add " + DEADLINE_TASK3);
		Tasks task3 = logic.getIncompleteTaskList().get(0);
		assertEquals("", display.getStartDuration(task3));
		assertEquals("12 Dec 15, 23:59", display.getEndDuration(task3));

		logic.executeCommand("clear");
		logic.executeCommand("add " + DEADLINE_TASK4);
		Tasks task4 = logic.getIncompleteTaskList().get(0);
		assertEquals("", display.getStartDuration(task4));
		assertEquals("12 Dec 15, 12:00", display.getEndDuration(task4));
	}

	@Test
	public void TestDurationTask() throws Exception{
		logic.executeCommand("clear");
		logic.executeCommand("add " + DURATION_TASK1);
		Tasks task1 = logic.getIncompleteTaskList().get(0);
		assertEquals("10 Oct 15, 12:00", display.getStartDuration(task1));
		assertEquals("11 Oct 15, 14:00", display.getEndDuration(task1));

		logic.executeCommand("clear");
		logic.executeCommand("add " + DURATION_TASK2);
		Tasks task2 = logic.getIncompleteTaskList().get(0);
		assertEquals("10 Oct 15, 12:00", display.getStartDuration(task2));
		assertEquals("11 Oct 15, 14:00", display.getEndDuration(task2));

		logic.executeCommand("clear");
		logic.executeCommand("add " + DURATION_TASK3);
		Tasks task3 = logic.getIncompleteTaskList().get(0);
		assertEquals("10 Oct 15, 12:00", display.getStartDuration(task3));
		assertEquals("10 Oct 15, 14:00", display.getEndDuration(task3));

		logic.executeCommand("clear");
		logic.executeCommand("add " + DURATION_TASK4);
		Tasks task4 = logic.getIncompleteTaskList().get(0);
		assertEquals("10 Oct 15, 12:00", display.getStartDuration(task4));
		assertEquals("10 Oct 15, 14:00", display.getEndDuration(task4));
	}
}
