package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

public class Kitten {
    Context parent;
    View parentView;
    ImageView imgBody, imgBodyDecor, imgHead, imgHeadDecor;
    int colIds[];
    int skinColors[];
    int SKIN_COLORS = 9;
    SoundPool soundPool;
    int cries[];
    Runnable endCry;
    Handler handler;
    boolean crying = false;
    String name = "Unknown";

    Kitten(Context context, View view) {
        parent = context;
        parentView = view;

        //Add precolors
        skinColors = new int[SKIN_COLORS];
        skinColors[0] = 0xFFFFFFFF; //White
        skinColors[1] = 0xFFA8A8A8; //Light Gray
        skinColors[2] = 0xFF515151; //Gray
        skinColors[3] = 0xFF212121; //Dark Gray
        skinColors[4] = 0xFF897364; //Light Brown
        skinColors[5] = 0xFF825030; //Brown
        skinColors[6] = 0xFF543D2F; //Dark Brown
        skinColors[7] = 0xFFFFD468; //Light Yellow
        skinColors[8] = 0xFFF77300; //Orange

        //Create sound player
        buildSoundPool();

        //Add cries
        cries = new int[5];
        cries[0] = soundPool.load(parent, R.raw.kitten01, 1);
        cries[1] = soundPool.load(parent, R.raw.kitten02, 1);
        cries[2] = soundPool.load(parent, R.raw.kitten03, 1);
        cries[3] = soundPool.load(parent, R.raw.kitten04, 1);
        cries[4] = soundPool.load(parent, R.raw.kitten05, 1);

        //Timer task
        endCry = new Runnable() {
            @Override
            public void run() {
                imgHead.setY(imgHead.getY() + 20);
                imgHeadDecor.setY(imgHead.getY());
                crying = false;
            }
        };
        handler = new Handler();

        //Create body & head
        imgBody = new ImageView(parent);
        imgBody.setImageResource(R.mipmap.img_kitty_body_white);
        addImage(imgBody);
        imgBodyDecor = new ImageView(parent);
        imgBodyDecor.setImageResource(R.mipmap.img_kitty_body_overlay_white);
        addImage(imgBodyDecor);
        imgHead = new ImageView(parent);
        imgHead.setImageResource(R.mipmap.img_kitty_head_white);
        addImage(imgHead);
        imgHeadDecor = new ImageView(parent);
        imgHeadDecor.setImageResource(R.mipmap.img_kitty_head_overlay_white);
        addImage(imgHeadDecor);

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

        //print(name + ": " + colIds[0] + ", " + colIds[1] + ", " + colIds[2] + ", " + colIds[3]);

        //Position body & head
        int x = 450;
        int y = 1400;
        imgBody.setX(x);
        imgBody.setY(y);
        imgBodyDecor.setX(imgBody.getX());
        imgBodyDecor.setY(imgBody.getY());
        imgHead.setX(x - 180);
        imgHead.setY(y - 220);
        imgHeadDecor.setX(imgHead.getX());
        imgHeadDecor.setY(imgHead.getY());

        imgBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cry();
            }
        });
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cry();
            }
        });
    }

    Kitten(Context context, View view, int bCol, int hCol, int bdCol, int hdCol) {
        this(context, view);

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

    private void addImage(ImageView img) {
        RelativeLayout rl = (RelativeLayout) parentView;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, parentView.getId());
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rl.addView(img, lp);
    }

    private void cry() {
        if (!crying) {
            crying = true;

            Random rand = new Random();
            int randCry = cries[rand.nextInt(5)];
            soundPool.play(randCry, 1, 1, 1, 0, 1);

            imgHead.setY(imgHead.getY() - 20);
            imgHeadDecor.setY(imgHead.getY());
            handler.postDelayed(endCry, 1000);
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
    }

    public String getName() {
        return name;
    }

    public int getBodyColor() {
        return colIds[0];
    }

    public int getHeadColor() {
        return colIds[1];
    }

    public int getBodyDecorColor() {
        return colIds[2];
    }

    public int getHeadDecorColor() {
        return colIds[3];
    }

    public Bundle getData() {
        Bundle data = new Bundle();

        data.putString("name", name);
        data.putInt("bodyColor", colIds[0]);
        data.putInt("headColor", colIds[1]);
        data.putInt("bodyDecorColor", colIds[2]);
        data.putInt("headDecorColor", colIds[3]);

        return data;
    }

    private void print(String txt) {
        Toast.makeText(parent, txt, Toast.LENGTH_SHORT).show();
    }

    public void destroy() {
        imgBody.setVisibility(View.GONE);
        imgHead.setVisibility(View.GONE);
        imgBodyDecor.setVisibility(View.GONE);
        imgHeadDecor.setVisibility(View.GONE);
    }
}
