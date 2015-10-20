package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatusBar extends View {
    private int max = 100;
    private int value = 50;
    private Point size;
    private Context parent;
    private View parentView;
    private TextView txtHint;
    private String hint = "";
    private String suffix = "";

    StatusBar(Context context, View view) {
        super(context);

        parent = context;
        parentView = view;

        size = new Point(1200, 50);

        txtHint = new TextView(parent);
        txtHint.setText(hint + value);
        txtHint.setTextColor(Global.WHITE);
        Global.addViewToParent(parentView, txtHint, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        txtHint.setVisibility(INVISIBLE);
    }

    StatusBar(Context context, View view, int max) {
        this(context, view);
        this.max = max;
    }

    public void setValue(int val) {
        int lastValue = value;
        value = val;
        if (value > max) {
            value = max;
        }
        else if (value < 0) {
            value = 0;
        }

        if (value != lastValue) {
            postInvalidate();
        }
        //Log.d("DEBUG", "New Value: " + value + " / " + max);
    }

    public int getValue() {
        return value;
    }

    public void setHint(String str) {
        hint = str;
        postInvalidate();
    }

    public void setSuffix(String str) {
        suffix = str;
        postInvalidate();
    }

    public void setMaxValue(int max) {
        int lastMax = this.max;
        this.max = max;
        if (value > max) {
            value = max;
        }

        if (this.max != lastMax) {
            postInvalidate();
        }
    }

    public int getMaxValue() {
        return max;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Global.WHITE);

        float x1 = getX();
        float x2 = x1 + size.x;
        float y1 = getY();
        float y2 = y1 + size.y;

        canvas.drawRect(x1, y1, x2, y2, paint);

        paint.setColor(Global.LIME);
        float percent = (float)value / max;
        x2 = x1 + (size.x * percent);
        canvas.drawRect(x1, y1, x2, y2, paint);

        x1 *= 2;
        y1 = ((y1 - size.y) * 2) + 16;
        txtHint.setX(x1);
        txtHint.setY(y1);
        txtHint.setText(hint + value + suffix);
    }

    public void setVisible(int state) {
        setVisibility(state);
        txtHint.setVisibility(state);
    }

    public void setSize(int x, int y) {
        size.x = x;
        size.y = y;
        postInvalidate();
    }
}
