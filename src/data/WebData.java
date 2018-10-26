package data;

import logic.Episode;
import logic.Genre;
import logic.Show;
import logic.ShowType;
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
    private static final String FETCH_GENRES = "http://www.glassgirder.com/graphtv/fetch_genres.php";
    private static final String FETCH_SHOW_TYPES = "http://www.glassgirder.com/graphtv/fetch_show_types.php";
    private static final String FIND_TOP_RATED_SHOWS = "http://www.glassgirder.com/graphtv/find_top_rated_shows.php";

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
                        trimString(rs.get("tconst")),
                        trimString(rs.get("primaryTitle")),
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
                        trimString(rs.get("tconst")),
                        stringToInt(rs.get("seasonNumber")),
                        stringToInt(rs.get("episodeNumber")),
                        trimString(rs.get("primaryTitle")),
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

    public static ArrayList<Genre> fetchGenres() {
        ArrayList<Genre> genres = new ArrayList<>();

        try {
            URL url = new URL(FETCH_GENRES);
            InputStream stream = url.openStream();
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new InputStreamReader(stream));

            for (int i = 0; i < array.size(); i++) {
                genres.add(new Genre(trimString(((JSONObject) array.get(i)).get("genre"))));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return genres;
    }

    public static ArrayList<ShowType> fetchShowTypes() {
        ArrayList<ShowType> showTypes = new ArrayList<>();

        try {
            URL url = new URL(FETCH_SHOW_TYPES);
            InputStream stream = url.openStream();
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new InputStreamReader(stream));

            for (int i = 0; i < array.size(); i++) {
                showTypes.add(new ShowType(
                        trimString(((JSONObject) array.get(i)).get("titleType")),
                        trimString(((JSONObject) array.get(i)).get("pretty"))
                ));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return showTypes;
    }

    private static String trimString(Object obj) {
        if (obj == null)
            return "";
        return ((String) obj).trim();
    }

    public static ArrayList<Show> findTopRatedShows(String showType, String genre, int minVotes) {
        ArrayList<Show> shows = new ArrayList<>();

        try {
            String urlString = FIND_TOP_RATED_SHOWS + "?minVotes=" + minVotes;
            if (showType != null || genre != null) {
                if (showType != null) {
                    urlString += "&type=" + showType;
                }
                if (genre != null) {
                    urlString += "&genre=" + genre;
                }
            }
            URL url = new URL(urlString);
            InputStream stream = url.openStream();
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new InputStreamReader(stream));

            for (int i = 0; i < array.size(); i++) {
                JSONObject show = (JSONObject) array.get(i);

                shows.add(new Show(
                        trimString(show.get("tconst")),
                        trimString(show.get("primaryTitle")),
                        trimString(show.get("titleType")),
                        trimString(show.get("parentTitle")),
                        stringToInt(show.get("startYear")),
                        stringToInt(show.get("runtimeMinutes")),
                        stringToFloat(show.get("averageRating")),
                        stringToInt(show.get("numVotes")),
                        trimString(show.get("genres"))
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
}