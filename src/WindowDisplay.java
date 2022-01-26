import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class WindowDisplay
{
    private static JFrame frame;

    // file info in a single string, linked list for item management, doubles for holding displayed text.
    private static String fileText;
    private static LinkedList<String> linkedItemList;
    private static List<JTextArea> expenseVals;
    private static double income;
    private static double expenseSum;
    private static double grossSum;

    // 2d array of what's in the frame: text areas, labels, buttons.
    private static JPanel [][] displayHolder;

    // dimensions for 2d JPanel.
    private static final int ROWS = 4;
    private static final int COLS = 2;

    // labels for the three different text displays.
    private static JLabel incomeText;
    private static JLabel expensesText;
    private static JLabel grossText;

    // text areas to show the relevant data.
    private static JTextArea showIncome;
    private static JTextArea showExpenses;
    private static JTextArea showGross;

    // edits income inside the .txt and displays new income. edit expenses for editing expense info.
    private static JButton editIncome;
    private static JButton editExpenses;
    private static JButton addExpense;

    // creates JFrame and calls other methods for creating the frame UI.
    public static void initWindow()
    {
        frame = new JFrame("Budget");
        frame.setPreferredSize(new Dimension(580, 290));
        frame.setLayout(new GridLayout(ROWS, COLS));

        // methods for frame UI creation.
        panelCreate();
        labelCreate();
        textBoxCreate();
        buttonCreate();

        // inserting labels and text fields to specific spots on frame.
        addComponents();

        // frame settings and then setting to visible.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // handles the making of buttons.
    private static void buttonCreate() {
        editIncome = new JButton("Edit Income");
        editExpenses = new JButton("Edit Expenses");
        addExpense = new JButton("Add Expense");

        // if edit income button is clicked...
        editIncome.addActionListener(e -> {
            String stringInc = JOptionPane.showInputDialog(frame, "Enter new weekly income", null);

            // if not empty, attempt to extract double.
            if (stringInc != null) {
                try {
                    income = Double.parseDouble(stringInc);
                    updateIncome();
                    setTextFields();
                } catch (NumberFormatException ne) {
                    showIncome.setText(("$ " + income));
                }
            }

            // empty or cancel, set text to previous income.
            else
                showIncome.setText(("$ " + income));
        });

        // when edit expenses button is clicked.
        editExpenses.addActionListener(e -> {
            expenseDialog();
        });

        // when add expenses button is clicked.
        addExpense.addActionListener(e -> {
            receiveNewExpense();
        });
    }

    private static void receiveNewExpense()
    {
        // panel to hold two input fields, one for expense name and one for its value.a
        JPanel expInput = new JPanel();
        expInput.setLayout(new GridLayout(2, 2, 10, 10));

        // labels to be clear on what the input should be.
        JLabel askExpenseName = new JLabel("Enter expense name: ");
        JLabel askExpenseVal = new JLabel("Enter expense cost: ");

        // text areas for input.
        JTextArea expenseName = new JTextArea();
        expenseName.setFont(new Font("Serif", Font.BOLD, 15));

        JTextArea expenseVal = new JTextArea();
        expenseVal.setFont(new Font("Serif", Font.BOLD, 15));
        expenseVal.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                String value = expenseVal.getText();
                // numbers and backspace only.
                if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyChar() == 8)
                    expenseVal.setEditable(true);
                else
                    expenseVal.setEditable(false);
            }
        });

        // placing the text areas and labels in correct locations.
        expInput.add(askExpenseName);
        expInput.add(expenseName);
        expInput.add(askExpenseVal);
        expInput.add(expenseVal);

        int val = JOptionPane.showConfirmDialog(null, expInput,
                "Enter new expense", JOptionPane.OK_CANCEL_OPTION);

        if (val == JOptionPane.OK_OPTION)
        {
            if (expenseName.getText() != null && expenseVal.getText() != null)
            {
                linkedItemList.add(expenseName.getText());
                linkedItemList.add(expenseVal.getText());
                updateFileInfo();
                setTextFields();
            }
        }
    }

    private static void expenseDialog()
    {
        // create a new frame and display labels in accordance with the number of expenses in linkedItemList.
        JFrame showExpenses = new JFrame("Edit Expenses");
        showExpenses.setPreferredSize(new Dimension(300, 350));

        // numRows calculated using # items in linkedItemList excluding income.
        int numRows = (linkedItemList.size() - 2) / 2;

        // main panel holding the 2d array of panels so we can include a scrollbar.
        JPanel mainExpPanel = new JPanel();
        mainExpPanel.setLayout(new GridLayout(numRows + 1, COLS));

        // populate the mainExpPanel with panels that will hold labels and text areas.
        JPanel [][] expensePanels = new JPanel[numRows + 1][COLS];
        expenseVals = new ArrayList<JTextArea>();

        for (int i = 0; i < numRows + 1; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                // adding each expensePanel to mainExpPanel in correct position.
                expensePanels[i][j] = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainExpPanel.add(expensePanels[i][j]);
            }
        }

        // adding labels to panels and expense values to text areas to edit.
        // textarea will need to only accept numbers.
        int listIter = 2;
        for (int i = 0; i < numRows; i++)
        {
            int colInd = 0;

            JLabel tempLabel = new JLabel(linkedItemList.get(listIter++));
            tempLabel.setFont(new Font("Serif", Font.PLAIN, 20));
            expensePanels[i][colInd++].add(tempLabel);

            JTextArea tempTArea = new JTextArea(linkedItemList.get(listIter++));
            tempTArea.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    String value = tempTArea.getText();
                    // numbers and backspace only.
                    if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyChar() == 8)
                        tempTArea.setEditable(true);
                    else
                        tempTArea.setEditable(false);
                }
            });
            tempTArea.setFont(new Font("Serif", Font.BOLD, 18));
            expenseVals.add(tempTArea);
            expensePanels[i][colInd].add(tempTArea);
        }

        // creating an OK and Cancel button to save changes to income.
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // adding action listener for OK button.
        okButton.addActionListener(e -> {
            // going through all items in text areas and inputting new values into linkedItemList.
            int itemListIter = 3;
            for (int i = 0; i < expenseVals.size(); i++)
            {
                String insertThis = expenseVals.get(i).getText();
                linkedItemList.set(itemListIter, insertThis);

                if ((itemListIter + 2) < linkedItemList.size())
                    itemListIter += 2;
            }

            // updating text inside the .txt file then resetting text fields on main frame.
            updateFileInfo();
            setTextFields();

            // closing the window after all the data is updated in the txt file.
            showExpenses.dispatchEvent(new WindowEvent(showExpenses, WindowEvent.WINDOW_CLOSING));
        });

        // simply closing the window.
        cancelButton.addActionListener(e -> {
            showExpenses.dispatchEvent(new WindowEvent(showExpenses, WindowEvent.WINDOW_CLOSING));
        });

        // adding the OK and Cancel button to last row of expensePanels.
        expensePanels[numRows][0].add(okButton);
        expensePanels[numRows][1].add(cancelButton);

        // finally adding the main panel to frame with scroll bar.
        showExpenses.add(new JScrollPane(mainExpPanel));

        showExpenses.setLocationRelativeTo(null);
        showExpenses.pack();
        showExpenses.setResizable(false);
        showExpenses.setVisible(true);
    }

    private static void panelCreate()
    {
        displayHolder = new JPanel[ROWS][COLS];

        for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                displayHolder[i][j] = new JPanel(new FlowLayout(FlowLayout.LEFT));
                frame.add(displayHolder[i][j]);
            }
        }
    }

    private static void labelCreate()
    {
        incomeText = new JLabel("Monthly Gross Income");
        incomeText.setFont(new Font("Serif", Font.PLAIN, 20));
        expensesText = new JLabel("Monthly Expenses");
        expensesText.setFont(new Font("Serif", Font.PLAIN, 20));
        grossText = new JLabel("Net Income (monthly)");
        grossText.setFont(new Font("Serif", Font.PLAIN, 20));

    }

    private static void textBoxCreate()
    {
        showIncome = new JTextArea();
        showIncome.setFont(new Font("Serif", Font.BOLD, 18));
        showExpenses = new JTextArea();
        showExpenses.setFont(new Font("Serif", Font.BOLD, 18));
        showGross = new JTextArea();
        showGross.setFont(new Font("Serif", Font.BOLD, 18));

        // grabbing income number from .txt file imported at beginning.
        setTextFields();

        showIncome.setEditable(false);
        showExpenses.setEditable(false);
        showGross.setEditable(false);
    }

    private static void setTextFields()
    {
        grabText();
        String [] splitComponents = fileText.split("\\s+");
        List<String> itemList = Arrays.asList(splitComponents);
        linkedItemList = new LinkedList<>(itemList);
        expenseSum = 0;

        for (int i = 0; i < itemList.size(); i++)
        {
            // file doesnt have income, default to 0.
            if (!linkedItemList.contains("income"))
            {
                linkedItemList.add(0, "0");
                linkedItemList.add(0, "income");
            }

            // item is income, grab value as double.
            else if (linkedItemList.get(i).equals("income"))
            {
                income = 4 * Double.parseDouble(linkedItemList.get(++i));
                showIncome.setText("$ " + income);
            }

            // item isn't income, add to expense total.
            else
            {
                double num;
                try
                {
                    num = Double.parseDouble(linkedItemList.get(i));
                } catch (NumberFormatException ne)
                {
                    continue;
                }
                expenseSum += num;
            }
        }
        grossSum = income - expenseSum;

        showExpenses.setText("$ " + expenseSum);
        showGross.setText("$ " + grossSum);
    }

    private static void addComponents()
    {
        displayHolder[0][0].add(incomeText);
        displayHolder[0][1].add(showIncome);
        displayHolder[1][0].add(expensesText);
        displayHolder[1][1].add(showExpenses);
        displayHolder[2][0].add(grossText);
        displayHolder[2][1].add(showGross);
        displayHolder[3][0].add(editIncome);
        displayHolder[3][1].add(addExpense);
        displayHolder[3][1].add(editExpenses);
    }

    private static void updateIncome()
    {
        linkedItemList.remove(1);
        linkedItemList.add(1, Double.toString(income));
        updateFileInfo();
    }

    private static void grabText()
    {
        try
        {
            FileHandle.grabFile();
            Scanner reader = new Scanner(FileHandle.file);

            fileText = "";

            while (reader.hasNextLine())
                fileText += reader.nextLine().toLowerCase()+ " ";

        }
        catch (FileNotFoundException e)
        {
            FileHandle.newFile();
        }
    }

    private static void updateFileInfo()
    {
        // linkedItemList should have updated info so insert it into file without editing income.
        // convert into a string array then do a 1 to 1 swap with the values inside the .txt.
        String [] splitComponents = linkedItemList.toArray(new String[0]);
        String newFileInput = "";
        int newLine = 0;

        for (String s : splitComponents)
        {
            newLine++;
            newFileInput += s;

            if (newLine % 2 == 0)
                newFileInput += '\n';
            else
                newFileInput += " ";
        }

        try
        {
            FileWriter writer = new FileWriter(FileHandle.file, false);
            writer.write(newFileInput);
            writer.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
