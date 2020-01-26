package com.example.iballbaton.ui.traffic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.iballbaton.R;
import com.example.iballbaton.StaticData;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TrafficFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_traffic,container,false);

        new BackgroudAsyncTask().execute(StaticData.URL_TRAFFIC_STATISTICS);

        return root;
    }


    private class BackgroudAsyncTask extends AsyncTask<String, String, HashMap<String, String>> {
        private OkHttpClient client = new OkHttpClient();
        private MediaType JSON =MediaType.get("application/json; charset=utf-8");

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticData.SHRD_PREF_COOKIE, Context.MODE_PRIVATE);
            String cookie = StaticData.COOKIE_NAME+"="+sharedPreferences.getString(StaticData.COOKIE_NAME, null);
            String result="";
            try {
                result = makeRequest(strings[0],cookie,"something");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("shanu: result",result);


            return null;
        }
        private String makeRequest(String url,String cookie, String json) throws IOException {
            RequestBody body = RequestBody.create(json,JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Cookie",cookie)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }
    }
}
