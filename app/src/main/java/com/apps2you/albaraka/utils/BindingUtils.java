package com.apps2you.albaraka.utils;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.exception.ExceptionDrawableFactory;
import com.apps2you.albaraka.ui.exception.ExceptionMessageFactory;
import com.apps2you.albaraka.ui.exception.ExceptionTitleFactory;
import com.apps2you.albaraka.utils.images.ImagesUtil;
import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;


public class BindingUtils {

    @BindingAdapter("android:visibility")
    public static void viewVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter("existence")
    public static void viewExistence(View view, boolean isExists) {
        view.setVisibility(isExists ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("requestFocus")
    public static void requestFocus(View view, boolean focus) {
        if (focus) {
            view.requestFocus();
        }
    }

    @BindingAdapter("dateTZ")
    public static void setDateTZ(TextView textView, String date) {
        formatDate(textView, date, "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", "dd/MM/yyyy hh:mm aa");
    }

    @BindingAdapter("dateWYearTZ")
    public static void setDateWYearTZ(TextView textView, String date) {
        formatDate(textView, date, "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy", true);
    }

    @BindingAdapter("expiryDate")
    public static void setExpiryDate(TextView textView, String date) {
        formatDate(textView, date, "dd_MM_yyyy", "dd/MM/yyyy");
    }

    @BindingAdapter("formattedNum")
    public static void setFormattedNum(TextView textView, BigDecimal number) {
        if (number == null) return;

        textView.setText(formatNumber(number));
    }

    public static String formatNumber(BigDecimal number) {
        if (number == null) return null;

        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        return formatter.format(number);
    }

    public static String formatNumber(String number) {
        if (number == null) return null;
        return formatNumber(new BigDecimal(number));
    }



    @BindingAdapter("account_background")
    public static void setViewBackground(ConstraintLayout constraintLayout, String color) {
        if (color == null) return;

        switch (color) {
            case Constants.COLOR_RED:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_red);
                break;
            case Constants.COLOR_BLUE:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_blue);
                break;
            case Constants.COLOR_SILVER:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_silver);
                break;
            case Constants.COLOR_PINK:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_pink);
                break;
            case Constants.COLOR_PURPLE:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_purple);
                break;
            case Constants.COLOR_ORANGE:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_orange);
                break;
            case Constants.COLOR_GREEN:
                constraintLayout.setBackgroundResource(R.drawable.bg_account_spinner_green);
                break;
        }
    }

    @BindingAdapter("bgColor")
    public static void setBgColor(ImageView imageView, String color) {
        int resource = 0;
        switch (color) {
            case Constants.COLOR_RED:
                resource = R.drawable.bg_account_red;
                break;
            case Constants.COLOR_BLUE:
                resource = R.drawable.bg_account_blue;
                break;
            case Constants.COLOR_SILVER:
                resource = R.drawable.bg_account_silver;
                break;
            case Constants.COLOR_PINK:
                resource = R.drawable.bg_account_pink;
                break;
            case Constants.COLOR_PURPLE:
                resource = R.drawable.bg_account_purple;
                break;
            case Constants.COLOR_ORANGE:
                resource = R.drawable.bg_account_orange;
                break;
            case Constants.COLOR_GREEN:
                resource = R.drawable.bg_account_green;
                break;
        }
        imageView.setImageResource(resource);
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        if (url == null) return;
//        if (!TextUtils.isEmpty(url)) {
        Glide.with(imageView.getContext())
                .load(Constants.BASE_URL + "/" + url)
                .into(imageView);
//        }
    }

    @BindingAdapter("imageUrlDef") // sets default image
    public static void setImageUrlDef(ImageView imageView, String url) {
        if (url == null) return;
        Glide.with(imageView.getContext())
                .load(Constants.BASE_URL + "/" + url)
                .placeholder(R.drawable.ic_albaraka_logo)
                .error(R.drawable.ic_albaraka_logo)

                .into(imageView);
    }

    @BindingAdapter("imageUrlDefLogo")
    public static void setImageUrlDefLogo(ImageView imageView, String url) {
        if (url == null) return;
        Glide.with(imageView.getContext())
                .load(Constants.BASE_URL + "/" + url)
                .placeholder(R.drawable.ic_services_default_img)
                .error(R.drawable.ic_services_default_img)
                .into(imageView);
    }

    @BindingAdapter(value = {"android:src", "placeHolder"}, requireAll = false)
    public static void loadImage(ImageView imageView, String url, Drawable placeHolder) {
        if (url == null) return;
        ImagesUtil.loadPictureAndCache(
                imageView,
                Constants.BASE_URL + "/" + url,
                placeHolder
        );
    }

    @BindingAdapter("app:goneUnless")
    public static void goneUnless(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("fontOfLanguage")
    public static void setFontDependingOnLanguage(TextView textView, String text) {
        Typeface typeface = ResourcesCompat.getFont(textView.getContext(), isTextContainsArabic(text) ? R.font.tahoma : R.font.trebuc);
        textView.setTypeface(typeface);
    }

    @BindingAdapter("titleFontOfLanguage")
    public static void setTitleFontDependingOnLanguage(TextView textView, String text) {
        Typeface typeface = ResourcesCompat.getFont(textView.getContext(), isTextContainsArabic(text) ? R.font.tahoma : R.font.trebuc);
        textView.setTypeface(typeface, Typeface.BOLD);
    }

    public static boolean isTextContainsArabic(String text) {
        for (char charac : text.toCharArray()) {
            if (Character.UnicodeBlock.of(charac) == Character.UnicodeBlock.ARABIC) {
                return true;
            }
        }
        return false;
    }

    private static int getServiceIconResource(int id) {
        int resource = 0;
        switch (id) {
            case Constants.TRANSFER:
                resource = R.drawable.ic_transfer;
                break;
            case Constants.BILLS:
                resource = R.drawable.ic_sep;
                break;
            case Constants.ZAKAT:
                resource = R.drawable.ic_hand;
                break;
            case Constants.SADAKA:
                resource = R.drawable.ic_solidarity;
                break;
            case Constants.UNIVERSITIES:
                resource = R.drawable.ic_mortarboard;
                break;
            case Constants.SCHOOLS:
                resource = R.drawable.ic_school;
                break;
            case Constants.MOBILE_PAYMENT:
                resource = R.drawable.ic_mobile_phone;
                break;
            case Constants.ADSL:
                resource = R.drawable.ic_wifi;
                break;
            case Constants.RESTAURANTS:
                resource = R.drawable.ic_fork;
                break;
            case Constants.ALPHA_CAPITAL:
                resource = R.drawable.ic_alpha_capital;
                break;
            case Constants.ATM_CARDS:
                resource = R.drawable.ic_my_cards;
                break;
            case Constants.SYGS:
                resource = R.drawable.ic_bank_transfer;
                break;
            case Constants.HF:
                resource = R.drawable.ic_services;
                break;
        }
        return resource;
    }

    @BindingAdapter("serviceImage")
    public static void setServiceImage(ImageView imageView, int id) {
        int resource = getServiceIconResource(id);
        if (resource == 0) return;
        imageView.setImageResource(resource);
//        Drawable drawable = ContextCompat.getDrawable(MyApplication.getAppContext(), resource);
//        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
    }

    @BindingAdapter("serviceIcon")
    public static void setServiceIcon(TextView textView, int id) {
        int resource = getServiceIconResource(id);
        if (resource == 0) return;
        Drawable drawable = ContextCompat.getDrawable(MyApplication.getAppContext(), resource);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
    }

    @BindingAdapter("notificationImage")
    public static void setNotificationImage(ImageView imageView, int type) {
        int resource = 0;
        switch (type) {
            case Constants.NOTIFICATION_NORMAL:
                resource = R.drawable.ic_albaraka_logo;
                break;
            case Constants.NOTIFICATION_TRANSFER:
                resource = R.drawable.ic_notification_transfer;
                break;
        }
        if (resource == 0) return;
        imageView.setImageResource(resource);
    }


    @BindingAdapter("errorTitle")
    public static void setErrorTitle(TextView textView, Exception e) {
        if (e != null) {
            if (e.getMessage() != null) {
                textView.setText(e.getMessage());
            } else {
                textView.setText(ExceptionTitleFactory.getStringResOf(e));
            }
        }
    }

    @BindingAdapter("errorMessage")
    public static void setErrorMessage(TextView textView, Exception e) {
        if (e != null) {
            if (e.getMessage() != null) {
                textView.setText(e.getMessage());
            } else {
                textView.setText(ExceptionMessageFactory.getStringResOf(e));
            }
        }
    }

    @BindingAdapter("errorSrc")
    public static void setError(ImageView imageView, Exception e) {
        if (e != null) {
            imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            imageView.getContext().getResources(),
                            ExceptionDrawableFactory.getDrawableResOf(e),
                            null
                    )
            );
        }
    }

    private static void formatDate(TextView textView, String date, String serverFormat, String displayFormat){
        formatDate(textView, date, serverFormat, displayFormat, false);
    }

    private static void formatDate(TextView textView, String date, String serverFormat, String displayFormat, boolean isUTC) {
        SimpleDateFormat formatter = new SimpleDateFormat(serverFormat, Locale.ENGLISH);
        if (isUTC) {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat(displayFormat, Locale.ENGLISH);
        dateFormatter.setTimeZone(TimeZone.getDefault());

        try {
            if (date != null) textView.setText(dateFormatter.format(formatter.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
