import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandle
{
    public static File file;
    public static String fileName;

    public static void grabFile()
    {
        fileName = System.getProperty("user.dir") + "\\src\\expenses.txt";

        file = new File(fileName);
    }

    public static void newFile()
    {
        try
        {
            FileWriter createNew = new FileWriter(fileName);
            createNew.write("income 0\n");
            file = new File("expenses.txt");

        } catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

}
