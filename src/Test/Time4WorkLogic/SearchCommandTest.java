package Test.Time4WorkLogic;

import org.junit.Test;

import Time4WorkData.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkData.FloatingTask;
import Time4WorkData.Tasks;

import static org.junit.Assert.*;

//@@author A0133894W
public class SearchCommandTest {
    private static final String MESSAGE_SEARCH_ = "Task searched %1$s";
    Logic logic = new Logic();
    
    /*This is the test for search task successfully*/
    @Test
    public void searchTaskSuccessfullyTest() throws Exception {
        
        logic.executeClear();
        Tasks task1 = new FloatingTask("reading1.");
        logic.executeAdd(task1);
        Tasks task2 = new FloatingTask("reading book.");
        logic.executeAdd(task2);
        Tasks task3 = new FloatingTask("playing.");
        logic.executeAdd(task3);
        
        
        FeedbackMessage feedbackMessage = logic.executeSearchDescription("r");
        assertEquals(String.format(MESSAGE_SEARCH_, "successfully"), feedbackMessage.getFeedback());
        
        assertEquals(3, logic.getIncompleteTaskList().size());
        assertFalse(logic.getIncompleteTaskList().contains(task3));
        
    }
    /*This is the test for search task failed*/
    @Test
    public void searchTaskFailedTest() throws Exception {
        
        logic.executeClear();
        Tasks task1 = new FloatingTask("reading1.");
        logic.executeAdd(task1);
        Tasks task2 = new FloatingTask("reading book.");
        logic.executeAdd(task2);
        Tasks task3 = new FloatingTask("playing.");
        logic.executeAdd(task3);
        
        
        FeedbackMessage feedbackMessage = logic.executeSearchDescription("dsfgdigoogjd");
        assertEquals(String.format(MESSAGE_SEARCH_, "failed: no such task"), feedbackMessage.getFeedback());
        
        assertEquals(3, logic.getIncompleteTaskList().size());
        
    }
    
}