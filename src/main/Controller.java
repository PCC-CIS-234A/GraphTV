package main;

import presentation.GUIForm;
import presentation.InfoGraph.InfoGraphForm;
import presentation.Search.SearchForm;

import javax.swing.*;

public class Controller {
    public static void start() {
        createGUI();
    }

    public static void createGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        // Create a JFrame to show our form in, and display the UsersTableGUI form.
        JFrame frame = new JFrame();
        // Makes the application close when the window goes away.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showForm(new SearchForm(), frame);
        // showForm(new InfoGraphForm("tt5370118", "KonoSuba - God's Blessing on This Wonderful World!"), frame);
    }

    public static void showForm(GUIForm form, JFrame frame) {
        JPanel root = form.getRootPanel();

        frame.getContentPane().removeAll();
        frame.getContentPane().add(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showSeriesInfo(String id, String title) {
        // System.out.println("Series info for " + id);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        InfoGraphForm form = new InfoGraphForm(id, title);
        showForm(form, frame);
    }
}
