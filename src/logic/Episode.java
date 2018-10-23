package logic;

import data.Database;
import data.WebData;

import java.util.ArrayList;

public class Episode {
    private String m_ID;
    private int m_SeasonNumber;
    private int m_EpisodeNumber;
    private String m_Title;
    private int m_Year;
    private float m_Rating;
    private int m_NumVotes;

    public Episode(String id, int season, int episode, String title, int year, float rating, int numVotes) {
        m_ID = id;
        m_SeasonNumber = season;
        m_EpisodeNumber = episode;
        m_Title = title;
        m_Year = year;
        m_Rating = rating;
        m_NumVotes = numVotes;
    }

    public static ArrayList<Episode> fetchEpisodes(String id) {
        // return Database.fetchEpisodes(id);
        return WebData.fetchEpisodes(id);
    }

    public String getID() {
        return m_ID;
    }

    public int getSeasonNumber() {
        return m_SeasonNumber;
    }

    public int getEpisodeNumber() {
        return m_EpisodeNumber;
    }

    public String getTitle() {
        return m_Title;
    }

    public int getYear() {
        return m_Year;
    }

    public float getRating() {
        return m_Rating;
    }

    public int getNumVotes() {
        return m_NumVotes;
    }
}
