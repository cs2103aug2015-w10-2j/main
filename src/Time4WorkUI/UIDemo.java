package Time4WorkUI;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

public class UIDemo extends JPanel implements ActionListener {
    
    private JTextField commandInput;
    private static JTextArea taskContent;
    private JLabel feedback;
    private static Logic logic = new Logic();
    private static ArrayList<Tasks> currentList = new ArrayList<Tasks>();
    private static final Logger logger = Logger.getLogger(Logic.class.getName());
    
    public UIDemo() {
        super(new GridBagLayout());
        
        commandInput = new JTextField(50);
        commandInput.addActionListener(this);
        
        feedback = new JLabel();
        
        taskContent = new JTextArea(25, 50);
        taskContent.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(taskContent);
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        c.fill = GridBagConstraints.HORIZONTAL;
        add(commandInput, c);
        add(feedback, c);
        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
        
        currentList = logic.getFullTaskList();
        taskContent.setText(display(currentList));
    }
    
    public void actionPerformed(ActionEvent evt) {
        try {
            String userCommand = commandInput.getText();
            logger.log(Level.INFO, userCommand + "was entered.");
            FeedbackMessage output;
            output = logic.executeCommand(userCommand);
            currentList = output.getTaskList();
            taskContent.setText(display(currentList));
            feedback.setText(output.getFeedback());
            logger.log(Level.INFO, "feedback received");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        commandInput.selectAll();
        
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        taskContent.setCaretPosition(taskContent.getDocument().getLength());
    }
    
   	public static String display(ArrayList<Tasks> taskList) {
		String taskListContent = "";
		int taskListSize = taskList.size();
		if (taskListSize != 0) {
			for (int i = 0; i < taskListSize - 1; i++) {
				taskListContent += (i + 1) + ". " + taskList.get(i).getDescription() + "     "
						+ getStartDuration(taskList.get(i)) + "     " + getEndDuration(taskList.get(i)) + "\n";
			}
			taskListContent += taskListSize + ". " + taskList.get(taskListSize - 1).getDescription()+
					"     " + getStartDuration(taskList.get(taskListSize - 1)) + "     " + getEndDuration(taskList.get(taskListSize - 1));
		}
		return taskListContent;
	}

	private static String getStartDuration(Tasks task) {
		Duration taskDuration = new Duration("","","","") ;
		String startDuration = "";

		if (task instanceof DeadlineTask) {
			DeadlineTask classifiedTask = (DeadlineTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof DurationTask) {
			DurationTask classifiedTask = (DurationTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof FloatingTask) {
			return "";
		}
		String date = taskDuration.getEndDate();
		String time = taskDuration.getEndTime();
		String dateDisplay = date.substring(0,2)+ "/" + date.substring(2,4) + "/" + date.substring(4);
		String timeDisplay = time.substring(0,2)+ ":" + time.substring(2,4) ;
		startDuration += dateDisplay + "  " + timeDisplay;
		return startDuration;
	}

	private static String getEndDuration(Tasks task) {
		Duration taskDuration = new Duration("", "", "", "");
		String endDuration = "";

		if (task instanceof DeadlineTask) {
			DeadlineTask classifiedTask = (DeadlineTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof DurationTask) {
			DurationTask classifiedTask = (DurationTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof FloatingTask) {
			return "";
		}

		String date = taskDuration.getEndDate();
		String time = taskDuration.getEndTime();
		String dateDisplay = date.substring(0,2)+ "/" + date.substring(2,4) + "/" + date.substring(4);
		String timeDisplay = time.substring(0,2)+ ":" + time.substring(2,4) ;
		endDuration += dateDisplay + "  " + timeDisplay;
		return endDuration;
	}
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Time4WorkDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(new UIDemo());
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
