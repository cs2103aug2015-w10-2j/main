package Test.Time4WorkLogic;

import org.junit.Test;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;

public class DeleteCommandTest {
    private static final String MESSAGE_DELETED_ = "Task %1$s deleted %2$s !";
    private static final String MESSAGE_UNDO_ = "Undo %1$s!";
    Logic logic = new Logic();
    
    /*This is the test for delete one task*/
    @Test
    public void deleteOneTest() throws Exception{
        
        logic.executeClear();
        Tasks task1 = new FloatingTask("reading1.");
        logic.executeAdd(task1);
        Tasks task2 = new FloatingTask("reading2.");
        logic.executeAdd(task2);
        Tasks task3 = new FloatingTask("reading3.");
        logic.executeAdd(task3);
        
        ArrayList<Integer> indexToBeDeleted = new ArrayList<Integer>();
        indexToBeDeleted.add(2);
        FeedbackMessage feedbackMessage = logic.executeDelete(indexToBeDeleted);
        assertEquals("Task 2  deleted successfully !", feedbackMessage.getFeedback());
        
        assertEquals(2, logic.getIncompleteTaskList().size());
        assertFalse(logic.getIncompleteTaskList().contains(task2));
        
        //test undo
        feedbackMessage = logic.executeUndo();
        assertEquals(String.format(MESSAGE_UNDO_, "successfully!"), feedbackMessage.getFeedback());
        assertEquals(3, feedbackMessage.getIncompleteTaskList().size());
    }
    
    /*Test for delete multiple tasks*/
    @Test
    public void deleteMulTest() throws Exception{
        
        logic.executeClear();
        Tasks task1 = new FloatingTask("reading1.");
        logic.executeAdd(task1);
        Tasks task2 = new FloatingTask("reading2.");
        logic.executeAdd(task2);
        Tasks task3 = new FloatingTask("reading3.");
        logic.executeAdd(task3);
        
        ArrayList<Integer> indexToBeDeleted = new ArrayList<Integer>();
        indexToBeDeleted.add(2);
        indexToBeDeleted.add(3);
        FeedbackMessage feedbackMessage = logic.executeDelete(indexToBeDeleted);
        assertEquals("Task 2 3  deleted successfully !", feedbackMessage.getFeedback());
        
        assertEquals(1, logic.getIncompleteTaskList().size());
        assertFalse(logic.getIncompleteTaskList().contains(task2));
        assertFalse(logic.getIncompleteTaskList().contains(task3));
        
        //test undo
        feedbackMessage = logic.executeUndo();
        assertEquals(String.format(MESSAGE_UNDO_, "successfully!"), feedbackMessage.getFeedback());
        assertEquals(3, feedbackMessage.getIncompleteTaskList().size());
    }
    
    /*Test for delete failed (index out of range)*/
    @Test
    public void deleteFailedTest() throws Exception{
        
        logic.executeClear();
        Tasks task1 = new FloatingTask("reading1.");
        logic.executeAdd(task1);
        Tasks task2 = new FloatingTask("reading2.");
        logic.executeAdd(task2);
        Tasks task3 = new FloatingTask("reading3.");
        logic.executeAdd(task3);
        
        ArrayList<Integer> indexToBeDeleted = new ArrayList<Integer>();
        indexToBeDeleted.add(5);
        FeedbackMessage feedbackMessage = logic.executeDelete(indexToBeDeleted);
        assertEquals("Task 5  deleted failed: index out of range !", feedbackMessage.getFeedback());
        
        assertEquals(3, logic.getIncompleteTaskList().size());
        
    }
    
}