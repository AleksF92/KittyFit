package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Kitten {
    private Context parent;
    private View parentView;
    private ImageView imgBody, imgBodyDecor, imgHead, imgHeadDecor;
    private int colIds[];
    private int skinColors[];
    private int SKIN_COLORS = 9;
    private SoundPool soundPool;
    private int cries[];
    private Runnable endCry, endPurr;
    private Handler handler;
    private boolean crying = false;
    private String name = "";
    private int purrSound;
    private boolean purring = false;
    private int pettedCount = 0;
    private int purrMargin = 4;
    private int purrStreamId;
    private float purrVolume = 1;
    private float voice;
    private long lastFedTime;
    private TextView txtName;

    Kitten(Context context, View view) {
        parent = context;
        parentView = view;

        //Add precolors
        skinColors = new int[SKIN_COLORS];
        skinColors[0] = Global.WHITE; //White
        skinColors[1] = Global.LT_GRAY; //Light Gray
        skinColors[2] = Global.GRAY; //Gray
        skinColors[3] = Global.DK_GRAY; //Dark Gray
        skinColors[4] = Global.LT_BROWN; //Light Brown
        skinColors[5] = Global.BROWN; //Brown
        skinColors[6] = Global.DK_BROWN; //Dark Brown
        skinColors[7] = Global.LT_YELLOW; //Light Yellow
        skinColors[8] = Global.ORANGE; //Orange

        //Create sound player
        buildSoundPool();

        //Add cries
        cries = new int[5];
        cries[0] = soundPool.load(parent, R.raw.kitten01, 1);
        cries[1] = soundPool.load(parent, R.raw.kitten02, 1);
        cries[2] = soundPool.load(parent, R.raw.kitten03, 1);
        cries[3] = soundPool.load(parent, R.raw.kitten04, 1);
        cries[4] = soundPool.load(parent, R.raw.kitten05, 1);

        //Add purring
        purrSound = soundPool.load(parent, R.raw.kitten_purring_short, 1);

        //Timer task
        endCry = new Runnable() {
            @Override
            public void run() {
                imgHead.setY(imgHead.getY() + 20);
                imgHeadDecor.setY(imgHead.getY());
                crying = false;
            }
        };
        endPurr = new Runnable() {
            @Override
            public void run() {
                purring = false;
            }
        };
        handler = new Handler();

        //Create body & head
        imgBody = new ImageView(parent);
        imgBody.setImageResource(R.mipmap.img_kitty_body_white);
        Global.addViewToParent(parentView, imgBody, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);
        imgBodyDecor = new ImageView(parent);
        imgBodyDecor.setImageResource(R.mipmap.img_kitty_body_overlay_white);
        Global.addViewToParent(parentView, imgBodyDecor, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);
        imgHead = new ImageView(parent);
        imgHead.setImageResource(R.mipmap.img_kitty_head_white);
        Global.addViewToParent(parentView, imgHead, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);
        imgHeadDecor = new ImageView(parent);
        imgHeadDecor.setImageResource(R.mipmap.img_kitty_head_overlay_white);
        Global.addViewToParent(parentView, imgHeadDecor, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);

        colIds = new int[4];

        //Skin tones
        Random rand = new Random();
        int skinId = rand.nextInt(SKIN_COLORS);
        imgBody.setColorFilter(skinColors[skinId], PorterDuff.Mode.MULTIPLY);
        colIds[0] = skinId;
        skinId = rand.nextInt(SKIN_COLORS);
        imgHead.setColorFilter(skinColors[skinId], PorterDuff.Mode.MULTIPLY);
        colIds[1] = skinId;

        //Overlay skin tones
        int visible = 0;
        if (rand.nextBoolean()) { visible = 255; }
        imgBodyDecor.setImageAlpha(visible);
        if (visible == 255) {
            skinId = rand.nextInt(SKIN_COLORS);
            imgBodyDecor.setColorFilter(skinColors[skinId], PorterDuff.Mode.MULTIPLY);
            colIds[2] = skinId;
        }
        else {
            colIds[2] = -1;
        }

        if (rand.nextBoolean()) { visible = 255; }
        imgHeadDecor.setImageAlpha(visible);
        if (visible == 255) {
            skinId = rand.nextInt(SKIN_COLORS);
            imgHeadDecor.setColorFilter(skinColors[skinId], PorterDuff.Mode.MULTIPLY);
            colIds[3] = skinId;
        }
        else {
            colIds[3] = -1;
        }

        //Randomize voice
        voice = rand.nextFloat() + 0.5f;

        //Position body & head
        int x = 500;
        int y = 1600;
        imgBody.setX(x);
        imgBody.setY(y);
        imgBodyDecor.setX(imgBody.getX());
        imgBodyDecor.setY(imgBody.getY());
        imgHead.setX(x - 180);
        imgHead.setY(y - 220);
        imgHeadDecor.setX(imgHead.getX());
        imgHeadDecor.setY(imgHead.getY());

        imgBody.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                kittenTouched(event);
                return true;
            }
        });
        imgHead.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                kittenTouched(event);
                return true;
            }
        });

        txtName = new TextView(parent);
        txtName.setText(name);
        Global.addViewToParent(parentView, txtName, RelativeLayout.ALIGN_TOP, RelativeLayout.CENTER_HORIZONTAL);
        txtName.setY(y + 400);
        txtName.setTextColor(Global.WHITE);
    }

    Kitten(Context context, View view, Bundle data) {
        this(context, view);

        setName(data.getString("kittenName", ""));
        int bCol = data.getInt("kittenBodyColor", 0);
        int hCol = data.getInt("kittenHeadColor", 0);
        int bdCol = data.getInt("kittenBodyDecorColor", 0);
        int hdCol = data.getInt("kittenHeadDecorColor", 0);
        voice = data.getFloat("kittenVoice", 1);
        lastFedTime = data.getLong("kittenLastFedTime", 0);

        //Skin tones
        imgBody.setColorFilter(skinColors[bCol], PorterDuff.Mode.MULTIPLY);
        imgHead.setColorFilter(skinColors[hCol], PorterDuff.Mode.MULTIPLY);

        //Overlay skin tones
        if (bdCol == -1) {
            imgBodyDecor.setImageAlpha(0);
        }
        else {
            imgBodyDecor.setImageAlpha(255);
            imgBodyDecor.setColorFilter(skinColors[bdCol], PorterDuff.Mode.MULTIPLY);
        }

        if (hdCol == -1) {
            imgHeadDecor.setImageAlpha(0);
        }
        else {
            imgHeadDecor.setImageAlpha(255);
            imgHeadDecor.setColorFilter(skinColors[hdCol], PorterDuff.Mode.MULTIPLY);
        }

        //Update stored colors
        colIds[0] = bCol;
        colIds[1] = hCol;
        colIds[2] = bdCol;
        colIds[3] = hdCol;
    }

    private void cry() {
        if (!crying) {
            crying = true;

            Random rand = new Random();
            int randCry = cries[rand.nextInt(5)];
            soundPool.play(randCry, 1, 1, 1, 0, voice);

            imgHead.setY(imgHead.getY() - 20);
            imgHeadDecor.setY(imgHead.getY());
            int delay = Math.round(1000 / voice);
            handler.postDelayed(endCry, delay);
        }
    }

    private void buildSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(25)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void show(boolean state) {
        int showState = View.INVISIBLE;
        if (state) { showState = View.VISIBLE; }
        imgBody.setVisibility(showState);
        imgBodyDecor.setVisibility(showState);
        imgHead.setVisibility(showState);
        imgHeadDecor.setVisibility(showState);
    }

    public void setName(String txt) {
        name = txt;
        txtName.setText(name);
    }

    public String getName() {
        return name;
    }

    public Bundle getData() {
        Bundle data = new Bundle();

        data.putString("kittenName", name);
        data.putInt("kittenBodyColor", colIds[0]);
        data.putInt("kittenHeadColor", colIds[1]);
        data.putInt("kittenBodyDecorColor", colIds[2]);
        data.putInt("kittenHeadDecorColor", colIds[3]);
        data.putFloat("kittenVoice", voice);
        data.putLong("kittenLastFedTime", lastFedTime);

        return data;
    }

    public void destroy() {
        imgBody.setVisibility(View.GONE);
        imgHead.setVisibility(View.GONE);
        imgBodyDecor.setVisibility(View.GONE);
        imgHeadDecor.setVisibility(View.GONE);
        txtName.setVisibility(View.GONE);
    }

    private void kittenTouched(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                pettedCount = 0;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (event.getX() >= 70 && event.getX() <= 470) {
                    if (event.getY() >= 70 && event.getY() <= 470) {
                        pettedCount++;
                        if (pettedCount >= purrMargin) {
                            purr(true);
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (pettedCount < purrMargin) {
                    cry();
                }
                else {
                    purr(false);
                }
                break;
            }
        }
    }

    private void purr(boolean state) {
        if (state) {
            if (purring) {
                purrVolume = 1;
                soundPool.setVolume(purrStreamId, purrVolume, purrVolume);
            }
            else {
                purring = true;
                purrStreamId = soundPool.play(purrSound, 1, 1, 1, 0, voice);
                int delay = Math.round(1750 / voice);
                handler.postDelayed(endPurr, delay);
            }
        }
        else {
            if (purring) {
                purrVolume = 0.5f;
                soundPool.setVolume(purrStreamId, purrVolume, purrVolume);
            }
        }
    }

    public void feed() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        lastFedTime = date.getTime();
    }

    public int hoursSinceLastFed() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        long timePassed = date.getTime() - lastFedTime;

        int HOUR_MS = 1000;
        int hours = Math.round(timePassed / HOUR_MS);

        //Log.d("DEBUG", "TimePassed: " + timePassed + ", Hours: " + hours);

        return hours;
    }
}
