package com.apps2you.albaraka.utils.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;

import androidx.fragment.app.Fragment;

final public class NavigationUtil {

    public static void openNotificationsSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

        // For Android 5-7
        intent.putExtra("app_package", activity.getPackageName());
        intent.putExtra("app_uid", activity.getApplicationInfo().uid);

        // For Android 8 and above
        intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());

        activity.startActivity(intent);
    }

    public static void openLocationSettings(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openLocationSettings(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openGooglePlay(Activity activity) {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static Intent pickContactIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        return intent;
    }

}
