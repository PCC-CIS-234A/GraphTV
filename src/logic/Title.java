package logic;

public class Title {
    private String m_ID;
    private String m_Title;

    public Title(String id, String title) {
        m_ID = id;
        m_Title = title;
    }

    public String getID() {
        return m_ID;
    }

    public String getTitle() {
        return m_Title;
    }
}
