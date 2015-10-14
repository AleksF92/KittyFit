package net.ledii.kittyfit.kittyfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TopMenu topMenu;
    private Kitten kitten;
    private SharedPreferences storedData;
    private VelocityTracker touchTracker;
    private StatusBar barFood;
    private Runnable runGameLogic;
    private Handler runThread;
    public Pedometer pedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        loadGame();
        pedometer = new Pedometer(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void print(String txt) {
        Toast.makeText(MainActivity.this, txt, Toast.LENGTH_SHORT).show();
    }

    private void setupViews() {
        View thisView = findViewById(R.id.mainView);
        topMenu = new TopMenu(this, thisView);

        thisView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        //Log.d("DEBUG", "Action was DOWN");
                        if (touchTracker == null) {
                            touchTracker = VelocityTracker.obtain();
                        } else {
                            touchTracker.clear();
                        }
                        touchTracker.addMovement(event);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        //Log.d("DEBUG", "Action was MOVE");
                        touchTracker.addMovement(event);
                        touchTracker.computeCurrentVelocity(1000);
                        int pointerId = event.getPointerId(event.getActionIndex());
                        float x = VelocityTrackerCompat.getXVelocity(touchTracker, pointerId);
                        float y = VelocityTrackerCompat.getYVelocity(touchTracker, pointerId);

                        if (y >= 3000) {
                            topMenu.show();
                        } else if (y <= -3000) {
                            topMenu.hide();
                            if (kitten != null) {
                                barFood.setVisible(View.VISIBLE);
                            }
                        }
                        //Log.d("DEBUG", "X: " + x + ", Y: " + y);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //Log.d("DEBUG", "Action was UP");
                        break;
                    }
                }

                return true;
            }
        });

        barFood = new StatusBar(this, thisView);
        Global.addViewToParent(thisView, barFood, RelativeLayout.ALIGN_TOP, RelativeLayout.ALIGN_LEFT);
        barFood.setY(500);
        barFood.setHint("Food: ");
        barFood.setSuffix("h");
        barFood.setMaxValue(48);
        barFood.setValue(48);
        barFood.setVisible(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //Grab data
                Bundle data = intent.getExtras();

                //Clear previous kitten if any
                if (kitten != null) {
                    kitten.destroy();
                    kitten = null;
                }

                //Create kitten
                makeKitten(data, true);

                //Save to database
                saveKitten(kitten.getData());

                //Hide list
                topMenu.showList(View.INVISIBLE);
                print("You have adopted " + kitten.getName() + "!");
            }
        }
    }

    private void loadKitten() {
        String name = storedData.getString("kittenName", "");

        if (name != "") {
            Bundle data = new Bundle();
            data.putString("kittenName", storedData.getString("kittenName", ""));
            data.putInt("kittenBodyColor", storedData.getInt("kittenBodyColor", 0));
            data.putInt("kittenHeadColor", storedData.getInt("kittenHeadColor", 0));
            data.putInt("kittenBodyDecorColor", storedData.getInt("kittenBodyDecorColor", 0));
            data.putInt("kittenHeadDecorColor", storedData.getInt("kittenHeadDecorColor", 0));
            data.getFloat("kittenVoice", storedData.getFloat("kittenVoice", 1));
            data.putLong("kittenLastFedTime", storedData.getLong("kittenLastFedTime", 0));

            makeKitten(data, false);
        }
    }

    private void makeKitten(Bundle data, boolean feed) {
        View thisView = findViewById(R.id.mainView);
        kitten = new Kitten(this, thisView, data);
        kitten.show(true);
        if (feed) {
            kitten.feed();
        }

        barFood.setVisible(View.VISIBLE);
        topMenu.enableAdoption(false);
    }

    private void loadGame() {
        storedData = getSharedPreferences("LediiData", MODE_PRIVATE);
        loadKitten();

        if (kitten != null) {
            //setScene("home");
        }

        runThread = new Handler();
        runGameLogic = new Runnable() {
            @Override
            public void run() {
                gameUpdate();

                //Next tick
                runThread.post(runGameLogic);
            }
        };
        runThread.post(runGameLogic);
    }

    private void gameUpdate() {
        if (kitten != null) {
            int hoursLeft = barFood.getMaxValue() - kitten.hoursSinceLastFed();
            barFood.setValue(hoursLeft);

            if (hoursLeft < 0) {
                clearKitten();
            }
        }

        TextView txtSteps = (TextView) findViewById(R.id.txt_Steps);
        TextView txtCoins = (TextView) findViewById(R.id.txt_Coins);
        String t1 = "Steps: " + pedometer.getSteps() + "/" + pedometer.getMaxSteps();
        String t2 = "Coins: " + pedometer.getCoins() + " (Today: " + pedometer.getCoinsToday() + "/" + pedometer.getMaxCoins() + ")";
        if (txtSteps.getText() != t1) {
            txtSteps.setText(t1);
        }
        if (txtCoins.getText() != t2) {
            txtCoins.setText(t2);
        }

        if (pedometer.maxCoinsWarning()) {
            print("You have collected 10/10 coins today!");
        }
    }

    public void sceneUpdate(String scenario) {
        switch (scenario) {
            case "Home": {
                print("Scene set to Home!");
                break;
            }
            case "Run": {
                print("Running is not aviable yet!");
                break;
            }
            case "Shop": {
                /*
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                Bundle data = new Bundle();
                data.putInt("money", pedometer.getCoins());
                intent.putExtras(data);

                startActivity(intent);
                */
                if (kitten != null) {
                    int COST = 5;
                    if (pedometer.pay(COST)) {
                        kitten.feed();
                        saveKitten(kitten.getData());
                        print("You fed " + kitten.getName() + "!");
                    }
                    else {
                        print("Price: " + COST + " (You have: " + pedometer.getCoins() + ")");
                    }
                }
                break;
            }
            case "Play": {
                print("Playing is not aviable yet!");
                break;
            }
            case "List": {
                int listState = View.INVISIBLE;
                int barState = View.INVISIBLE;

                if (topMenu.getListVisibility() == View.INVISIBLE) {
                    listState = View.VISIBLE;
                }
                else if (kitten != null) {
                    barState = View.VISIBLE;
                }

                topMenu.showList(listState);
                barFood.setVisible(barState);
                break;
            }
            case "Adopt": {
                Intent intent = new Intent(MainActivity.this, AdoptActivity.class);
                startActivityForResult(intent, 0);
                break;
            }
            case "Statistics": {
                print("Statistics is not aviable yet!");
                break;
            }
            case "Options": {
                print("Options is not aviable yet!");
                break;
            }
        }
    }

    private void saveKitten(Bundle data) {
        SharedPreferences.Editor save = storedData.edit();

        save.putString("kittenName", data.getString("kittenName"));
        save.putInt("kittenBodyColor", data.getInt("kittenBodyColor"));
        save.putInt("kittenHeadColor", data.getInt("kittenHeadColor"));
        save.putInt("kittenBodyDecorColor", data.getInt("kittenBodyDecorColor"));
        save.putInt("kittenHeadDecorColor", data.getInt("kittenHeadDecorColor"));
        save.putFloat("kittenVoice", data.getFloat("kittenVoice"));
        save.putLong("kittenLastFedTime", data.getLong("kittenLastFedTime"));

        save.commit();
    }

    private void clearKitten() {
        if (kitten != null) {
            Bundle nullData = new Bundle();
            nullData.putString("kittenName", "");
            nullData.putInt("kittenBodyColor", 0);
            nullData.putInt("kittenHeadColor", 0);
            nullData.putInt("kittenBodyDecorColor", 0);
            nullData.putInt("kittenHeadDecorColor", 0);
            nullData.putFloat("kittenVoice", 0);
            nullData.putLong("kittenLastFedTime", 0);

            saveKitten(nullData);
            kitten.destroy();
            kitten = null;

            barFood.setVisible(View.INVISIBLE);
            topMenu.enableAdoption(true);
        }
    }
}
