import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class InventoryLoader {
    public static int countLines(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int lineCount = 0;
        while (reader.readLine() != null) {
            lineCount++;
        }
        reader.close();
        return lineCount;
    }
    //------------------------------------------------------------------------------
    public static void load(String[] types, String[] itemIds, String[] titles, String[] genres,
                            double[] ratings, boolean[] availabilities, String[] extra1, String[] extra2, File inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        for (int i = 0; i < types.length; i++) {
            String line = reader.readLine();
            String[] parts = line.split(",", -1); // -1 keeps trailing empty fields

            types[i] = parts[0].trim();
            itemIds[i] = parts[1].trim();
            titles[i] = parts[2].trim();
            genres[i] = parts[3].trim();
            ratings[i] = Double.parseDouble(parts[4].trim());
            availabilities[i] = Boolean.parseBoolean(parts[5].trim());
            extra1[i] = parts[6].trim();
            extra2[i] = parts.length > 7 ? parts[7].trim() : "";
        }
        reader.close();
    }
}
