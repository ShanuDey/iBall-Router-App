package com.example.iballbaton.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alespero.expandablecardview.ExpandableCardView;
import com.example.iballbaton.Dashboard;
import com.example.iballbaton.MainActivity;
import com.example.iballbaton.R;
import com.example.iballbaton.StaticData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private Context context;
    private AlertDialog alertDialog;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        HashMap<String, String> system_status = null;
        try {
            system_status = new LoginAsyncTask().execute(StaticData.URL_SYSTEM_STATUS).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        context = root.getContext();

        if(system_status==null){
            Toast.makeText(context, "WiFi is not connected", Toast.LENGTH_SHORT).show();
            showAlert();
            return root;
        }

        ExpandableCardView card_wan_status =  root.findViewById(R.id.wan_status);

        // some static value
        String[] connectionType = new String[]{"Static IP","Dynamic IP","PPPoE", "PPTP", "L2TP"};
        String[] connectionState = new String[]{"Disconnected","Connecting","Connected"};

        // Fill wan status card_wan_status view's text data
        ((TextView) card_wan_status.findViewById(R.id.tv_connection_status)).setText(
                connectionState[
                        Integer.parseInt(
                                cleanUp(
                                    system_status.get("cableDSL")
                                )
                        )
                ]
        );
        ((TextView) card_wan_status.findViewById(R.id.tv_wanIP)).setText(cleanUp(system_status.get("wanIP")));
        ((TextView) card_wan_status.findViewById(R.id.tv_subnetMask)).setText(cleanUp(system_status.get("subMask")));
        ((TextView) card_wan_status.findViewById(R.id.tv_gateway)).setText(cleanUp(system_status.get("gateWay")));
        ((TextView) card_wan_status.findViewById(R.id.tv_primaryDNS)).setText(cleanUp(system_status.get("dns1")));
        ((TextView) card_wan_status.findViewById(R.id.tv_secondaryDNS)).setText(cleanUp(system_status.get("dns2")));
        ((TextView) card_wan_status.findViewById(R.id.tv_connectionType)).setText(
                connectionType[
                        Integer.parseInt(
                                cleanUp(
                                        system_status.get("conTypeIdx")
                                )
                        )-1 // array starts from 0
                ]
        );

        ExpandableCardView card_system_status = root.findViewById(R.id.system_status);

        ((TextView) card_system_status.findViewById(R.id.tv_lan_mac_addr)).setText(cleanUp(system_status.get("lan_mac")));
        ((TextView) card_system_status.findViewById(R.id.tv_wan_mac_addr)).setText(cleanUp(system_status.get("wan_mac")));
        ((TextView) card_system_status.findViewById(R.id.tv_system_time)).setText(cleanUp(system_status.get("systime")));
        ((TextView) card_system_status.findViewById(R.id.tv_running_time)).setText(timeStr(cleanUp(system_status.get("uptime"))));
        ((TextView) card_system_status.findViewById(R.id.tv_connected_client)).setText(cleanUp(system_status.get("clients")));
        ((TextView) card_system_status.findViewById(R.id.tv_software_version)).setText(cleanUp(system_status.get("run_code_ver")));
        ((TextView) card_system_status.findViewById(R.id.tv_hardware_version)).setText(cleanUp(system_status.get("hw_ver")));

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog!=null){
            alertDialog.dismiss();
        }
    }

    public void showAlert(){
        alertDialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Alert")
                .setMessage("Opps! WiFi Disconnected. Check Router Connection.")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        getActivity().recreate();
                    }
                })
                .create();
        alertDialog.show();
    }

    private String cleanUp(String s){
        int lenght = s.length();
        return s.substring(1,lenght-2);
    }

    private String timeStr(String time) {
        long s,m,h,d;
        long t = Long.parseLong(time);
        String str = new String();
        if(t < 0)
        {
            str="00:00:00";
            return str;
        }
        s=t%60;
        m=(t/60)%60;
        h=(t/3600)%24;
        d=(t/86400);
        if (d > 999) {
            return "Permanent";
        }
        if (d!=0) {
            str+=d+"Day";
        }
        str+=fit2(h)+':';
        str+=fit2(m)+':';
        str+=fit2(s);
        return str;
    }

    private String fit2(Long n){
        String s=(""+n+100).substring(0,2);
        return s;
    }

    private class LoginAsyncTask extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            try {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticData.SHRD_PREF_COOKIE, MODE_PRIVATE);
                String cookieValue = sharedPreferences.getString(StaticData.COOKIE_NAME, null);

                Connection.Response response = Jsoup.connect(strings[0]) // Login Page URL
                        .method(Connection.Method.GET)
                        .cookie(StaticData.COOKIE_NAME, cookieValue)
                        .execute();

                Document document = response.parse();
//                Log.v("shanu",document.select("script[language=JavaScript][type$=text/javascript]").outerHtml());
                String[] data = document.select("script[language=JavaScript][type$=text/javascript]").outerHtml().split("\n");
                HashMap<String, String> system_status = new HashMap<>();
                for (int i = 5; i <= 22; i++) {
                    try{
                        String[] pair = data[i].split("=");
                        system_status.put(pair[0].trim(), pair[1].trim());
//                        Log.v("shanu", "Key=" + pair[0] + " Value=" + pair[1]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                        logout();
                        return null;
                    }
                }
                return system_status;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void logout() {
        // Remove cookie
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticData.SHRD_PREF_COOKIE,MODE_PRIVATE);
        sharedPreferences.edit().remove(StaticData.COOKIE_NAME).apply();

        // Loading Login Activity
        startActivity(new Intent(getActivity(),MainActivity.class));
        getActivity().finish();
    }

}