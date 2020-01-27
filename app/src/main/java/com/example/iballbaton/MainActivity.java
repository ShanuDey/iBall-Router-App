package com.example.iballbaton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.LinkMovementMethod;
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

        SharedPreferences sharedPreferences = getSharedPreferences(StaticData.SHRD_PREF_COOKIE,MODE_PRIVATE);
        String cookie = sharedPreferences.getString(StaticData.COOKIE_NAME,null);
        if(cookie!=null){
            goDashboard();
        }

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
            // Show Toast for success
            Toast.makeText(this, "Authentication Successfull", Toast.LENGTH_SHORT).show();
            goDashboard();
        } else{
            et_password.setError("Wrong Password! Try Again.");
        }
    }
    public void goDashboard(){
        // redirect to dashboard
        Intent dashboard = new Intent(this,Dashboard.class);
        startActivity(dashboard);
        finish();
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
        if (id == R.id.action_about) {
            AlertDialog ad_About = new MaterialAlertDialogBuilder(this)
                    .setTitle("About")
                    .setView(R.layout.view_about)
                    .setCancelable(true)
                    .create();
            ad_About.show();

            // Make the textview clickable. Must be called after show()
            TextView tv_source = ad_About.findViewById(R.id.tv_source);
            tv_source.setMovementMethod(LinkMovementMethod.getInstance());

            ((TextView) ad_About.findViewById(R.id.tv_author)).setMovementMethod(LinkMovementMethod.getInstance());
            ((TextView) ad_About.findViewById(R.id.tv_donation)).setMovementMethod(LinkMovementMethod.getInstance());

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
                Connection.Response response1 = authForm.submit().execute().method(Connection.Method.POST);

                String cookieValue = response1.cookie(StaticData.COOKIE_NAME);
                String title = response1.parse().title();
                Log.v("shanu",title);

                // store cookie in shared preference for future use
                SharedPreferences sharedPreferences = getSharedPreferences(StaticData.SHRD_PREF_COOKIE,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(StaticData.COOKIE_NAME,cookieValue);
                editor.apply();

                return "" + !title.equals("LOGIN");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
