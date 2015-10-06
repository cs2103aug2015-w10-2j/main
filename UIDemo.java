import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class UIDemo extends JPanel implements ActionListener {

	private JTextField commandInput;
    private static JTextArea taskContent;
    private JLabel feedback;
    private static Logic logic = new Logic();
    private static ArrayList<Tasks> currentList = new ArrayList<Tasks>();

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

        currentList = logic.getCurrentList();
		taskContent.setText(display(currentList));
    }

    public void actionPerformed(ActionEvent evt) {
		try {
			String userCommand = commandInput.getText();

			FeedbackMessage output;
			output = logic.executeCommand(userCommand);
			currentList = logic.getCurrentList();
			taskContent.setText(display(currentList));
			feedback.setText(output.getFeedback());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        commandInput.selectAll();

        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        taskContent.setCaretPosition(taskContent.getDocument().getLength());
    }

    public static String display(ArrayList<Tasks> taskList){
        String taskListContent = "";
        int taskListSize = taskList.size();
        for (int i = 0; i < taskListSize - 1; i++){
            taskListContent += (i+1) + ". " + taskList.get(i).getDescription() + "\n";
        }
        taskListContent += taskList.get(taskListSize - 1);
        return taskListContent;
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
