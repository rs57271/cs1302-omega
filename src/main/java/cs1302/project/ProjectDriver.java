package cs1302.project;

import javafx.application.Application;

public class ProjectDriver {
    /**
     * The main entry point of the application.
     * 
     * @param args the command-line arguments to the application.
     */
    public static void main(String[] args) {
        try {
            Application.launch(MainScreen.class, args);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.err.println("Likely due to X11 timeout. Logout and log back in...");
            System.exit(1);
        } // try
    } // main
}
