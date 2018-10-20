package presentation.InfoGraph;

import logic.Episode;
import presentation.GUIForm;
import presentation.components.GraphPanel;

import javax.swing.*;

public class InfoGraphForm extends GUIForm {
    private JPanel rootPanel;
    private GraphPanel graphPanel;

    public InfoGraphForm(String id, String title) {
        System.out.println("Showing graph for " + id);
        graphPanel.setEpisodes(Episode.fetchEpisodes(id), title);
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }
}
