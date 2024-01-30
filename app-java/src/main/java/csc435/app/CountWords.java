package csc435.app;

import java.io.*;
import java.util.*;

public class CountWords {
    public long dataset_size = 0;
    public double execution_time = 0.0;

    public void count_words(String input_dir, String output_dir) {
        try {
            File inputDirectory = new File(input_dir);
            File[] files = inputDirectory.listFiles();

            if (files == null) {
                System.err.println("No files found in the input directory.");
                System.exit(1);
            }

            File outputDirectory = new File(output_dir);
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                System.err.println("Failed to create the output directory.");
                System.exit(1);
            }

            List<Long> datasetSizes = new ArrayList<>();
            List<Long> countingTimes = new ArrayList<>();

            PrintWriter datasetSizesWriter = new PrintWriter(new FileWriter("datasetSizes.txt"));
            PrintWriter countingTimesWriter = new PrintWriter(new FileWriter("countingTimes.txt"));

            for (File file : files) {
                if (file.isFile()) {
                    long startTime = System.currentTimeMillis();
                    countWordsAndSave(file, output_dir, datasetSizes, countingTimes);
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    countingTimes.add(elapsedTime);
                    datasetSizes.add(file.length());

                    countingTimesWriter.println(elapsedTime);
                    datasetSizesWriter.println(file.length());
                }
            }

            datasetSizesWriter.close();
            countingTimesWriter.close();

            for (int i = 0; i < datasetSizes.size(); i++) {
                System.out.println("Dataset " + (i + 1) + " Size: " + datasetSizes.get(i) + " bytes");
                System.out.println("Counting Time for Dataset " + (i + 1) + ": " + countingTimes.get(i) + " milliseconds");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void countWordsAndSave(File file, String outputDir, List<Long> datasetSizes, List<Long> countingTimes) throws IOException {
        String fileName = file.getName();
        File outputFile = new File(outputDir, fileName);

        Map<String, Integer> wordCountMap = new HashMap<>();
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[\\s\\t\\n\\r]+");

                for (String word : words) {
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                }
            }

            writer = new BufferedWriter(new FileWriter(outputFile));

            for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }

            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("improper number of arguments");
            System.exit(1);
        }
        CountWords countWords = new CountWords();
        countWords.count_words(args[0], args[1]);
    }
}
