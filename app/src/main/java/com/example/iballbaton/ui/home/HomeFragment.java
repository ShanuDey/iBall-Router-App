package com.example.iballbaton.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.iballbaton.R;
import com.example.iballbaton.StaticData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        HashMap<String, String> system_status = null;
        try {
            system_status = new LoginAsyncTask().execute(StaticData.URL_SYSTEM_STATUS).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v("shanu: Home: result=", system_status.get("wanIP"));
        return root;
    }

    private class LoginAsyncTask extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            try {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticData.SHRD_PREF_COOKIE, Context.MODE_PRIVATE);
                String cookieValue = sharedPreferences.getString(StaticData.COOKIE_NAME, null);

                Connection.Response response = Jsoup.connect(strings[0]) // Login Page URL
                        .method(Connection.Method.GET)
                        .cookie(StaticData.COOKIE_NAME, cookieValue)
                        .execute();

                Document document = response.parse();
//                Log.v("shanu",document.select("script[language=JavaScript][type$=text/javascript]").outerHtml());
                String[] data = document.select("script[language=JavaScript][type$=text/javascript]").outerHtml().split("\n");
                HashMap<String, String> system_status = new HashMap<>();
                for (int i = 6; i <= 22; i++) {
                    String[] pair = data[i].split("=");
                    system_status.put(pair[0].trim(), pair[1].trim());
                    Log.v("shanu", "Key=" + pair[0] + " Value=" + pair[1]);
                }
                return system_status;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}