package com.example.iballbaton;

public class StaticData {
    public static final String URL_HOST = "http://192.168.1.1/";
    public static final String URL_LOGIN = URL_HOST + "login.asp";
    public static final String URL_SYSTEM_STATUS = URL_HOST+ "system_status.asp";
    public static final String URL_TRAFFIC_STATISTICS  = URL_HOST+ "goform/updateIptAccount";
    public static final String URL_ENABLE_BANDWIDTH_CONTROL = URL_HOST+ "goform/trafficForm?GO=net_tc.asp&up_Band=12800&down_Band=12800&cur_number=2&tc_list_1=80,103,103,0,1,100,1,1&tc_list_2=80,103,103,1,1,100,1,1&tc_enable=";


    public static final String COOKIE_NAME = "ecos_pw";
    public static final String SHRD_PREF_COOKIE = "LoginAuth";
}
