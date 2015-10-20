package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Task extends View {
    Context parent;
    View parentView;
    Frame frame;
    TextView txtTitle, txtReward;
    StatusBar barProgress;
    Button btnStart;
    boolean expanded = true;

    Task(Context context, View view) {
        super(context);

        //Keep parent
        parent = context;
        parentView = view;

        //Get resolution
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        float d = metrics.density;

        //Setup view
        frame = new Frame(parent, screenSize.x, 0);
        frame.setColor(Global.DK_GRAY, Global.COLOR_BUTTONS);
        Global.addViewToParent(parentView, frame, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        txtTitle = new TextView(parent);
        txtTitle.setTextColor(Global.WHITE);
        txtTitle.setX(32);
        txtTitle.setY(16);
        Global.addViewToParent(parentView, txtTitle, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        txtReward = new TextView(parent);
        txtReward.setTextColor(Global.WHITE);
        txtReward.setX(-32);
        txtReward.setY(16);
        Global.addViewToParent(parentView, txtReward, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_PARENT_RIGHT);

        barProgress = new StatusBar(parent, parentView);
        barProgress.setSuffix(" meters");
        barProgress.setX(16);
        barProgress.setY(128);
        barProgress.setSize(800, 50);
        barProgress.setVisible(View.VISIBLE);
        Global.addViewToParent(parentView, barProgress, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        btnStart = new Button(parent);
        btnStart.setText("Claim");
        btnStart.setX(-16);
        btnStart.setY(128 + 32);
        btnStart.getBackground().setColorFilter(Global.COLOR_BUTTONS, PorterDuff.Mode.SRC);
        btnStart.setEnabled(false);
        Global.addViewToParent(parentView, btnStart, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_PARENT_RIGHT);

        toggleExpand();

        //Click event
        frame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "Clicked frame");
                toggleExpand();
            }
        });

        //Create random quest
        txtTitle.setText("Run for 100m");
        txtReward.setText("Money: 5$");
    }

    private void toggleExpand() {
        expanded = !expanded;

        Point size = frame.getSize();
        if (expanded) {
            size.y = 128 + 32;
            setExpandViewVisibility(VISIBLE);
        }
        else {
            size.y = 48;
            setExpandViewVisibility(INVISIBLE);
        }
        frame.setSize(size);
    }

    private void setExpandViewVisibility(int state) {
        barProgress.setVisible(state);
        btnStart.setVisibility(state);
    }

    private void setPosition(int x, int y) {
        float diffX = x - frame.getX();
        float diffY = y - frame.getY();

        frame.setX(x);
        frame.setY(y);
        txtTitle.setX(txtTitle.getX() + diffX);
        txtTitle.setY(txtTitle.getY() + diffY);
        txtReward.setX(txtReward.getX() + diffX);
        txtReward.setY(txtReward.getY() + diffY);
        barProgress.setX(barProgress.getX() + diffX);
        barProgress.setY(barProgress.getY() + (diffY / 2));
        btnStart.setX(btnStart.getX() + diffX);
        btnStart.setY(btnStart.getY() + diffY);
    }
}