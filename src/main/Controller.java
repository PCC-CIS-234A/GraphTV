package main;

import presentation.GUIForm;
import presentation.Search.SearchForm;

import javax.swing.*;

public class Controller {
    private static JFrame m_Frame;

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
        m_Frame = new JFrame();
        // Makes the application close when the window goes away.
        m_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showForm(new SearchForm());
    }

    public static void showForm(GUIForm form) {
        JPanel root = form.getRootPanel();

        m_Frame.getContentPane().removeAll();
        m_Frame.getContentPane().add(root);
        m_Frame.pack();
        m_Frame.setLocationRelativeTo(null);
        m_Frame.setVisible(true);
    }
}
