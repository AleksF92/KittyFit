package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.widget.RelativeLayout;

public class Frame extends View {
    private Point size;
    private Paint paint;
    private int frameColor, fillColor;

    Frame(Context context) {
        super(context);
        size = new Point(0, 0);
        setLayoutParams(new RelativeLayout.LayoutParams(size.x, size.y));

        paint = new Paint();
        paint.setStrokeWidth(1);

        fillColor = Global.BLACK;
        frameColor = Global.WHITE;
    }

    Frame(Context context, int w, int h) {
        this(context);
        size.x = w;
        size.y = (h * 2) + 16;
        setLayoutParams(new RelativeLayout.LayoutParams(size.x, size.y));
    }

    @Override
    public void onDraw(Canvas canvas) {
        float x1 = getX();
        float x2 = x1 + size.x;
        float y1 = getY();
        float y2 = y1 + size.y;

        paint.setColor(fillColor);
        paint.setStrokeWidth(1);
        canvas.drawRect(x1, y1, x2, y2, paint);

        paint.setColor(frameColor);
        paint.setStrokeWidth(8);
        canvas.drawLine(x1, y1, x2, y1, paint);
        canvas.drawLine(x2, y1, x2, y2, paint);
        canvas.drawLine(x2, y2, x1, y2, paint);
        canvas.drawLine(x1, y2, x1, y1, paint);
    }

    public void setColor(int fill, int frame) {
        frameColor = frame;
        fillColor = fill;
        postInvalidate();
    }

    public void setSize(Point s) {
        size = s;
        size.y = (size.y * 2) + 16;
        setLayoutParams(new RelativeLayout.LayoutParams(size.x, size.y));
        postInvalidate();
    }

    public Point getSize() {
        return size;
    }
}
