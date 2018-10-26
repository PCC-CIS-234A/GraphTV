package presentation;

import javax.swing.*;

public class TabbedPaneForm extends GUIForm {
    private JTabbedPane tabbedPane;
    private JPanel rootPanel;

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
