package net.ledii.kittyfit.kittyfit;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AdoptActivity extends AppCompatActivity {

    ImageButton btnBack, btnNext;
    private int catNum = 0;
    private int KITTENS = 5;
    private Kitten kittens[];
    Intent intent;
    EditText txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt);

        setupButtons();

        View thisView = findViewById(R.id.adoptView);
        kittens = new Kitten[KITTENS];
        for (int i = 0; i < KITTENS; i++) {
            kittens[i] = new Kitten(this, thisView);
            //kittens[i] = new Kitten(this, thisView, 7, 7, 0, 0);
        }
        displayKitten(catNum);

        intent = getIntent();
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

        txtName = (EditText) findViewById(R.id.adopt_txt_Name);

        Button btnAdopt = (Button) findViewById(R.id.adopt_btn_Adopt);
        btnAdopt.getBackground().setColorFilter(Global.COLOR_BUTTONS, PorterDuff.Mode.MULTIPLY);

        btnAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = txtName.getText().toString();
                //Log.d("DEBUG", "TxtName: '" + txt + "'");
                if (!txt.isEmpty()) {
                    kittens[catNum].setName(txtName.getText().toString());

                    Bundle catData = kittens[catNum].getData();
                    intent.putExtras(catData);
                    setResult(RESULT_OK, intent);

                    finish();
                }
                else {
                    print("You must name your kitten!");
                }
            }
        });
    }
}
