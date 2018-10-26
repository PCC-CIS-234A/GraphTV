package logic;

import data.WebData;

import java.util.ArrayList;

public class ShowType {
    private String m_Name;

    public ShowType(String name) {
        m_Name = name;
    }

    public static ArrayList<ShowType> fetchShowTypes() {
        return WebData.fetchShowTypes();
    }

    public String getName() {
        return m_Name;
    }
}
