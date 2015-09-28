package net.ledii.kittyfit.kittyfit;

import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;

public class AdoptActivity extends AppCompatActivity {

    ImageButton btnBack, btnNext;
    private int catNum = 0;
    private int KITTENS = 3;
    SoundPool soundPool;
    private class Kitten {
        ImageView imgBody, imgHead, imgHeadDecor;
        int skinColors[];
        int SKIN_COLORS = 9;
        int cries[];
        Runnable endCry;
        Handler handler;
        boolean crying = false;

        Kitten() {
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


            //Add cries
            cries = new int[5];
            cries[0] = soundPool.load(AdoptActivity.this, R.raw.kitten01, 1);
            cries[1] = soundPool.load(AdoptActivity.this, R.raw.kitten02, 1);
            cries[2] = soundPool.load(AdoptActivity.this, R.raw.kitten03, 1);
            cries[3] = soundPool.load(AdoptActivity.this, R.raw.kitten04, 1);
            cries[4] = soundPool.load(AdoptActivity.this, R.raw.kitten05, 1);

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
            imgBody = new ImageView(AdoptActivity.this);
            imgBody.setImageResource(R.mipmap.img_kitty_body_white);
            addImage(imgBody);
            imgHead = new ImageView(AdoptActivity.this);
            imgHead.setImageResource(R.mipmap.img_kitty_head_white);
            addImage(imgHead);
            imgHeadDecor = new ImageView(AdoptActivity.this);
            imgHeadDecor.setImageResource(R.mipmap.img_kitty_head_overlay_white);
            addImage(imgHeadDecor);

            //Set skin tones
            Random rand = new Random();
            int skinColor = skinColors[rand.nextInt(SKIN_COLORS)];
            imgBody.setColorFilter(skinColor, PorterDuff.Mode.MULTIPLY);
            imgHead.setColorFilter(skinColor, PorterDuff.Mode.MULTIPLY);

            int decorColor = skinColors[rand.nextInt(SKIN_COLORS)];
            imgHeadDecor.setColorFilter(decorColor, PorterDuff.Mode.MULTIPLY);
            int visible = 0;
            if (rand.nextBoolean()) { visible = 255; }
            imgHeadDecor.setImageAlpha(visible);

            //Position body & head
            int x = 450;
            int y = 1400;
            imgBody.setX(x);
            imgBody.setY(y);
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

        private void addImage(ImageView img) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.adoptView);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.BELOW, R.id.adoptView);
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

        public void show(boolean state) {
            int showState = View.INVISIBLE;
            if (state) { showState = View.VISIBLE; }
            imgBody.setVisibility(showState);
            imgHead.setVisibility(showState);
            imgHeadDecor.setVisibility(showState);
        }
    }

    private Kitten kittens[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt);

        buildSoundPool();
        setupButtons();
        kittens = new Kitten[KITTENS];
        for (int i = 0; i < KITTENS; i++) {
            kittens[i] = new Kitten();
        }
        displayKitten(catNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adopt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayKitten(int num) {
        //Hide all kittens
        for (int i = 0; i < KITTENS; i++) {
            kittens[i].show(false);
        }
        kittens[num].show(true);
    }

    private void buttonToggle(ImageButton btn, boolean state) {
        btn.setEnabled(state);
        if (state) { btn.setVisibility(View.VISIBLE); }
        else { btn.setVisibility(View.INVISIBLE); }
    }

    private void switchCat(int change) {
        catNum += change;

        //Auto enable/disable buttons
        buttonToggle(btnBack, true);
        buttonToggle(btnNext, true);
        if (catNum == 0) { buttonToggle(btnBack, false); }
        if (catNum == KITTENS - 1) { buttonToggle(btnNext, false); }

        displayKitten(catNum);
        //print("CatValue: " + catNum);
    }

    private void print(String txt) {
        Toast.makeText(AdoptActivity.this, txt, Toast.LENGTH_SHORT).show();
    }

    private void setupButtons() {
        btnBack = (ImageButton) findViewById(R.id.adopt_btn_Left);
        btnNext = (ImageButton) findViewById(R.id.adopt_btn_Right);

        buttonToggle(btnBack, false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCat(-1);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCat(1);
            }
        });
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
}
