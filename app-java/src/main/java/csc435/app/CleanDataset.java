package csc435.app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CleanDataset {
    public long dataset_size = 0;
    public double execution_time = 0.0;

    public void clean_dataset(final String input_dir, final String output_dir) {
        try {
            Files.createDirectories(Paths.get(output_dir));

            final List<Long> datasetSizes = new ArrayList<>();
            final List<Long> dataReadList = new ArrayList<>();
            final List<Long> cleaningTimes = new ArrayList<>();

            Files.walk(Paths.get(input_dir))
                 .filter(new java.util.function.Predicate<Path>() {
                     @Override
                     public boolean test(Path path) {
                         return Files.isRegularFile(path);
                     }
                 })
                 .forEach(new java.util.function.Consumer<Path>() {
                     @Override
                     public void accept(Path filePath) {
                         try {
                             long startTime = System.currentTimeMillis();
                             long dataRead = cleanAndSaveFile(filePath, input_dir, output_dir);
                             long endTime = System.currentTimeMillis();
                             long elapsedTime = endTime - startTime;

                             datasetSizes.add(getFileSizeInMiB(filePath));
                             dataReadList.add(dataRead);
                             cleaningTimes.add(elapsedTime);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 });

            saveListToFile(datasetSizes, "datasetSizes.txt");
            saveListToFile(dataReadList, "dataReadList.txt");
            saveListToFile(cleaningTimes, "cleaningTimes.txt");

            System.out.println("Finished cleaning " + dataset_size + " MiB of data");
            System.out.println(" in " + execution_time + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("improper number of arguments");
            System.exit(1);
        }

        CleanDataset cleanDataset = new CleanDataset();

        cleanDataset.clean_dataset(args[0], args[1]);
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

    public long cleanAndSaveFile(Path filePath, String inputDir, String outputDir) throws IOException {
        String relativePath = inputDir.isEmpty() ? filePath.toString() : filePath.toString().replace(inputDir, "");
        Path outputPath = Paths.get(outputDir, relativePath);

        Files.createDirectories(outputPath.getParent());

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

            String line;
            long dataRead = 0;

            while ((line = reader.readLine()) != null) {
                line = line.replace("\r", "");

                line = line.replaceAll("[\\s\\t\\n\\r]+", " ");

                line = line.replaceAll("[^\\w\\s]", "");

                writer.write(line);
                writer.newLine();

                dataRead += line.getBytes(StandardCharsets.UTF_8).length;
            }

            dataset_size += getFileSizeInMiB(filePath);
            return dataRead;
        }
    }

    public long getFileSizeInMiB(Path filePath) throws IOException {
        long fileSizeInBytes = Files.size(filePath);
        return fileSizeInBytes / (1024 * 1024);
    }
}
