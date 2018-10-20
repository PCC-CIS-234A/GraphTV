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

    private class Sizes {
        float leftMargin;
        float topMargin;
        float rightMargin;
        float bottomMargin;
        float leading;
        float ratingTextHeight;
        float popupTextHeight;
        float titleHeight;
        float popupMargin;
        float popupOffset;
        float arrowSize;
        float minInfoWidth;
        float pointRadius;
        float pointSize;
        float innerPointRadius;
        float innerPointSize;
        float popupFontSize;
        float regularFontSize;
        float titleFontSize;
        BasicStroke regularStroke;
        BasicStroke axisStroke;
        BasicStroke regressionStroke;
        BasicStroke regressionBorderStroke;
        Font popupFont;
        Font regularFont;
        Font titleFont;
    }

    private Sizes m_Sizes;
    Color m_InfoColor;

    public GraphPanel() {
        super();
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

        initSizes(panelWidth, panelHeight);
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
        g2.setFont(m_Sizes.titleFont);
        int width = g2.getFontMetrics().stringWidth(m_Title);
        g2.drawString(m_Title, (panelWidth - width) / 2, panelHeight - m_Sizes.titleHeight / 2);
        g2.setFont(m_Sizes.regularFont);
        drawAxes(g2, Math.round(panelWidth - m_Sizes.leftMargin - m_Sizes.rightMargin),
                Math.round(panelHeight - m_Sizes.topMargin - m_Sizes.bottomMargin), minRating, maxRating);
        plotPoints(Math.round(panelWidth - m_Sizes.leftMargin - m_Sizes.rightMargin),
                Math.round(panelHeight - m_Sizes.topMargin - m_Sizes.bottomMargin), minRating, maxRating);
        drawRegressionLines(g2, maxSeason);
        drawPoints(g2, maxSeason);

        drawSelectedInfo(g2, panelWidth, panelHeight);
    }

    private void initSizes(int panelWidth, int panelHeight) {
        float base = (float) Math.sqrt(panelWidth * panelHeight) / 500.0f;
        m_Sizes = new Sizes();

        m_Sizes.leftMargin = 40 * base;
        m_Sizes.topMargin = 25 * base;
        m_Sizes.rightMargin = 10 * base;
        m_Sizes.bottomMargin = 40 * base;
        m_Sizes.leading = 5 * base;
        m_Sizes.ratingTextHeight = 10 * base;
        m_Sizes.popupTextHeight = 8 * base;
        m_Sizes.titleHeight = 20 * base;
        m_Sizes.popupMargin = 5 * base;
        m_Sizes.popupOffset = 10 * base;
        m_Sizes.arrowSize = 8 * base;
        m_Sizes.minInfoWidth = 120 * base;
        m_Sizes.popupFontSize = 10 * base;
        m_Sizes.regularFontSize = 12 * base;
        m_Sizes.titleFontSize = 18 * base;
        m_Sizes.pointSize = 8 * base;
        m_Sizes.pointRadius = m_Sizes.pointSize / 2;
        m_Sizes.innerPointSize = m_Sizes.pointSize * .75f;
        m_Sizes.innerPointRadius = m_Sizes.innerPointSize / 2;

        m_Sizes.axisStroke = new BasicStroke(2 * base);
        m_Sizes.regressionStroke = new BasicStroke(2 * base);
        m_Sizes.regressionBorderStroke = new BasicStroke(4 * base);
        m_Sizes.regularStroke = new BasicStroke(1 * base);

        m_Sizes.popupFont = new Font("TimesRoman", Font.PLAIN, Math.round(m_Sizes.popupFontSize));
        m_Sizes.regularFont = new Font("TimesRoman", Font.PLAIN, Math.round(m_Sizes.regularFontSize));
        m_Sizes.titleFont = new Font("TimesRoman", Font.PLAIN, Math.round(m_Sizes.titleFontSize));
    }

    private void drawAxes(Graphics2D g2, int width, int height, float minRating, float maxRating) {
        g2.setStroke(m_Sizes.axisStroke);
        g2.drawLine(Math.round(m_Sizes.leftMargin), Math.round(m_Sizes.topMargin - m_Sizes.leading), Math.round(m_Sizes.leftMargin), Math.round(m_Sizes.topMargin + height));
        g2.drawLine(Math.round(m_Sizes.leftMargin), Math.round(m_Sizes.topMargin + height), Math.round(m_Sizes.leftMargin + width), Math.round(m_Sizes.topMargin + height));
        g2.setStroke(m_Sizes.regularStroke);
        for (float rating = minRating; rating <= maxRating; rating += 0.5f) {
            int y = Math.round(m_Sizes.topMargin + height - height * (rating - minRating) / (maxRating - minRating));
            String ratingString = "" + rating;
            int ratingWidth = g2.getFontMetrics().stringWidth(ratingString);
            g2.drawString(ratingString, m_Sizes.leftMargin - 2 * m_Sizes.leading - ratingWidth, y + m_Sizes.ratingTextHeight / 2);
            g2.drawLine(Math.round(m_Sizes.leftMargin - m_Sizes.leading), y, Math.round(m_Sizes.leftMargin + width), y);
        }
    }

    private void drawRegressionLines(Graphics2D g2, int maxSeason) {
        if (m_Points == null)
            return;
        HashMap<Integer, ArrayList<DataPoint>> map = DataPoint.splitBySeason(m_Points);
        for (Integer seasonNumber: map.keySet()) {
            ArrayList<DataPoint> seasonPoints = map.get(seasonNumber);
            Line2D.Float line = DataPoint.linearRegression(seasonPoints);
            g2.setStroke(m_Sizes.regressionBorderStroke);
            g2.setColor(Color.BLACK);
            g2.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
            g2.setStroke(m_Sizes.regressionStroke);
            g2.setColor(ColorChooser.chooseColor(seasonNumber, maxSeason));
            g2.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
        }
        g2.setStroke(m_Sizes.regularStroke);
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
                float x = m_Sizes.leftMargin + width * (i + 0.5f) / length;
                float y = m_Sizes.topMargin + height - height * (episode.getRating() - minRating) / (maxRating - minRating);
                DataPoint p = new DataPoint(Math.round(x), Math.round(y), episode);
                m_Points.add(p);
                // System.out.println(episode.getTitle() + "   " + episode.getSeasonNumber() + "   " + episode.getEpisodeNumber() + "  " + episode.getRating() + " " + episode.getNumVotes());
            }
        }
    }

    private void drawPoints(Graphics2D g2, int maxSeason) {
        for (DataPoint p: m_Points) {
            g2.setColor(Color.BLACK);
            int left = Math.round(p.getX() - m_Sizes.pointRadius);
            int top = Math.round(p.getY() - m_Sizes.pointRadius);
            int innerLeft = Math.round(left + m_Sizes.pointRadius - m_Sizes.innerPointRadius);
            int innerTop = Math.round(top + m_Sizes.pointRadius - m_Sizes.innerPointRadius);

            g2.fillArc(left, top, Math.round(m_Sizes.pointSize), Math.round(m_Sizes.pointSize), 0, 360);
            g2.setColor(ColorChooser.chooseColor(p.getEpisode().getSeasonNumber(), maxSeason));
            g2.fillArc(innerLeft, innerTop,
                    Math.round(m_Sizes.innerPointSize), Math.round(m_Sizes.innerPointSize), 0, 360);
        }
    }

    private void drawSelectedInfo(Graphics2D g2, int panelWidth, int panelHeight) {
        if(m_SelectedPoint == null)
            return;

        Episode episode = m_SelectedPoint.getEpisode();
        g2.setFont(m_Sizes.popupFont);
        String title = episode.getTitle();
        float titleWidth = Math.max(m_Sizes.minInfoWidth, g2.getFontMetrics().stringWidth(title));
        float width = 2 * m_Sizes.popupMargin + titleWidth;
        float height = 2 * m_Sizes.popupMargin + 3 * (m_Sizes.popupMargin + m_Sizes.popupTextHeight);
        float pointX = m_SelectedPoint.getX();
        float pointY = m_SelectedPoint.getY();
        float x, y;

        g2.setColor(Color.BLACK);

        if(pointX < panelWidth / 2) {
            x = pointX + m_Sizes.popupOffset + m_Sizes.arrowSize;
        } else {
            x = pointX - m_Sizes.popupOffset - width;
        }

        if(pointY < panelHeight / 3) {
            y = pointY + m_Sizes.popupOffset + m_Sizes.arrowSize;
        } else if (pointY > 2 * panelHeight / 3){
            y = pointY - m_Sizes.popupOffset - height;
        } else {
            y = pointY - height / 2;
        }

        g2.setColor(m_InfoColor);
        g2.fillRoundRect(Math.round(x), Math.round(y),  Math.round(width),
                Math.round(height), Math.round(2 * m_Sizes.popupMargin), Math.round(2 * m_Sizes.popupMargin));
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(Math.round(x - 1), Math.round(y - 1),  Math.round(width + 1),
                Math.round(height + 2), Math.round(2 * m_Sizes.popupMargin), Math.round(2 * m_Sizes.popupMargin));
        centerText(g2, title, Math.round(x + width / 2), Math.round(y + m_Sizes.popupMargin + m_Sizes.popupTextHeight));
        g2.drawLine(Math.round(x + m_Sizes.popupMargin), Math.round(y + 2 * m_Sizes.popupMargin + m_Sizes.popupTextHeight),
                Math.round(x + m_Sizes.popupMargin + titleWidth), Math.round(y + 2 * m_Sizes.popupMargin + m_Sizes.popupTextHeight));
        centerText(g2, "Season: " + episode.getSeasonNumber(), x + width / 4, y + m_Sizes.popupMargin + 2 * (m_Sizes.popupMargin + m_Sizes.popupTextHeight));
        centerText(g2, "Episode: " + episode.getEpisodeNumber(), x + width / 4, y + m_Sizes.popupMargin + 3 * (m_Sizes.popupMargin + m_Sizes.popupTextHeight));
        centerText(g2, "Rating: " + episode.getRating(), x + 3 * width / 4, y + m_Sizes.popupMargin + 2 * (m_Sizes.popupMargin + m_Sizes.popupTextHeight));
        centerText(g2, "# Votes: " + episode.getNumVotes(), x + 3 * width / 4, y + m_Sizes.popupMargin + 3 * (m_Sizes.popupMargin + m_Sizes.popupTextHeight));
    }

    private void centerText(Graphics2D g2, String text, float x, float y){
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
            DataPoint point = DataPoint.findDataPoint(m_Points, e.getX(), e.getY(), m_Sizes.pointRadius);
            if(point != m_SelectedPoint) {
                m_SelectedPoint = point;
                repaint();
            }
        }
    }
}
