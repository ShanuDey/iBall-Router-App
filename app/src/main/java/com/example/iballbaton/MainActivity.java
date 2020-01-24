package com.example.iballbaton;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Button btn_login;
    private TextInputEditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btn_login = findViewById(R.id.btn_login);
        et_password = findViewById(R.id.et_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loginAuth();
            }
        });

        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    loginAuth();
                    handled=true;
                }
                return handled;
            }
        });

    }

    private void loginAuth(){
        // get user input password
        String password = String.valueOf(et_password.getText());

        // validate user input password length <13
        if (password.length()>13){
            et_password.setError("Password length can not exceed 12 character");
        }

        // encode password
        String encodedPassword = new EncodePassword().str_encode(password);

        // auth in background task
        LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
        String result = null;
        try {
            result = loginAsyncTask.execute(StaticData.URL_LOGIN, encodedPassword).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v("shanu","result = "+result);

        // validate result
        Boolean auth = Boolean.parseBoolean(result);
        if(auth) {
            Toast.makeText(this, "Authentication Successfull", Toast.LENGTH_SHORT).show();
        } else{
            et_password.setError("Wrong Password! Try Again.");
        }
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

    private class LoginAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection.Response response = Jsoup.connect(strings[0]) // Login Page URL
                        .method(Connection.Method.GET)
                        .execute();

                Document document = response.parse();
                FormElement authForm = (FormElement) document.select("form[name=Auth]").first();
                Element inputPassword = authForm.select("input[name=Password]").first();
                inputPassword.val(strings[1]); // user input password
                Document document1 = authForm.submit().post();
                Log.v("shanu",document1.title());
                return "" + !document1.title().equals("LOGIN");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
