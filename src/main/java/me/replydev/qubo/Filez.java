package me.replydev.qubo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.nio.file.StandardOpenOption;

public class Filez {

    private static Path quboOutputs = Paths.get("qubo_outputs");
    private static Path currentPath;

    
    public static void setCurrentPath(Path p){
        currentPath = p;
    }

    public static Path getCurrentPath() {
        return currentPath;
    }
    

    public static void init(){
        /**
         * crea la cartella qubo_outputs
         * se non esiste già
         */
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
