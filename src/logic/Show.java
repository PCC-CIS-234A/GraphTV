package logic;

import data.WebData;

import java.util.ArrayList;

public class Show {
    private String m_ID;
    private String m_Title;
    private int m_StartYear;
    private int m_EndYear;
    private int m_RuntimeMinutes;
    private int m_NumEpisodes;
    private float m_Rating;
    private int m_Votes;
    private String m_Type;
    private String m_ParentTitle;
    private String m_ParentID;
    private String m_Genres;

    public Show(String id, String title, int start, int end, int minutes, String genres, int episodes) {
        m_ID = id;
        m_Title = title;
        m_StartYear = start;
        m_EndYear = end;
        m_RuntimeMinutes = minutes;
        m_Genres = genres;
        m_NumEpisodes = episodes;
    }

    public Show(String id, String title, String type, String parentTitle, int start, int minutes, float rating, int numVotes, String genres, int numEpisodes, String parentID) {
        m_ID = id;
        m_Title = title;
        m_Type = type;
        m_ParentTitle = parentTitle;
        m_StartYear = start;
        m_RuntimeMinutes = minutes;
        m_Rating = rating;
        m_Votes = numVotes;
        m_Genres = genres;
        m_NumEpisodes = numEpisodes;
        m_ParentID = parentID;
    }

    public static ArrayList<Show> findTopRatedShows(String showType, String genre, int minVotes) {
        return WebData.findTopRatedShows(showType, genre, minVotes);
    }

    public String getID() {
        return m_ID;
    }

    public String getParentID() {
        return m_ParentID;
    }

    public String getTitle() {
        return m_Title;
    }

    public int getStartYear() {
        return m_StartYear;
    }

    public int getEndYear() {
        return m_EndYear;
    }

    public int getRuntimeMinutes() {
        return m_RuntimeMinutes;
    }

    public int getNumEpisodes() {
        return m_NumEpisodes;
    }

    public float getRating() {
        return m_Rating;
    }

    public int getVotes() {
        return m_Votes;
    }

    public String getType() {
        return m_Type;
    }

    public String getParentTitle() {
        return m_ParentTitle;
    }

    public String getGenres() {
        return m_Genres;
    }
}
