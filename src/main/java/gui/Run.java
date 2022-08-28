package gui;

/**
 * Application Main class (the class that starts first) cannot extend Application when compiling into jar.
 * more info in <a href="https://stackoverflow.com/questions/52569724/javafx-11-create-a-jar-file-with-gradle/52571719#52571719">stack overflow</a>.
 */
public class Run {
    /**
     * Starts the application
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
