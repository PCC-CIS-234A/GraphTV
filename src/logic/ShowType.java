package logic;

import data.WebData;

import java.util.ArrayList;

public class ShowType {
    private String m_TitleType;
    private String m_Pretty;

    public ShowType(String titleType, String pretty) {
        this.m_TitleType = titleType;
        this.m_Pretty = pretty;
    }

    public static ArrayList<ShowType> fetchShowTypes() {
        return WebData.fetchShowTypes();
    }

    public String getTitleType() {
        return m_TitleType;
    }

    public String getPretty() {
        return m_Pretty;
    }
}
