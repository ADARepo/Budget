import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class WindowDisplay
{

    private static JFrame frame;

    // file info in a single string, linked list for item management, doubles for holding displayed text.
    private static String fileText;
    private static LinkedList<String> linkedItemList;
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

    // edit income edits income inside of .txt and displays new income. edit expenses for editing expense info.
    private static JButton editIncome;
    private static JButton editExpenses;

    // creates JFrame and calls other methods for creating the frame UI.
    public static void initWindow()
    {
        frame = new JFrame("Budget");
        frame.setPreferredSize(new Dimension(280, 200));
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
    public static void buttonCreate() {
        editIncome = new JButton("Edit Income");
        editExpenses = new JButton("Edit Expenses");

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

        editExpenses.addActionListener(e -> {
            showExpenseWindow();
        });
    }

    public static void showExpenseWindow()
    {
        JFrame expenseFrame = new JFrame();

        JDialog expenseDialog = new JDialog(expenseFrame, "Edit Expenses", true);

        int numExpenses = (linkedItemList.size() - 2) / 2;

        expenseDialog.setLayout(new GridLayout(numExpenses, 2));


        expenseDialog.setModal(true);
        expenseDialog.setAlwaysOnTop(true);
        expenseDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        expenseDialog.setLocationRelativeTo(null);
        expenseDialog.setVisible(true);
    }

    public static void panelCreate()
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

    public static void labelCreate()
    {
        incomeText = new JLabel("Monthly Gross Income");
        expensesText = new JLabel("Monthly Expenses");
        grossText = new JLabel("Net Income (monthly)");
    }

    public static void textBoxCreate()
    {
        showIncome = new JTextArea();
        showExpenses = new JTextArea();
        showGross = new JTextArea();

        // grabbing income number from .txt file imported at beginning.
        setTextFields();

        showIncome.setEditable(false);
        showExpenses.setEditable(false);
        showGross.setEditable(false);
    }

    public static void setTextFields()
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

    public static void addComponents()
    {
        displayHolder[0][0].add(incomeText);
        displayHolder[0][1].add(showIncome);
        displayHolder[1][0].add(expensesText);
        displayHolder[1][1].add(showExpenses);
        displayHolder[2][0].add(grossText);
        displayHolder[2][1].add(showGross);
        displayHolder[3][0].add(editIncome);
        displayHolder[3][1].add(editExpenses);
    }

    public static void updateIncome()
    {
        linkedItemList.remove(1);
        linkedItemList.add(1, Double.toString(income));
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

    public static void grabText()
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
}
