package com.angopapo.datoo.app;


public class Config {


    // Parse Server
    public static final String SERVER_URL = "https://parse.server.url/";
    public static final String LIVE_QUERY_URL = "ws://live.query.url";
    static final String SERVER_API_KEY = "HYCScpeir3rgUpfrrggsPTpjYPQVqSvGDHS";
    static final String SERVER_CLIENT_KEY = "DF4YRDEDZbOIrggfsJDssDFfdfdUJ56F";

    // Push notifications
    public static final String CHANNEL = "global";

    // Agora API
    public static final String AGORA_APP_ID = "0d493144900a47498h876ff5bc5eab03";

    // Instagram API
    public static final String INSTAGRAM_APP_ID = "21117044325198873";
    public static final String INSTAGRAM_APP_SECRET = "8c56be0545gg41afh7d9a8a9a4b31900";
    public static final String INSTAGRAM_REDIRECT_URI = "https://www.angopapo.com/";

    // Google Admob
    static final String HOME_BANNER_ADS = "ca-app-pub-1084112649181796/1781127328";
    static final String REWARDED_ADS= "ca-app-pub-1084112649181796/1990038550";

    // Credits needed to activate features
    public static int TYPE_RISE_UP = 50;
    public static int TYPE_GET_MORE_VISITS = 100;
    public static int TYPE_ADD_EXTRA_SHOWS = 100;
    public static int TYPE_SHOW_IM_ONLINE = 100;
    public static int TYPE_3X_POPULAR = 200;

    // Amount of days to activate features
    public static int DAYS_TO_ACTIVATE_FEATURES = 7;

    // Google Play In-app Purchases IDs
    public static final String CREDIT_550 = "datoo.550.credits";
    public static final String CREDIT_100 = "datoo.100.credits";
    public static final String CREDIT_1250 = "datoo.1250.credits";
    public static final String CREDIT_2750 = "datoo.2750.credits";

    public static final String PAY_LIFETIME = "datoo.pay.lifetime";

    //Google Play In-app Subscription IDs
    public static final String SUBS_3_MONTHS = "datoo.3.months";
    public static final String SUBS_1_WEEK = "datoo.1.week";
    public static final String SUBS_1_MONTH = "datoo.1.month";
    public static final String SUBS_6_MONTHS = "datoo.6.months";

    // Web links for help, privacy policy and terms of use.
    public static final String HELP_CENTER = "https://datoo.angopapo.com/help.html";
    public static final String PRIVACY_POLICY = "https://datoo.angopapo.com/privacy.html";
    public static final String TERMS_OF_USE = "https://datoo.angopapo.com/terms.html";

    // Enable or Disable Ads and Premium.
    public static final boolean isAdsActivated = true;
    public static final boolean isPremiumEnabled = true;

    // Application setup
    public static final String bio = "Hey! i'm using datoo!";
    public static final int WelcomeCredit = 10;
    public static final int MinimumAgeToRegister = 18;
    public static final int MaximumAgeToRegister = 80;
    public static final int MaxUsersNearToShow = 50;
    public static final double DistanceForRealBadge = 0.9;
    public static final double DistanceForRealKm = 2.1;
    public static final double DistanceBetweenUsers = 999999999;
    public static final double DistanceBetweenUsersLive = 1000;
    public static final boolean ShowBlockedUsersOnQuery = true;
}