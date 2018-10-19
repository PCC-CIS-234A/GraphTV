package presentation.Search;

import logic.Searcher;
import logic.Show;
import presentation.GUIForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchForm extends GUIForm implements Searcher.ShowListener {
    private JTextField searchText;
    private JTable showTable;
    private JPanel rootPanel;
    private JButton seriesInfoButton;
    private DefaultTableModel m_SearchModel;
    private Searcher m_Searcher;
    private ArrayList<Show> m_CurrentShows = null;

    public SearchForm() {
        setupTable();
        setupSearchBox();
        setupSeriesInfoButton();
        m_Searcher = new Searcher();
        m_Searcher.addShowListener(this);
    }

    private void setupTable() {
        // Create a default table model with three columns named Email, Password and Role, and no table data.
        m_SearchModel = new DefaultTableModel(
                // Initial data (empty)
                new Object[][]{},
                // Initial columns
                new Object[] { "Title", "Start Year", "End Year", "Runtime" }
        ) {
            // Do not let the user edit values in the table.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Make the userTable use that model
        showTable.setModel(m_SearchModel);

        // Center values in the Start Year, End Year, and Runtime columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        showTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        showTable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );

        // Center column headers
        ((DefaultTableCellRenderer)showTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        showTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = showTable.getSelectedRow();
                if (row > -1) {
                    seriesInfoButton.setEnabled(true);
                } else {
                    seriesInfoButton.setEnabled(false);
                }
            }
        });
    }

    private void setupSearchBox() {
        searchText.getDocument().addDocumentListener(new DocumentListener() {
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

    private void setupSeriesInfoButton() {
        seriesInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = showTable.getSelectedRow();
                System.out.println("Row " + row + " selected.");
                System.out.println(m_CurrentShows.get(row).getID());
            }
        });
    }

    private void updateSearch() {
        System.out.println(searchText.getText());
        m_Searcher.search(searchText.getText());
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private String emptyForZero(int val) {
        return (val != 0) ? "" + val : "";
    }
    @Override
    public void showsArrived(ArrayList<Show> shows) {
        m_CurrentShows = shows;
        m_SearchModel.setRowCount(0);
        for (Show show: shows) {
            m_SearchModel.addRow(new Object[]{
                    show.getTitle(),
                    emptyForZero(show.getStartYear()),
                    emptyForZero(show.getEndYear()),
                    emptyForZero(show.getRuntimeMinutes())
            });
        }
    }
}
