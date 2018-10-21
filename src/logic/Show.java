package logic;

public class Show {
    private String m_ID;
    private String m_Title;
    private int m_StartYear;
    private int m_EndYear;
    private int m_RuntimeMinutes;
    private int m_NumEpisodes;

    public Show(String id, String title, int start, int end, int minutes, int episodes) {
        m_ID = id;
        m_Title = title;
        m_StartYear = start;
        m_EndYear = end;
        m_RuntimeMinutes = minutes;
        m_NumEpisodes = episodes;
    }

    public String getID() {
        return m_ID;
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
}
