package data;

import logic.Show;
import logic.Title;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class TitleData {
    private static ArrayList<Title> m_Titles;

    public static void init() {
        m_Titles = loadTitles("data/title_titles.tsv.gz");
    }

    public static ArrayList<Title> loadTitles(String filename) {
        ArrayList<Title> titles = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
            String s;
            do {
                s = in.readLine();
                if (s != null) {
                    String[] parts = s.split("\t");
                    titles.add(new Title(parts[0], parts[1]));
                }
            } while (s != null);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Couldn't open title file!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Couldn't extract title file contents!");
            e.printStackTrace();
        }
        return titles;
    }

    public static ArrayList<Show> findShowsByTitle(String title, int maxShows) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<Show> results = new ArrayList<>();
        int hits = 0;
        String titleLower = title.toLowerCase();

        for (int i = 0; i < m_Titles.size() && hits < maxShows; i++) {
            if (m_Titles.get(i).getTitle().contains(titleLower)) {
                // System.out.println(m_Titles.get(i).getID() + ": " + m_Titles.get(i).getTitle());
                ids.add(m_Titles.get(i).getID());
                hits++;
            }
        }
        // return Database.findShowsByID(ids);
        return WebData.findShowsByID(ids);
    }
}
