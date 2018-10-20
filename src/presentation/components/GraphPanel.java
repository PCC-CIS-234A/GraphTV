package presentation.components;

import logic.Episode;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphPanel extends JPanel implements MouseInputListener {
    private ArrayList<Episode> m_Episodes = null;
    private String m_Title = "";
    private ArrayList<DataPoint> m_Points = null;
    private DataPoint m_SelectedPoint = null;

    private static final int LEFT_MARGIN = 40;
    private static final int TOP_MARGIN = 25;
    private static final int RIGHT_MARGIN = 10;
    private static final int BOTTOM_MARGIN = 40;
    private static final int LEADING = 5;
    private static final int RATING_TEXT_HEIGHT = 10;
    private static final int POPUP_TEXT_HEIGHT = 8;
    private static final int TITLE_HEIGHT = 20;
    private static final int POPUP_MARGIN = 5;
    private static final int POPUP_OFFSET = 10;
    private static final int ARROW_SIZE = 8;
    private static final int MIN_INFO_WIDTH = 120;
    private BasicStroke m_RegularStroke;
    private BasicStroke m_AxisStroke;
    private BasicStroke m_RegressionStroke;
    private BasicStroke m_RegressionBorderStroke;
    private static final int POPUP_FONT_SIZE = 10;
    private static final int REGULAR_FONT_SIZE = 12;
    private static final int TITLE_FONT_SIZE = 18;
    private Font m_PopupFont;
    private Font m_RegularFont;
    private Font m_TitleFont;
    private Color m_InfoColor;

    public GraphPanel() {
        super();
        m_AxisStroke = new BasicStroke(2);
        m_RegressionStroke = new BasicStroke(2);
        m_RegressionBorderStroke = new BasicStroke(4);
        m_RegularStroke = new BasicStroke(1);
        m_PopupFont = new Font("TimesRoman", Font.PLAIN, POPUP_FONT_SIZE);
        m_RegularFont = new Font("TimesRoman", Font.PLAIN, REGULAR_FONT_SIZE);
        m_TitleFont = new Font("TimesRoman", Font.PLAIN, TITLE_FONT_SIZE);
        m_InfoColor = new Color(255, 255, 240);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public void setEpisodes(ArrayList<Episode> episodes, String title) {
        m_Episodes = episodes;
        m_Title = title;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (m_Episodes == null)
            return;
        if (m_Episodes.size() == 0)
            return;
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        Episode firstEp = m_Episodes.get(0);
        float minRating = firstEp.getRating();
        float maxRating = firstEp.getRating();
        int maxSeason = 0;

        for (Episode episode: m_Episodes) {
            float rating = episode.getRating();

            if (rating > maxRating)
                maxRating = rating;
            if (rating > 0 && (minRating == 0 || rating < minRating))
                minRating = rating;
            if (episode.getSeasonNumber() > maxSeason) {
                maxSeason = episode.getSeasonNumber();
            }
        }

        minRating = (float)Math.floor(2 * minRating) / 2;
        maxRating = (float)Math.ceil(2 * maxRating) / 2;

        g2.setColor(Color.YELLOW);
        g2.setFont(m_TitleFont);
        int width = g2.getFontMetrics().stringWidth(m_Title);
        g2.drawString(m_Title, (panelWidth - width) / 2, panelHeight - TITLE_HEIGHT / 2);
        g2.setFont(m_RegularFont);
        drawAxes(g2, panelWidth - LEFT_MARGIN - RIGHT_MARGIN,
                panelHeight - TOP_MARGIN - BOTTOM_MARGIN, minRating, maxRating);
        plotPoints(panelWidth - LEFT_MARGIN - RIGHT_MARGIN,
                panelHeight - TOP_MARGIN - BOTTOM_MARGIN, minRating, maxRating);
        drawRegressionLines(g2, maxSeason);
        drawPoints(g2, maxSeason);

        drawSelectedInfo(g2, panelWidth, panelHeight);
    }

    private void drawAxes(Graphics2D g2, int width, int height, float minRating, float maxRating) {
        for (float rating = minRating; rating <= maxRating; rating += 0.5f) {
            int y = Math.round(TOP_MARGIN + height - height * (rating - minRating) / (maxRating - minRating));
            String ratingString = "" + rating;
            int ratingWidth = g2.getFontMetrics().stringWidth(ratingString);
            g2.drawString(ratingString, LEFT_MARGIN - 2 * LEADING - ratingWidth, y + RATING_TEXT_HEIGHT / 2);
            g2.drawLine(LEFT_MARGIN - LEADING, y, LEFT_MARGIN + width, y);
        }
        g2.setStroke(m_AxisStroke);
        g2.drawLine(LEFT_MARGIN, TOP_MARGIN - LEADING, LEFT_MARGIN, TOP_MARGIN + height);
        g2.drawLine(LEFT_MARGIN, TOP_MARGIN + height, LEFT_MARGIN + width, TOP_MARGIN + height);
        g2.setStroke(m_RegularStroke);
    }

    private void drawRegressionLines(Graphics2D g2, int maxSeason) {
        if (m_Points == null)
            return;
        HashMap<Integer, ArrayList<DataPoint>> map = DataPoint.splitBySeason(m_Points);
        for (Integer seasonNumber: map.keySet()) {
            ArrayList<DataPoint> seasonPoints = map.get(seasonNumber);
            Line2D.Float line = DataPoint.linearRegression(seasonPoints);
            g2.setStroke(m_RegressionBorderStroke);
            g2.setColor(Color.BLACK);
            g2.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
            g2.setStroke(m_RegressionStroke);
            g2.setColor(ColorChooser.chooseColor(seasonNumber, maxSeason));
            g2.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
        }
        g2.setStroke(m_RegularStroke);
    }

    private void plotPoints(int width, int height, float minRating, float maxRating) {
        int length = m_Episodes.size();

        if (m_Points == null)
            m_Points = new ArrayList<>();
        else
            m_Points.clear();
        for (int i = 0; i < m_Episodes.size(); i++) {
            Episode episode = m_Episodes.get(i);
            if (episode.getRating() > 0) {
                float x = LEFT_MARGIN + width * (i + 0.5f) / length;
                float y = TOP_MARGIN + height - height * (episode.getRating() - minRating) / (maxRating - minRating);
                DataPoint p = new DataPoint(Math.round(x), Math.round(y), episode);
                m_Points.add(p);
                // System.out.println(episode.getTitle() + "   " + episode.getSeasonNumber() + "   " + episode.getEpisodeNumber() + "  " + episode.getRating() + " " + episode.getNumVotes());
            }
        }
    }

    private void drawPoints(Graphics2D g2, int maxSeason) {
        for (DataPoint p: m_Points) {
            g2.setColor(Color.BLACK);
            g2.drawArc(p.getX() - 4, p.getY() - 4, 8, 8, 0, 360);
            g2.setColor(ColorChooser.chooseColor(p.getEpisode().getSeasonNumber(), maxSeason));
            g2.fillArc(p.getX() - 3, p.getY() - 3, 7, 7, 0, 360);
        }
    }

    private void drawSelectedInfo(Graphics2D g2, int panelWidth, int panelHeight) {
        if(m_SelectedPoint == null)
            return;

        Episode episode = m_SelectedPoint.getEpisode();
        g2.setFont(m_PopupFont);
        String title = episode.getTitle();
        int titleWidth = Math.max(MIN_INFO_WIDTH, g2.getFontMetrics().stringWidth(title));
        int width = 2 * POPUP_MARGIN + titleWidth;
        int height = 2 * POPUP_MARGIN + 3 * (POPUP_MARGIN + POPUP_TEXT_HEIGHT);
        int pointX = m_SelectedPoint.getX();
        int pointY = m_SelectedPoint.getY();
        int x, y;

        g2.setColor(Color.BLACK);

        if(pointX < panelWidth / 2) {
            x = pointX + POPUP_OFFSET + ARROW_SIZE;
        } else {
            x = pointX - POPUP_OFFSET - width;
        }

        if(pointY < panelHeight / 3) {
            y = pointY + POPUP_OFFSET + ARROW_SIZE;
        } else if (pointY > 2 * panelHeight / 3){
            y = pointY - POPUP_OFFSET - height;
        } else {
            y = pointY - height / 2;
        }

        g2.setColor(m_InfoColor);
        g2.fillRoundRect(x, y,  width,
                height, 2 * POPUP_MARGIN, 2 * POPUP_MARGIN);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x - 1, y - 1,  width + 1,
                height + 2, 2 * POPUP_MARGIN, 2 * POPUP_MARGIN);
        centerText(g2, title, x + width / 2, y + POPUP_MARGIN + POPUP_TEXT_HEIGHT);
        g2.drawLine(x + POPUP_MARGIN, y + 2 * POPUP_MARGIN + POPUP_TEXT_HEIGHT, x + POPUP_MARGIN + titleWidth, y + 2 * POPUP_MARGIN + POPUP_TEXT_HEIGHT);
        centerText(g2, "Season: " + episode.getSeasonNumber(), x + width / 4, y + POPUP_MARGIN + 2 * (POPUP_MARGIN + POPUP_TEXT_HEIGHT));
        centerText(g2, "Episode: " + episode.getEpisodeNumber(), x + width / 4, y + POPUP_MARGIN + 3 * (POPUP_MARGIN + POPUP_TEXT_HEIGHT));
        centerText(g2, "Rating: " + episode.getRating(), x + 3 * width / 4, y + POPUP_MARGIN + 2 * (POPUP_MARGIN + POPUP_TEXT_HEIGHT));
        centerText(g2, "# Votes: " + episode.getNumVotes(), x + 3 * width / 4, y + POPUP_MARGIN + 3 * (POPUP_MARGIN + POPUP_TEXT_HEIGHT));
    }

    private void centerText(Graphics2D g2, String text, int x, int y){
        int width = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, x - width / 2, y);
    }
    @Override
    public void mouseClicked(MouseEvent event) {
        if (m_SelectedPoint != null) {
            System.out.println("Clicked " + m_SelectedPoint.getEpisode().getID());
            try {
                Desktop.getDesktop().browse(
                        new URL("http://www.imdb.com/title/" + m_SelectedPoint.getEpisode().getID()).toURI()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // System.out.println(e.getX() + ", " + e.getY());
        if (m_Points != null && m_Points.size() > 0) {
            DataPoint point = DataPoint.findDataPoint(m_Points, e.getX(), e.getY());
            if(point != m_SelectedPoint) {
                m_SelectedPoint = point;
                repaint();
            }
        }
    }
}
