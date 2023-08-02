package com.app.gmv3;

public class Config {
    //PRODUCCION
    //public static final String ADMIN_PANEL_URL = "http://186.1.15.164:8448/gmv3";
    //public static final String API_COMMISIOENS = "http://186.1.15.164:8448/SAC";

    public static final String SERVER = "devs"; //devs or apps
    public static final String ADMIN_PANEL_URL = "https://" + SERVER + ".gumacorp.com/gmv3";
    public static final String API_COMMISIOENS = "https://" + SERVER + ".gumacorp.com/SAC";

    public static final String URL_S3 = "http://186.1.15.171:8448/SAC";

    //set false if you want price to be displayed in decimal
    public static final boolean ENABLE_DECIMAL_ROUNDING = false;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //splash screen duration in milliseconds
    public static final int SPLASH_TIME = 3000;

}