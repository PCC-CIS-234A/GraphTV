package data;

import logic.Episode;
import logic.Show;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebData {
    private static final String FIND_SHOWS_BY_ID = "http://www.glassgirder.com/graphtv/find_shows_by_id.php?ids=";
    private static final String FETCH_EPISODES = "http://www.glassgirder.com/graphtv/fetch_episodes.php?id=";

    private static int stringToInt(Object val) {
        if (val == null)
            return 0;
        return Integer.parseInt((String) val);
    }

    private static float stringToFloat(Object val) {
        if (val == null)
            return 0.0f;
        return Float.parseFloat((String) val);
    }

    public static ArrayList<Show> findShowsByID(ArrayList<String> ids) {
        ArrayList<Show> shows = new ArrayList<>();
        if (ids.size() == 0)
            return shows;
        try {
            URL url = new URL(FIND_SHOWS_BY_ID + JSONValue.toJSONString(ids));
            InputStream stream = url.openStream();
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new InputStreamReader(stream));

            for (int i = 0; i < array.size(); i++) {
                JSONObject rs = (JSONObject) array.get(i);

                shows.add(new Show(
                        (String) rs.get("tconst"),
                        (String) rs.get("primaryTitle"),
                        stringToInt(rs.get("startYear")),
                        stringToInt(rs.get("endYear")),
                        stringToInt(rs.get("runtimeMinutes")),
                        stringToInt(rs.get("numEpisodes"))
                ));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return shows;
    }

    public static ArrayList<Episode> fetchEpisodes(String id) {
        ArrayList<Episode> episodes = new ArrayList<>();

        try {
            URL url = new URL(FETCH_EPISODES + id);
            InputStream stream = url.openStream();
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new InputStreamReader(stream));

            for (int i = 0; i < array.size(); i++) {
                JSONObject rs = (JSONObject) array.get(i);

                episodes.add(new Episode(
                        (String) rs.get("tconst"),
                        stringToInt(rs.get("seasonNumber")),
                        stringToInt(rs.get("episodeNumber")),
                        (String) rs.get("primaryTitle"),
                        stringToInt(rs.get("startYear")),
                        stringToFloat(rs.get("averageRating")),
                        stringToInt(rs.get("numVotes"))
                ));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return episodes;
    }
}