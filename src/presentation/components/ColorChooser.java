package presentation.components;

import java.awt.*;

public class ColorChooser {
    public static Color chooseColor(int w, int top) {
        double STEP = 0.6180339887f * 2 * Math.PI;
        double SQRT_2 = Math.sqrt(2);

        double theta = STEP * (w - 5);
        double s = Math.sin(theta);
        double c = Math.cos(theta);

        int y, b, r, g;

        if (s > 0) {
            y = (int) (255 * s);
            b = 0;
        } else {
            y = 0;
            b = (int) (-255 * s);
        }
        if (c > 0) {
            r = (int) (255 * c);
            g = 0;
        } else {
            r = 0;
            g = (int) (-255 * c);
        }
        double fr = Math.floor((r + y) / SQRT_2);
        double fg = Math.floor((g + y) / SQRT_2);
        double fb = Math.floor(b);
        return new Color((int)fr, (int)fg, (int)fb);
    }
}
