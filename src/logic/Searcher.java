package logic;

import data.Database;

import java.util.ArrayList;

public class Searcher {
    private ArrayList<ShowListener> m_Listeners;
    private String m_Pending = null;
    private boolean m_Searching = false;

    public Searcher() {
        m_Listeners = new ArrayList<>();
    }

    public void search(String title) {
        if (m_Searching) {
            m_Pending = title;
            return;
        }
        m_Searching = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Show> shows = Database.findShowsByTitle(title);
                showsArrived(shows);
            }
        }).start();
    }

    private void showsArrived(ArrayList<Show> shows) {
        for (ShowListener listener: m_Listeners) {
            listener.showsArrived(shows);
        }
        m_Searching = false;
        if (m_Pending != null)
            search(m_Pending);
        m_Pending = null;
    }

    public void addShowListener(ShowListener listener) {
        m_Listeners.add(listener);
    }

    public interface ShowListener {
        public void showsArrived(ArrayList<Show> shows);
    }
}
