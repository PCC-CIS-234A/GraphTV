package logic;

import data.Database;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Searcher {
    private ArrayList<ShowListener> m_Listeners;
    private String m_Pending = null;
    private boolean m_Searching = false;
    private Timer m_SearchDelayTimer;

    public Searcher() {
        m_Listeners = new ArrayList<>();
        m_SearchDelayTimer = new Timer("Search Delay Timer");
    }

    public void search(String title) {
        m_Pending = title;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (title == m_Pending) {
                    ArrayList<Show> shows = Database.findShowsByTitle(title);
                    showsArrived(shows);
                }
            }
        };
        m_SearchDelayTimer.schedule(task, 500);
    }

    private void showsArrived(ArrayList<Show> shows) {
        for (ShowListener listener: m_Listeners) {
            listener.showsArrived(shows);
        }
    }

    public void addShowListener(ShowListener listener) {
        m_Listeners.add(listener);
    }

    public interface ShowListener {
        public void showsArrived(ArrayList<Show> shows);
    }
}
