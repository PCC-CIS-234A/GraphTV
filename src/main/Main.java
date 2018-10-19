package main;

import javax.swing.*;

/**
 * Main entry point for the program.
 * <p>
 * Kick off the application by calling the start method of the Controller class.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Controller.start();
            }
        });
    }
}
