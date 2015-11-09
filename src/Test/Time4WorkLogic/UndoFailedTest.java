package Test.Time4WorkLogic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Time4WorkData.FeedbackMessage;
import Time4WorkLogic.Logic;

//@@author A0133894W
public class UndoFailedTest {
	
	private static final String MESSAGE_UNDO_ = "Undo %1$s!";
	Logic logic = new Logic();
	@Test
	public void undoFailedTest() {
		FeedbackMessage feedbackMessage = logic.executeUndo();
        assertEquals(String.format(MESSAGE_UNDO_, "failed: no more command for undo"), feedbackMessage.getFeedback());
	}
}
