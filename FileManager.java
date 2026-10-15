package Banking;
import java.io.*;

public class FileManager {

    public static void save(Object object, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            out.writeObject(object);
        } catch (IOException e) {
            System.out.println("Error saving " + fileName
                + ": " + e.getMessage());
        }
    }

    public static Object load(String fileName) {
        File file = new File(fileName);

        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading " + fileName
                + ": " + e.getMessage());
            return null;
        }
    }
}