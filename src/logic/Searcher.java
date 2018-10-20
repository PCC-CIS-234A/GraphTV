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
        m_Pending = title;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (title == m_Pending) {
                    ArrayList<Show> shows = Database.findShowsByTitle(title);
                    showsArrived(shows);
                }
            }
        }).start();
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
