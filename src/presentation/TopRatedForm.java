package presentation;

import logic.*;
import main.Controller;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class TopRatedForm extends GUIForm implements ShowListener {
    private JPanel rootPanel;
    private JTable showTable;
    private JComboBox typeComboBox;
    private JComboBox genreComboBox;
    private JTextField minVotesField;

    private static final String ALL_GENRES = "-- All Genres --";
    private static final String ALL_TYPES = "-- All Types --";
    private ArrayList<Genre> m_Genres;
    private ArrayList<ShowType> m_ShowTypes;
    private String m_CurrentGenre = null;
    private String m_CurrentType = null;
    private DefaultTableModel m_ShowTableModel;
    private int m_MinVotes = 50000;
    private TopRatedSearcher m_Searcher;

    public TopRatedForm() {
        setupTypeCombo();
        setupGenreCombo();
        setupMinVotesField();
        // setupTable();
        m_Searcher = new TopRatedSearcher();
        m_Searcher.addShowListener(this);
        showTable();
    }

    private void setupGenreCombo() {
        m_Genres = Genre.fetchGenres();

        genreComboBox.addItem(ALL_GENRES);
        for (Genre genre: m_Genres)
            genreComboBox.addItem(genre.getName());

        genreComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String) e.getItem();

                    if (item == ALL_GENRES)
                        m_CurrentGenre = null;
                    else
                        m_CurrentGenre = item;
                    showTable();
                }
            }
        });
    }

    private void setupTypeCombo() {
        m_ShowTypes = ShowType.fetchShowTypes();

        typeComboBox.addItem(ALL_TYPES);
        for (ShowType showType: m_ShowTypes)
            typeComboBox.addItem(showType.getName());

        typeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String) e.getItem();

                    if (item == ALL_TYPES)
                        m_CurrentType = null;
                    else
                        m_CurrentType = item;
                    showTable();
                }
            }
        });
    }

    private void setupMinVotesField() {
        minVotesField.setText("" + m_MinVotes);
        minVotesField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearch();
            }
        });
    }

    /*
    private void setupSeriesInfoButton() {
        seriesInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = showTable.getSelectedRow();
                Controller.showSeriesInfo(m_CurrentShows.get(row).getID(), m_CurrentShows.get(row).getTitle());
            }
        });
    }
    */

    private void updateSearch() {
        try {
            // System.out.println("New text " + minVotesField.getText());
            int minVotes = Integer.parseInt(minVotesField.getText());
            if (minVotes < 1000) {
                throw(new NumberFormatException("minVotes must be 1000 or more."));
            }
            minVotesField.setForeground(Color.BLACK);
            m_MinVotes = minVotes;
            showTable();
        } catch (NumberFormatException e) {
            minVotesField.setForeground(Color.RED);
        }
    }


    private String emptyForZero(int val) {
        return (val != 0) ? "" + val : "";
    }
    private String emptyForZero(float val) {
        return (val != 0.0f) ? "" + val : "";
    }

    private void showTable() {
        m_Searcher.search(m_CurrentType, m_CurrentGenre, m_MinVotes);
    }

    @Override
    public void showsArrived(ArrayList<Show> shows) {
        setupTable();
        m_ShowTableModel.setRowCount(0);
        boolean hasParentTitle = false;
        for (Show show: shows) {
            if (show.getParentTitle() != "")
                hasParentTitle = true;
            m_ShowTableModel.addRow(new Object[]{
                    show.getTitle(),
                    show.getType(),
                    show.getParentTitle(),
                    emptyForZero(show.getStartYear()),
                    emptyForZero(show.getRuntimeMinutes()),
                    emptyForZero(show.getRating()),
                    emptyForZero(show.getVotes()),
                    show.getGenres()
            });
        }
        if (!hasParentTitle) {
            showTable.getColumnModel().getColumn(2).setMinWidth(0);
            showTable.getColumnModel().getColumn(2).setMaxWidth(0);
        }
    }


    private void setupTable() {
        // Create a default table model with three columns named Email, Password and Role, and no table data.
        m_ShowTableModel = new DefaultTableModel(
                // Initial data (empty)
                new Object[][]{},
                // Initial columns
                new Object[] { "Show Title", "Type", "Series Title", "Start Year", "Runtime", "Rating", "Votes", "Genres" }
        ) {
            // Do not let the user edit values in the table.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Make the userTable use that model
        showTable.setModel(m_ShowTableModel);

        // Center values in the Start Year, End Year, and Runtime columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        showTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(4).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(5).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(6).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(7).setCellRenderer( centerRenderer );

        // Center column headers
        ((DefaultTableCellRenderer)showTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Adjust column widths
        showTable.getColumnModel().getColumn(0).setMinWidth(250);
        showTable.getColumnModel().getColumn(1).setMinWidth(80);
        showTable.getColumnModel().getColumn(2).setMinWidth(250);
        showTable.getColumnModel().getColumn(2).setMaxWidth(250);
        showTable.getColumnModel().getColumn(7).setMinWidth(180);

        showTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                /*
                int row = showTable.getSelectedRow();
                if (row > -1) {
                    if(m_CurrentShows.get(row).getNumEpisodes() > 0)
                        seriesInfoButton.setEnabled(true);
                    else
                        seriesInfoButton.setEnabled(false);
                } else {
                    seriesInfoButton.setEnabled(false);
                }
                */
            }
        });
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }
}
