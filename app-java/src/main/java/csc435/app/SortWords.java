package csc435.app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class SortWords {
    public long num_words = 0;
    public double execution_time = 0.0;

    public void sort_words(final String input_dir, final String output_dir) {
        try {
            File inputDirFile = new File(input_dir);
            File[] files = inputDirFile.listFiles();

            if (files == null) {
                System.err.println("Input directory does not exist or is empty.");
                return;
            }

            Files.createDirectories(Paths.get(output_dir));

            List<Long> totalWordsList = new ArrayList<>();
            List<Long> sortingTimes = new ArrayList<>();

            for (File file : files) {
                if (file.isFile()) {
                    long startTime = System.currentTimeMillis();
                    long totalWords = sortWordsAndSave(file.toPath(), input_dir, output_dir);
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    totalWordsList.add(totalWords);
                    sortingTimes.add(elapsedTime);
                }
            }

            saveListToFile(totalWordsList, "totalWordsList.txt");
            saveListToFile(sortingTimes, "sortingTimes.txt");

            System.out.println("Finished sorting " + num_words + " words");
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

        SortWords sortWords = new SortWords();

        sortWords.sort_words(args[0], args[1]);
    }

    public static void saveListToFile(List<Long> list, String fileName) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName);
            for (Long item : list) {
                writer.println(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public long sortWordsAndSave(Path filePath, String inputDir, String outputDir) throws IOException {
        String relativePath = inputDir.isEmpty() ? filePath.toString() : filePath.toString().replace(inputDir, "");
        Path outputPath = Paths.get(outputDir, relativePath);

        Files.createDirectories(outputPath.getParent());

        List<WordFrequency> wordList = new ArrayList<>();
        long totalWords = 0;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String word = parts[0];
                    int frequency = Integer.parseInt(parts[1]);
                    wordList.add(new WordFrequency(word, frequency));
                    totalWords += frequency;
                }
            }
            Collections.sort(wordList, Collections.reverseOrder());

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath.toFile()), StandardCharsets.UTF_8));
            for (WordFrequency wf : wordList) {
                writer.write(wf.getWord() + " " + wf.getFrequency());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Sorted: " + outputPath);
        return totalWords; 
    }
    static class WordFrequency implements Comparable<WordFrequency> {
        private final String word;
        private final int frequency;

        public WordFrequency(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }

        public String getWord() {
            return word;
        }

        public int getFrequency() {
            return frequency;
        }

        @Override
        public int compareTo(WordFrequency other) {
            return Integer.compare(other.frequency, this.frequency);
        }
    }
}
