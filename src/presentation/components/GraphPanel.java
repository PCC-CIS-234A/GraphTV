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
        float regularStrokeSize;
        float axisStrokeSize;
        float leftMargin;
        float topMargin;
        float rightMargin;
        float bottomMargin;
        float leading;
        float ratingTextHeight;
        float popupTextHeight;
        float popupLineHeight;
        float titleHeight;
        float popupMargin;
        float twicePopupMargin;
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

        float graphWidth;
        float graphHeight;
        float graphRight;
        float graphBottom;
        float graphMiddle;
        float gridLineLeft;
        float popupArrowOffset;
        
        int leftMarginInt;
        int topMarginInt;
        int graphRightInt;
        int graphBottomInt;
        int gridLineLeftInt;
        int popupLineHeightInt;
        int popupMarginInt;

        BasicStroke regularStroke;
        BasicStroke axisStroke;
        BasicStroke regressionStroke;
        BasicStroke regressionBorderStroke;

        Font popupFont;
        Font regularFont;
        Font titleFont;
        Font pointFont;
        Font innerPointFont;

        private void calculateSizes(int panelWidth, int panelHeight) {
            float base = (float) Math.sqrt(panelWidth * panelHeight) / 500.0f;

            regularStrokeSize = 1 * base;
            axisStrokeSize = 2 * base;
            leftMargin = 40 * base;
            topMargin = 25 * base;
            rightMargin = 10 * base;
            bottomMargin = 40 * base;
            leading = 5 * base;
            ratingTextHeight = 10 * base;
            popupTextHeight = 8 * base;
            titleHeight = 20 * base;
            popupMargin = 5 * base;
            popupOffset = 10 * base;
            arrowSize = 8 * base;
            minInfoWidth = 120 * base;
            popupFontSize = 10 * base;
            regularFontSize = 12 * base;
            titleFontSize = 18 * base;
            pointSize = 8 * base;
            pointRadius = pointSize / 2;
            innerPointSize = pointSize * .75f;
            innerPointRadius = innerPointSize / 2;

            graphWidth = Math.round(panelWidth - m_Sizes.leftMargin - m_Sizes.rightMargin);
            graphHeight = Math.round(panelHeight - m_Sizes.topMargin - m_Sizes.bottomMargin);
            graphRight = leftMargin + graphWidth;
            graphBottom = topMargin + graphHeight;
            graphMiddle = graphBottom - graphHeight / 2;
            gridLineLeft = leftMargin - leading;
            popupArrowOffset = popupOffset + arrowSize;
            twicePopupMargin = popupMargin * 2;
            popupLineHeight = popupMargin + popupTextHeight;
            graphRightInt = Math.round(graphRight);
            graphBottomInt = Math.round(graphBottom);
            leftMarginInt = Math.round(leftMargin);
            topMarginInt = Math.round(topMargin);
            gridLineLeftInt = Math.round(gridLineLeft);
            popupLineHeightInt = Math.round(popupLineHeight);
            popupMarginInt = Math.round(popupMargin);

            axisStroke = new BasicStroke(axisStrokeSize);
            regressionStroke = new BasicStroke(2 * base);
            regressionBorderStroke = new BasicStroke(4 * base);
            regularStroke = new BasicStroke(regularStrokeSize);

            popupFont = new Font("TimesRoman", Font.PLAIN, Math.round(popupFontSize));
            regularFont = new Font("TimesRoman", Font.PLAIN, Math.round(regularFontSize));
            titleFont = new Font("TimesRoman", Font.BOLD, Math.round(titleFontSize));
            pointFont = new Font("Helvetica", Font.BOLD, Math.round(1.8f * pointSize));
            innerPointFont = new Font("Helvetica", Font.BOLD, Math.round(1.8f * innerPointSize));
        }
    }

    private Sizes m_Sizes;
    private Color m_InfoColor;
    private Color m_InfoBorderColor;

    public GraphPanel() {
        super();
        m_InfoColor = new Color(255, 245, 220);
        m_InfoBorderColor = new Color(200, 120, 0);
        addMouseMotionListener(this);
        addMouseListener(this);
        m_Sizes = new Sizes();
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

        m_Sizes.calculateSizes(panelWidth, panelHeight);
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.add(new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP));
        g2.setRenderingHints(rh);

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
        drawAxes(g2, minRating, maxRating);
        plotPoints(minRating, maxRating);
        drawRegressionLines(g2, maxSeason);
        drawPoints(g2, maxSeason);

        drawSelectedInfo(g2, panelWidth, panelHeight);
    }

    private void drawAxes(Graphics2D g2, float minRating, float maxRating) {
        g2.setStroke(m_Sizes.axisStroke);
        g2.drawLine(m_Sizes.leftMarginInt, Math.round(m_Sizes.topMargin - m_Sizes.leading), m_Sizes.leftMarginInt, m_Sizes.graphBottomInt);
        g2.drawLine(m_Sizes.leftMarginInt, m_Sizes.graphBottomInt, m_Sizes.graphRightInt, m_Sizes.graphBottomInt);
        g2.setStroke(m_Sizes.regularStroke);
        for (float rating = minRating; rating <= maxRating; rating += 0.5f) {
            int y = Math.round(m_Sizes.graphBottom - m_Sizes.graphHeight * (rating - minRating) / (maxRating - minRating));
            String ratingString = "" + rating;
            int ratingWidth = g2.getFontMetrics().stringWidth(ratingString);
            g2.drawString(ratingString, m_Sizes.leftMargin - 2 * m_Sizes.leading - ratingWidth, y + m_Sizes.ratingTextHeight / 2.3f);
            g2.drawLine(m_Sizes.gridLineLeftInt, y, m_Sizes.graphRightInt, y);
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

    private void plotPoints(float minRating, float maxRating) {
        int length = m_Episodes.size();

        if (m_Points == null)
            m_Points = new ArrayList<>();
        else
            m_Points.clear();
        for (int i = 0; i < m_Episodes.size(); i++) {
            Episode episode = m_Episodes.get(i);
            if (episode.getRating() > 0) {
                float x = m_Sizes.leftMargin + m_Sizes.graphWidth * (i + 0.5f) / length;
                float y = m_Sizes.graphBottom - m_Sizes.graphHeight * (episode.getRating() - minRating) / (maxRating - minRating);
                DataPoint p = new DataPoint(Math.round(x), Math.round(y), episode);
                m_Points.add(p);
                // System.out.println(episode.getTitle() + "   " + episode.getSeasonNumber() + "   " + episode.getEpisodeNumber() + "  " + episode.getRating() + " " + episode.getNumVotes());
            } else {
                float x = m_Sizes.leftMargin + m_Sizes.graphWidth * (i + 0.5f) / length;
                float y = m_Sizes.graphMiddle;
                DataPoint p = new DataPoint(Math.round(x), Math.round(y), episode);
                m_Points.add(p);
            }
        }
    }

    private void drawPoints(Graphics2D g2, int maxSeason) {
        for (DataPoint p: m_Points) {
            g2.setColor(Color.BLACK);

            if (p.getEpisode().getRating() > 0) {
                int left = Math.round(p.getX() - m_Sizes.pointRadius);
                int top = Math.round(p.getY() - m_Sizes.pointRadius);
                int innerLeft = Math.round(left + m_Sizes.pointRadius - m_Sizes.innerPointRadius);
                int innerTop = Math.round(top + m_Sizes.pointRadius - m_Sizes.innerPointRadius);
                g2.fillArc(left, top, Math.round(m_Sizes.pointSize), Math.round(m_Sizes.pointSize), 0, 360);
                g2.setColor(ColorChooser.chooseColor(p.getEpisode().getSeasonNumber(), maxSeason));
                g2.fillArc(innerLeft, innerTop,
                        Math.round(m_Sizes.innerPointSize), Math.round(m_Sizes.innerPointSize), 0, 360);
            } else {
                int left = Math.round(p.getX() - m_Sizes.pointRadius);
                int top = Math.round(p.getY() + m_Sizes.pointRadius - m_Sizes.pointRadius / 4);
                int innerLeft = Math.round(left + m_Sizes.pointRadius - m_Sizes.innerPointRadius);
                int innerTop = Math.round(top + m_Sizes.pointRadius - m_Sizes.innerPointRadius);
                int shift = (int)Math.ceil(m_Sizes.pointRadius - m_Sizes.innerPointRadius);
                g2.setColor(Color.BLACK);
                g2.setFont(m_Sizes.innerPointFont);
                g2.drawString("?", innerLeft - shift, innerTop - shift);
                g2.drawString("?", innerLeft - shift, innerTop);
                g2.drawString("?", innerLeft - shift, innerTop + shift);
                g2.drawString("?", innerLeft, innerTop - shift);
                g2.drawString("?", innerLeft, innerTop + shift);
                g2.drawString("?", innerLeft + shift, innerTop - shift);
                g2.drawString("?", innerLeft + shift, innerTop);
                g2.drawString("?", innerLeft + shift, innerTop + shift);
                g2.setColor(ColorChooser.chooseColor(p.getEpisode().getSeasonNumber(), maxSeason));
                g2.drawString("?", innerLeft, innerTop);
            }
        }
    }

    private void drawSelectedInfo(Graphics2D g2, int panelWidth, int panelHeight) {
        if(m_SelectedPoint == null)
            return;

        Episode episode = m_SelectedPoint.getEpisode();
        g2.setFont(m_Sizes.popupFont);
        String title = episode.getTitle();
        float titleWidth = Math.max(m_Sizes.minInfoWidth, g2.getFontMetrics().stringWidth(title));
        float width = m_Sizes.twicePopupMargin + titleWidth;
        float height = m_Sizes.twicePopupMargin + 3 * m_Sizes.popupLineHeight;
        float pointX = m_SelectedPoint.getX();
        float pointY = m_SelectedPoint.getY();
        float x, y;

        g2.setColor(Color.BLACK);

        if(pointX < panelWidth / 2) {
            x = pointX + m_Sizes.popupArrowOffset;
        } else {
            x = pointX - m_Sizes.popupOffset - width;
        }

        if(pointY < panelHeight / 3) {
            y = pointY + m_Sizes.popupArrowOffset;
        } else if (pointY > 2 * panelHeight / 3){
            y = pointY - m_Sizes.popupOffset - height;
        } else {
            y = pointY - height / 2;
        }

        float oneLine = y + m_Sizes.popupMargin + m_Sizes.popupLineHeight;
        float twoLines = y + m_Sizes.popupMargin + 2 * m_Sizes.popupLineHeight;
        float threeLines = y + m_Sizes.popupMargin + 3 * m_Sizes.popupLineHeight;
        int xInt = Math.round(x);
        int yInt = Math.round(y);
        int widthInt = Math.round(width);
        int heightInt = Math.round(height);
        int stroke = (int)Math.ceil(m_Sizes.regularStrokeSize);

        g2.setColor(Color.BLACK);
        g2.fillRoundRect(xInt - 2 * stroke, yInt - 2 * stroke,  widthInt + 4 * stroke,heightInt + 4 * stroke, m_Sizes.popupMarginInt, m_Sizes.popupMarginInt);
        g2.setColor(m_InfoBorderColor);
        g2.fillRoundRect(xInt - stroke, yInt - stroke,  widthInt + 2 * stroke,heightInt + 2 * stroke, m_Sizes.popupMarginInt, m_Sizes.popupMarginInt);
        g2.setColor(m_InfoColor);
        g2.fillRoundRect(xInt, yInt,  widthInt, heightInt, m_Sizes.popupMarginInt, m_Sizes.popupMarginInt);
        g2.setColor(Color.BLACK);
        centerText(g2, title, Math.round(x + width / 2), Math.round(y + m_Sizes.popupLineHeight));
        g2.drawLine(Math.round(x + m_Sizes.popupMargin), Math.round(oneLine),
                Math.round(x + m_Sizes.popupMargin + titleWidth), Math.round(oneLine));
        float column1 = x + width / 4;
        float column2 = x + 3 * width / 4;
        centerText(g2, "Season: " + episode.getSeasonNumber(), column1, twoLines);
        centerText(g2, "Episode: " + episode.getEpisodeNumber(), column1, threeLines);
        centerText(g2, "Rating: " + (episode.getRating() > 0 ? episode.getRating() : "???"), column2, twoLines);
        centerText(g2, "Votes: " + (episode.getNumVotes() > 0 ? episode.getNumVotes() : "<5"), column2, threeLines);
    }

    private void centerText(Graphics2D g2, String text, float x, float y){
        int width = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, x - width / 2, y);
    }
    @Override
    public void mouseClicked(MouseEvent event) {
        if (m_SelectedPoint != null) {
            // System.out.println("Clicked " + m_SelectedPoint.getEpisode().getID());
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
