package data;

import logic.Show;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    // Connection string for connecting to SQL Server at CISDBSS, using the IMDB database.
    // Requires jtds.XXX.jar to be included in the project with the correct dependency set.
    public static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://cisdbss.pcc.edu/IMDB";

    // Some SQL queries.
    private static final String FIND_SHOWS_QUERY =
            "SELECT TOP 50 tconst, primaryTitle, startYear, endYear, runtimeMinutes"
                    + " FROM title_basics"
                    + " WHERE titleType = 'tvSeries'"
                    + " AND primaryTitle COLLATE SQL_Latin1_General_CP1_CI_AS LIKE ?;";

    // The one and only connection object
    private static Connection m_Connection = null;
    private static PreparedStatement m_Statement;

    /**
     * Create a new connection object if there isn't one already.
     */
    private static void connect() {
        if (m_Connection != null)
            return;
        try {
            // Create a database connection with the given username and password.
            m_Connection = DriverManager.getConnection(CONNECTION_STRING, "275student", "275student");
        } catch (SQLException e) {
            System.err.println("Error! Couldn't connect to the database!");
        }
    }

    /**
     * Fetch a list of users with the given role.
     *
     * @param text The role for users that we want to fetch
     * @return The list of users matching that role.
     */
    public static ArrayList<Show> findShowsByTitle(String text) {
        ResultSet rs = null;
        ArrayList<Show> shows = new ArrayList<>();

        try {
            // Create a connection if there isn't one already
            connect();

            // Prepare a SQL statement
            m_Statement = m_Connection.prepareStatement(FIND_SHOWS_QUERY);

            // This one has a single parameter for the role, so we bind the value of role to the parameter
            m_Statement.setString(1, "%" + text + "%");

            // Execute the query returning a result set
            rs = m_Statement.executeQuery();

            // For each row in the result set, create a new User object with the specified values
            // and add it to the list of results.
            while (rs.next()) {
                shows.add(new Show(
                        rs.getString("tconst"),
                        rs.getString("primaryTitle"),
                        rs.getInt("startYear"),
                        rs.getInt("endYear"),
                        rs.getInt("runtimeMinutes")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error: Interrupted or couldn't connect to database.");
            m_Statement = null;
            return null;
        }
        // Return the list of results. Will be an empty list if there was an error.
        return shows;
    }

    public static void cancel() {
        if (m_Statement != null) {
            try {
                m_Statement.cancel();
                m_Statement = null;
                m_Connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
