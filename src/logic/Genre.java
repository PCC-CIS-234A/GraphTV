package logic;

import data.WebData;

import java.util.ArrayList;

public class Genre {
    private String m_Name;

    public Genre(String name) {
        m_Name = name;
    }

    public static ArrayList<Genre> fetchGenres() {
        return WebData.fetchGenres();
    }

    public String getName() {
        return m_Name;
    }
}
