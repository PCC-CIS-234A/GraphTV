package data;

import logic.Episode;
import logic.Show;

import java.sql.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Database {
    // Connection string for connecting to SQL Server at CISDBSS, using the IMDB database.
    // Requires jtds.XXX.jar to be included in the project with the correct dependency set.
    /*
    private static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://cisdbss.pcc.edu/IMDB";
    private static final String USERNAME = "275student";
    private static final String PASSWORD = "275student";
    */
    private static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://192.168.2.33/NEW_IMDB";
    private static final String USERNAME = "IMDB";
    private static final String PASSWORD = "IMDB001!";

    // Some SQL queries.
    private static final String FIND_SHOWS_BY_ID_QUERY_FRONT =
            "SELECT tconst, primaryTitle, startYear, endYear, runtimeMinutes,"
                    + " (SELECT COUNT(*) FROM title_episode WHERE parentTconst = title_basics.tconst) AS numEpisodes"
                    + " FROM title_basics"
                    + " WHERE tconst IN (";
    private static final String FIND_SHOWS_BY_ID_QUERY_BACK =
            ") ORDER BY startYear DESC;";

    private static final String FETCH_EPISODES_QUERY =
            "SELECT	title_episode.tconst, seasonNumber, episodeNumber, primaryTitle, startYear, averageRating, numVotes"
                    + " FROM		title_episode"
                    + " JOIN		title_basics ON title_episode.tconst = title_basics.tconst"
                    + " LEFT JOIN	title_ratings ON title_episode.tconst = title_ratings.tconst"
                    + " WHERE		parentTconst = ?"
                    + " ORDER BY	seasonNumber, episodeNumber;";

    // The one and only connection object
    private static Connection m_Connection = null;
    private static PreparedStatement m_Statement;
    private static Timer m_ConnectionCloseTimer = new Timer("DB Connection Timer");
    private static TimerTask m_ConnectionCloseTask = null;
    private static final long CONNECTION_KEEP_ALIVE = 120000L;

    private static void resetConnectionCloseTimer() {
        if (m_ConnectionCloseTask != null) {
            m_ConnectionCloseTask.cancel();
            m_ConnectionCloseTimer.purge();
        }
        m_ConnectionCloseTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (m_Connection != null) {
                        m_ConnectionCloseTask = null;
                        m_Connection.close();
                        m_Connection = null;
                        // System.out.println("Closing idle connection.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        m_ConnectionCloseTimer.schedule(m_ConnectionCloseTask, CONNECTION_KEEP_ALIVE);
    }

    /**
     * Create a new connection object if there isn't one already.
     */
    private static void connect() {
        resetConnectionCloseTimer();
        if (m_Connection != null)
            return;
        try {
            // Create a database connection with the given username and password.
            // System.out.println("Opening database connection.");
            m_Connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error! Couldn't connect to the database!");
        }
    }

    /**
     * Fetch a list of shows given an array of IDs.
     *
     * @param ids The ids to search for
     * @return The list of shows with that text in their primaryTitle.
     */
    public static ArrayList<Show> findShowsByID(ArrayList<String> ids) {
        ResultSet rs = null;
        ArrayList<Show> shows = new ArrayList<>();

        if (ids.size() == 0)
            return shows;
        try {
            // Create a connection if there isn't one already
            connect();

            /* Sadly, this doesn't work in jtds-1.3.1.
            "SELECT ... FROM ... WHERE id IN ?"
            m_Statement = m_Connection.prepareStatement(FIND_SHOWS_BY_ID_QUERY);
            Object[] idArray = new Object[ids.size()];
            idArray = ids.toArray(idArray);
            Array array = m_Connection.createArrayOf("NCHAR", idArray);
            m_Statement.setArray(1, array);

            Fortunately, this is not being passed user input directly, so the following is safe:
            */
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < ids.size(); i++) {
                if (i != 0)
                    query.append(",");
                query.append("'" + ids.get(i) + "'");
            }

            // Execute the query returning a result set
            rs = m_Connection.createStatement().executeQuery(FIND_SHOWS_BY_ID_QUERY_FRONT + query.toString() + FIND_SHOWS_BY_ID_QUERY_BACK);

            // For each row in the result set, create a new Show object with the specified values
            // and add it to the list of results.
            while (rs.next()) {
                shows.add(new Show(
                        rs.getString("tconst"),
                        rs.getString("primaryTitle"),
                        rs.getInt("startYear"),
                        rs.getInt("endYear"),
                        rs.getInt("runtimeMinutes"),
                        rs.getInt("numEpisodes")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error: Interrupted or couldn't connect to database.");
            e.printStackTrace();
            m_Statement = null;
            return null;
        }
        // Return the list of results. Will be an empty list if there was an error.
        return shows;
    }

    /**
     * Fetch the episodes for a given series, along with their ratings.
     *
     * @param id The title id for the parent series.
     * @return The list of episodes for that series.
     */
    public static ArrayList<Episode> fetchEpisodes(String id) {
        ResultSet rs = null;
        ArrayList<Episode> episodes = new ArrayList<>();

        try {
            // Create a connection if there isn't one already
            connect();

            // Prepare a SQL statement
            m_Statement = m_Connection.prepareStatement(FETCH_EPISODES_QUERY);

            // This one has a single parameter for the role, so we bind the value of role to the parameter
            m_Statement.setString(1, id);

            // Execute the query returning a result set
            rs = m_Statement.executeQuery();

            // For each row in the result set, create a new User object with the specified values
            // and add it to the list of results.
            while (rs.next()) {
                // matches public Episode(String id, int season, int episode, String title, int year, float rating, int numVotes);
                episodes.add(new Episode(
                        rs.getString("tconst"),
                        rs.getInt("seasonNumber"),
                        rs.getInt("episodeNumber"),
                        rs.getString("primaryTitle"),
                        rs.getInt("startYear"),
                        rs.getFloat("averageRating"),
                        rs.getInt("numVotes")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error: Interrupted or couldn't connect to database.");
            m_Statement = null;
            return null;
        }
        // Return the list of results. Will be an empty list if there was an error.
        return episodes;
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
