package net.ledii.kittyfit.kittyfit;

import android.view.View;
import android.widget.RelativeLayout;

public class Global {
    public static int WHITE = 0xFFFFFFFF;
    public static int LT_GRAY = 0xFFA8A8A8;
    public static int GRAY = 0xFF515151;
    public static int DK_GRAY = 0xFF212121;
    public static int LT_BROWN = 0xFF897364;
    public static int BROWN = 0xFF825030;
    public static int DK_BROWN = 0xFF543D2F;
    public static int LT_YELLOW = 0xFFFFD468;
    public static int LT_ORANGE = 0xFFFF9C32;
    public static int ORANGE = 0xFFF77300;
    public static int LIME = 0xFF00FF00;

    public static int COLOR_BUTTONS = LT_ORANGE;

    public static void addViewToParent(View parentView, View view, int alignY, int alignX) {
        RelativeLayout rl = (RelativeLayout) parentView;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        lp.addRule(alignY);
        lp.addRule(alignX);
        rl.addView(view, lp);
    }
}
