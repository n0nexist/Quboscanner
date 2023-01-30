package me.replydev.qubo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.nio.file.StandardOpenOption;

public class Filez {

    private static Path quboOutputs = Paths.get("qubo_outputs");


    public static String parse(String f) {
        return f.replaceAll("[^a-zA-Z0-9]", "_");
    }

    public static void setQuboOutputs(Path quboOutputs) {
        Filez.quboOutputs = quboOutputs;
    }

    public static void setQuboOutputs(String quboOutputs) {
        Filez.quboOutputs = Paths.get(quboOutputs);
    }

    public static Path getCurrentPath() {
        return quboOutputs;
    }
    

    public static void init(String dirpath){
        /**
         * crea la cartella degli outputs
         * se non esiste già
         */
        setQuboOutputs(dirpath);
        if (!Files.exists(quboOutputs)) {
            try {
                Files.createDirectories(quboOutputs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Path create_file(String fileName) {
        /** 
         * crea il file desiderato
         */
        fileName = parse(fileName);
        Path newFile = quboOutputs.resolve(fileName);
        while (Files.exists(newFile)) {
            /* se esiste già,
             * creiamo il file con lo stesso nome ma con 
             * 3 caratteri random aggiunti
             */
            String randomChars = "";
            Random rand = new Random();
            for (int i = 0; i < 3; i++) {
                char c = (char) (rand.nextInt(26) + 'a');
                randomChars += c;
            }
            newFile = quboOutputs.resolve(fileName + randomChars);
        }
        try {
            Files.createFile(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    public static void writeToFile(Path newFile,String content){
        /**
         * scrivi dentro il file desiderato
         * (in append mode)
         */
        try {
            Files.write(newFile, (content+"\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
