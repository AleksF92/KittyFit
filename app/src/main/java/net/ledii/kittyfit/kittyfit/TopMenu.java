package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TopMenu {
    private Context parent;
    private View parentView;
    private ImageButton btnRun, btnShop, btnPlay, btnList;
    private Button btnListAdopt, btnListStats, btnListOptions;
    private TextView txtScene;
    private boolean active = false;
    private Runnable runSlide;
    private Handler runThread;
    private Point btnSize;
    private int slideSpeed, slideDelay;
    private float slideAlphaSpeed;
    private boolean slideIn = true;
    private boolean sliding = false;

    TopMenu(Context context, View view) {
        //Copy context & view from parent
        parent = context;
        parentView = view;

        //Create buttons
        btnRun = new ImageButton(parent);
        btnRun.setBackgroundResource(R.mipmap.btn_cute);
        btnRun.setImageResource(R.mipmap.img_menu_run);
        btnRun.setAdjustViewBounds(true);
        btnRun.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnRun, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        btnShop = new ImageButton(parent);
        btnShop.setBackgroundResource(R.mipmap.btn_cute);
        btnShop.setImageResource(R.mipmap.img_menu_shop);
        btnShop.setAdjustViewBounds(true);
        btnShop.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnShop, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        btnPlay = new ImageButton(parent);
        btnPlay.setBackgroundResource(R.mipmap.btn_cute);
        btnPlay.setImageResource(R.mipmap.img_menu_play);
        btnPlay.setAdjustViewBounds(true);
        btnPlay.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnPlay, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        btnList = new ImageButton(parent);
        btnList.setBackgroundResource(R.mipmap.btn_cute);
        btnList.setImageResource(R.mipmap.img_menu_list);
        btnList.setAdjustViewBounds(true);
        btnList.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnList, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        txtScene = new TextView(parent);
        txtScene.setText("Home");
        txtScene.setTextColor(Global.WHITE);
        txtScene.setTextSize(24);
        Global.addViewToParent(parentView, txtScene, RelativeLayout.ALIGN_TOP, RelativeLayout.CENTER_HORIZONTAL);

        btnListAdopt = new Button(parent);
        btnListAdopt.setBackgroundResource(R.mipmap.btn_cute);
        btnListAdopt.setText("Adopt kitten");
        btnListAdopt.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnListAdopt, RelativeLayout.ALIGN_TOP, RelativeLayout.CENTER_HORIZONTAL);

        btnListStats = new Button(parent);
        btnListStats.setBackgroundResource(R.mipmap.btn_cute);
        btnListStats.setText("Statistics");
        btnListStats.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnListStats, RelativeLayout.ALIGN_TOP, RelativeLayout.CENTER_HORIZONTAL);

        btnListOptions = new Button(parent);
        btnListOptions.setBackgroundResource(R.mipmap.btn_cute);
        btnListOptions.setText("Options");
        btnListOptions.setVisibility(View.INVISIBLE);
        Global.addViewToParent(parentView, btnListOptions, RelativeLayout.ALIGN_TOP, RelativeLayout.CENTER_HORIZONTAL);

        //Place buttons properly
        int offX = 160;
        btnSize = getResourceSize(parent, R.mipmap.img_menu_run);
        int btnOffX = offX + btnSize.x;
        btnRun.setX((btnOffX * 0));
        btnShop.setX((btnOffX * 1));
        btnPlay.setX((btnOffX * 2));
        btnList.setX((btnOffX * 3));

        int btnOffY = btnSize.y / 2;
        btnShop.setY(btnOffY);
        btnList.setY(btnOffY);

        txtScene.setY(btnSize.y * 1.5f);

        btnListAdopt.setY(btnSize.y * 2.25f);
        btnListStats.setY(btnSize.y * 3f);
        btnListOptions.setY(btnSize.y * 3.75f);

        //Set click event
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("Run");
            }
        });
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("Shop");
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("Play");
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("List");
            }
        });
        btnListAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("Adopt");
            }
        });
        btnListStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("Statistics");
            }
        });
        btnListOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton("Options");
            }
        });

        //Create run thread and processes
        slideSpeed = 30;
        int slideAmount = Math.round((btnSize.y * 1.5f) / slideSpeed);
        slideAlphaSpeed = 1.0f / slideAmount;
        slideDelay = 0;

        runThread = new Handler();
        runSlide = new Runnable() {
            @Override
            public void run() {
                //Set direction
                int speed = slideSpeed;
                float alphaSpeed = slideAlphaSpeed;
                if (!slideIn) {
                    speed *= -1;
                    alphaSpeed *= -1;
                }

                //Move buttons
                float yInc = btnRun.getY() + speed;
                if (slideIn && yInc > 0) {
                    yInc = speed - yInc;
                }
                else if (!slideIn && yInc < btnSize.y * -1.5f) {
                    yInc = (btnSize.y * -1.5f) - btnRun.getY();
                }
                else {
                    yInc = speed;
                }
                btnRun.setY(btnRun.getY() + yInc);
                btnShop.setY(btnShop.getY() + yInc);
                btnPlay.setY(btnPlay.getY() + yInc);
                btnList.setY(btnList.getY() + yInc);
                txtScene.setY(txtScene.getY() + yInc);

                //Find alpha
                float a = btnRun.getAlpha() + alphaSpeed;
                if (slideIn && a > 1) {
                    a = 1;
                }
                else if (!slideIn && a < 0) {
                    a = 0;
                }

                //Set new alpha
                btnRun.setAlpha(a);
                btnShop.setAlpha(a);
                btnPlay.setAlpha(a);
                btnList.setAlpha(a);

                //Log.d("DEBUG", "Alpha: " + a + ", Speed: " + alphaSpeed);

                //Determine state
                if (a == 0) {
                    //show();
                    sliding = false;
                    btnRun.setVisibility(View.VISIBLE);
                    btnShop.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.VISIBLE);
                    btnList.setVisibility(View.VISIBLE);
                }
                else if (a == 1) {
                    active = true;
                    sliding = false;
                }
                else {
                    runThread.post(runSlide);
                    //runThread.postDelayed(runSlide, slideDelay);
                }
            }
        };

        //Temp activate
        hide();
    }

    private Point getResourceSize(Context context, int resourceId) {
        Point size = new Point(0, 0);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resourceId, dimensions);

        float density = context.getResources().getDisplayMetrics().density;
        int w = Math.round(dimensions.outHeight * density);
        int h = Math.round(dimensions.outWidth * density);
        size.set(w, h);

        return size;
    }

    private void clickButton(String btnName) {
        if (active) {
            switch (btnName) {
                case "Run": {
                    break;
                }
                case "Shop": {
                    break;
                }
                case "Play": {
                    break;
                }
                case "List": {
                    int listToggle = View.INVISIBLE;
                    if (btnListAdopt.getVisibility() == View.INVISIBLE) {
                        listToggle = View.VISIBLE;
                    }
                    showList(listToggle);
                    break;
                }
                case "Adopt": {
                    break;
                }
                case "Statistics": {
                    break;
                }
                case "Options": {
                    break;
                }
            }

            ((MainActivity) parent).sceneUpdate(btnName);
            //Toast.makeText(parent, btnName + " was clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    public void show() {
        if (!sliding && !slideIn) {
            slideIn = true;
            sliding = true;
            runThread.postDelayed(runSlide, slideDelay);
        }
    }

    public void hide() {
        if (!sliding && slideIn) {
            active = false;
            btnRun.setImageAlpha(255);
            slideIn = false;
            sliding = true;
            showList(View.INVISIBLE);

            runThread.post(runSlide);
            //runThread.postDelayed(runSlide, slideDelay);
        }
    }

    public void setSceneText(String txt) {
        txtScene.setText(txt);
    }

    public void showList(int state) {
        btnListAdopt.setVisibility(state);
        btnListStats.setVisibility(state);
        btnListOptions.setVisibility(state);
    }

    public int getListVisibility() {
        return btnListAdopt.getVisibility();
    }

    public void enableAdoption(boolean state) {
        btnListAdopt.setEnabled(state);
    }
}
