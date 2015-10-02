package net.ledii.kittyfit.kittyfit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Kitten kitten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
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

    private void setupButtons() {
        Button btnAdopt = (Button) findViewById(R.id.btn_Adopt);
        Button btnOptions = (Button) findViewById(R.id.btn_Options);

        btnAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //print("You clicked the button!");
                Intent intent = new Intent(MainActivity.this, AdoptActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print("Options is not aviable yet!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //print("Activity finished!");

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //print("Result was OK!");

                Bundle data = intent.getExtras();
                String name = data.getString("name");
                int bCol = data.getInt("bodyColor");
                int hCol = data.getInt("headColor");
                int bdCol = data.getInt("bodyDecorColor");
                int hdCol = data.getInt("headDecorColor");

                if (kitten != null) {
                    kitten.destroy();
                    kitten = null;
                }

                View thisView = findViewById(R.id.mainView);
                kitten = new Kitten(this, thisView, bCol, hCol, bdCol, hdCol);
                kitten.setName(name);
                kitten.show(true);

                print("You have adopted " + kitten.getName() + "!");
            }
        }
    }
}
