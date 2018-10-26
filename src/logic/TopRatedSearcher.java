package logic;

import data.TitleData;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TopRatedSearcher {
    private ArrayList<ShowListener> m_Listeners;
    private String m_PendingType = null;
    private String m_PendingGenre = null;
    private int m_PendingMinVotes = 0;
    private boolean m_Searching = false;
    private Timer m_SearchDelayTimer;
    private static final long DELAY = 100;
    private static final int MAX_SHOWS = 200;

    public TopRatedSearcher() {
        m_Listeners = new ArrayList<>();
        m_SearchDelayTimer = new Timer("Search Delay Timer");
        TitleData.init();
    }

    public void search(String type, String genre, int minVotes) {
        m_PendingType = type;
        m_PendingGenre = genre;
        m_PendingMinVotes = minVotes;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (type == m_PendingType && genre == m_PendingGenre && minVotes == m_PendingMinVotes) {
                    ArrayList<Show> shows = Show.findTopRatedShows(type, genre, minVotes);
                    showsArrived(shows);
                }
            }
        };
        m_SearchDelayTimer.schedule(task, DELAY);
    }

    private void showsArrived(ArrayList<Show> shows) {
        for (ShowListener listener: m_Listeners) {
            listener.showsArrived(shows);
        }
    }

    public void addShowListener(ShowListener listener) {
        m_Listeners.add(listener);
    }
}
