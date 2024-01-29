import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class DatasetCleaner {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java DatasetCleaner <input_directory> <output_directory>");
            return;
        }

        String inputDirectory = args[0];
        String outputDirectory = args[1];

        try {
            collectDataAndClean(inputDirectory, outputDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  public static void collectDataAndClean(String inputDir, String outputDir) throws IOException {
        Files.createDirectories(Paths.get(outputDir));

        List<Long> datasetSizes = new ArrayList<>();
        List<Long> dataReadList = new ArrayList<>();
        List<Long> cleaningTimes = new ArrayList<>();

        Files.walk(Paths.get(inputDir))
             .filter(Files::isRegularFile)
             .forEach(filePath -> {
                 try {
                     long startTime = System.currentTimeMillis();
                     long dataRead = cleanAndSaveFile(filePath, inputDir, outputDir);
                     long endTime = System.currentTimeMillis();
                     long elapsedTime = endTime - startTime;

                     datasetSizes.add(getFileSizeInMiB(filePath)); 
                     dataReadList.add(dataRead); 
                     cleaningTimes.add(elapsedTime); 
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             });

        saveListToFile(datasetSizes, "datasetSizes.txt");
        saveListToFile(dataReadList, "dataReadList.txt");
        saveListToFile(cleaningTimes, "cleaningTimes.txt");
        System.out.println("Current Directory: " + System.getProperty("user.dir"));

    }
    public static void saveListToFile(List<Long> list, String fileName) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (Long item : list) {
                writer.println(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    

    public static long cleanAndSaveFile(Path filePath, String inputDir, String outputDir) throws IOException {
        String relativePath = inputDir.isEmpty() ? filePath.toString() : filePath.toString().replace(inputDir, "");
        Path outputPath = Paths.get(outputDir, relativePath);

        Files.createDirectories(outputPath.getParent());

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\r", "");

                line = line.replaceAll("[\\s\\t\\n\\r]+", " ");

                line = line.replaceAll("[^\\w\\s]", "");

                writer.write(line);
                writer.newLine();
            }
        }

        System.out.println("Cleaned: " + outputPath);
        return Files.size(filePath);
    }
    public static long getFileSizeInMiB(Path filePath) throws IOException {
        long fileSizeInBytes = Files.size(filePath);
        return fileSizeInBytes / (1024 * 1024);
    }
}
