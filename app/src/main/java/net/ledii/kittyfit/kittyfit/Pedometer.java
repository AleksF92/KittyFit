package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Calendar;

public class Pedometer {
    private static int COINS_MAX_DAILY = 10;
    private static int STEPS_PER_COIN = 10;
    private int coins, coinsToday, steps, stepsLastCoin, lastStepDay;
    private SharedPreferences storedData;
    private Context parent;
    private SensorManager sm;
    private Sensor stepSensor;
    private boolean notifyMax = false;

    Pedometer(Context context) {
        parent = context;
        loadData();

        sm = (SensorManager) parent.getSystemService(parent.SENSOR_SERVICE);
        stepSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                    countStep();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, stepSensor, 0);
    }

    private void loadData() {
        storedData = parent.getSharedPreferences("LediiData", parent.MODE_PRIVATE);

        coins = storedData.getInt("pedometerCoins", 0);
        checkForDayReset();
        //resetData();
    }

    private void checkForDayReset() {
        lastStepDay = storedData.getInt("pedometerLastStepDay", 0);

        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int d = cal.get(Calendar.DAY_OF_YEAR);
        int day = (y * 1000) + d;

        if (day - lastStepDay > 0) {
            //Coins collected last day
            SharedPreferences.Editor save = storedData.edit();
            save.putInt("pedometerCoinsToday", 0);
            save.putInt("pedometerSteps", 0);
            save.putInt("pedometerStepsLastCoin", 0);
            save.commit();
        }

        coinsToday = storedData.getInt("pedometerCoinsToday", 0);
        steps = storedData.getInt("pedometerSteps", 0);
        stepsLastCoin = storedData.getInt("pedometerStepsLastCoin", 0);
    }

    private void countStep() {
        checkForDayReset();
        steps++;

        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int d = cal.get(Calendar.DAY_OF_YEAR);
        lastStepDay = (y * 1000) + d;

        SharedPreferences.Editor save = storedData.edit();
        save.putInt("pedometerSteps", steps);
        save.putInt("pedometerLastStepDay", lastStepDay);

        if (steps >= stepsLastCoin + STEPS_PER_COIN) {
            stepsLastCoin = stepsLastCoin + STEPS_PER_COIN;
            save.putInt("pedometerStepsLastCoin", stepsLastCoin);

            if (coinsToday < COINS_MAX_DAILY) {
                coins++;
                coinsToday++;
                save.putInt("pedometerCoins", coins);
                save.putInt("pedometerCoinsToday", coinsToday);

                if (coinsToday == COINS_MAX_DAILY) {
                    notifyMax = true;
                }
            }
        }

        save.commit();
        Log.d("DEBUG", "Pedometer update! (Coins: " + coins + ", CoinsToday: " + coinsToday + ", Steps: " + steps + ", LastStepDay: " + lastStepDay + ")");
    }

    private void resetData() {
        coins = 0;
        coinsToday = 0;
        steps = 0;
        stepsLastCoin = 0;
        lastStepDay = 0;

        SharedPreferences.Editor save = storedData.edit();
        save.putInt("pedometerCoins", coins);
        save.putInt("pedometerCoinsToday", coinsToday);
        save.putInt("pedometerSteps", steps);
        save.putInt("pedometerStepsLastCoin", stepsLastCoin);
        save.putInt("pedometerLastStepDay", lastStepDay);
        save.commit();
    }

    public int getCoins() {
        return coins;
    }

    public boolean pay(int price) {
        boolean paid = false;
        if (coins >= price) {
            paid = true;
            coins -= price;
        }
        return paid;
    }

    public boolean maxCoinsWarning() {
        boolean result = notifyMax;
        notifyMax = false;
        return result;
    }

    public int getMaxSteps() {
        return COINS_MAX_DAILY * STEPS_PER_COIN;
    }

    public int getMaxCoins() {
        return COINS_MAX_DAILY;
    }

    public int getSteps() {
        return steps;
    }

    public int getCoinsToday() {
        return coinsToday;
    }
}
