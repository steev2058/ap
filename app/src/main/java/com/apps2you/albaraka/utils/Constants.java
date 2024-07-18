package com.apps2you.albaraka.utils;

public class Constants {
    // these are constants in API
    public static final String COLOR_RED = "RED",
            COLOR_ORANGE = "ORANGE",
            COLOR_SILVER = "SILVER",
            COLOR_PURPLE = "PURPLE",
            COLOR_PINK = "PINK",
            COLOR_GREEN = "GREEN",
            COLOR_BLUE = "BLUE",
            TYPE_BRANCH = "branch", // types of branches that are display in LocationsActivity
            TYPE_ATM = "atm",
            TYPE_POS = "POS",
            GENDER_MALE = "male",
            GENDER_FEMALE = "female";

    // test link
  public static String BASE_URL = "Dapz/9upWXEoKkswz0bIqOPt34RWfWu9HRrfhk3MB6TH+AgrU1sFMg1PEzU9sF8XMUBHfhT43/VrfcDwh7e5j4JTc99bn1yQMAh3o4YZ6BjCxoITog==";

    // live link
  // public static String BASE_URL = "RLTWlkMoTClzcij5FhztackcT0FhNy6tBKB/DwqOpJgYSVNA47xPT5FTjPuRKpXGnGu3N1ApQcH87GcjWwnJCkGh6uLEG+1Ei5FV89JS9i9ong==";
    public static String BASE_URL_SEP = "http://epaytest.albaraka.com.sy:4433/SEP";
    public static String BASE_URL_SEP_ICON = "http://epaytest.albaraka.com.sy:4433";
    //public static String BASE_URL_SEP = "https://epay.albaraka.com.sy/SEP";

    //public static String BASE_URL_SEP_ICON = "https://epay.albaraka.com.sy";

    public static String LINK_CREATE_ACCOUNT = "rvhV2HLplH95BCLFyBP7c6YTi/eBTAvYigwT1Hd9dioGrdApqLIlcFymRkPzUFC3V2aH6zAkEFpg48GZ7btdFp/xQIu67DoFodvq",
            LINK_FINANCING = "9VQwaXIdzDKzBy1cGnCKLWIuh1Ov5uchEwo4YSEWHBsQTE2jTa48UuErehNzaiWWuzQ3FO2P7N78v8/VkS0gNQcpzaKYisHjACTB5pPsQZ0lufxRYZ35FcuBZEg=",
            LINK_ORDER_ATM = "qRc9nVeyQzH9m/nWRhxlfF19kOoANC86p+SsUjrl3dcmfGmTYVloPA9iiXZByaTuVmwiWUmKBcaJyYXMacI3P1EI6GLR48wPsztvSg==";

    public static final int TRANSFER = 1,
            ZAKAT = 2,
            SADAKA = 3,
            UNIVERSITIES = 4,
            SCHOOLS = 5,
            MOBILE_PAYMENT = 6,
            ADSL = 7, // quick services ids
            RESTAURANTS = 8,

            SEP = 9,
            ALPHA_CAPITAL = 10,
            ATM_CARDS = 11,

            SYGS = 12;



    public static final int TRANSFER_AL_BARAKA = 1,
            TRANSFER_MY_TRANSFER = 2,
            TRANSFER_SADAKA = 3,
            TRANSFER_ZAKAT = 4,
            TRANSFER_RESTAURANT = 5,
            TRANSFER_UNIVERSITY = 6,
            TRANSFER_SCHOOL = 7,
            TRANSFER_QR = 8,
            TRANSFER_ADSL = 9,
            TRANSFER_ALPHA = 11,

            TRANSFER_SEP = 16;

    public static final int NOTIFICATION_NORMAL = 1,
            NOTIFICATION_TRANSFER = 2; // types of notifications


    //Confirmation dialog action types
    public static final int LOGOUT_CONFIRMATION_ACTION_TYPE = 1;
    public static final int BIOMETRIC_AUTH_ACTION_TYPE = 2;
    public static final int LANGUAGE_SELECTION_ACTION_TYPE = 3;
    public static final int NOTIFICATIONS_ACTION_TYPE = 4;

    //navigation types
    public static final int NAVIGATION_FROM_SETTINGS_TO_RESET_PIN = 1;
    public static final int NAVIGATION_FROM_SETTINGS_TO_RESET_PASS = 3;

    public static final String ARG_CHANGE_PIN_CODE = "arg_change_pin_code";

    public static final String POST_PAID = "postpaid";

    //fcm topic
    public static final String FCM_TOPIC = "android_test";
    // TODO: 8/28/2022 Uncomment the live topic
//    public static final String FCM_TOPIC = "android"; // Live Topic
}
