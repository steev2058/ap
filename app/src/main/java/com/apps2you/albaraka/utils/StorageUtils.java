package com.apps2you.albaraka.utils;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.apps2you.albaraka.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;

public class StorageUtils {

    public boolean saveToFile(Context context, ResponseBody body, String prefix) {

        CustomFile customFile = getCustomFile(context, prefix);
        if (customFile == null)
            return false;
        else {
            try {
                customFile.outputStream.write(body.bytes());
                customFile.outputStream.close();

                openFile(context, customFile.uri);
            } catch (IOException | ActivityNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public boolean saveToFile(Context context, PdfDocument document, String prefix) {

        CustomFile customFile = getCustomFile(context, prefix);
        if (customFile == null)
            return false;
        else {
            try {
                document.writeTo(customFile.outputStream);
                document.close();
                customFile.outputStream.close();

                openFile(context, customFile.uri);
            } catch (IOException | ActivityNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private CustomFile getCustomFile(Context context, String prefix) {
        String currentDate = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.ENGLISH)
                .format(new Date());
        final String fileName = prefix + currentDate + ".pdf";
        OutputStream outputStream;
        Uri uri;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, ".pdf");

                final ContentResolver resolver = context.getContentResolver();
                uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                outputStream = resolver.openOutputStream(uri);

            } else {
                File directoryDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                if (!directoryDocuments.exists())
                    directoryDocuments.mkdir();

                File directoryAlBaraka = new File(directoryDocuments + "/Al Baraka Accounts");
                if (!directoryAlBaraka.exists())
                    directoryAlBaraka.mkdir();

                File file = new File(directoryAlBaraka + File.separator + fileName);
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
                outputStream = new FileOutputStream(file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return new CustomFile(outputStream, uri);
    }

    private void openFile(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setDataAndType(uri, "application/pdf");
        context.startActivity(intent);
    }
}

class CustomFile {
    OutputStream outputStream;
    Uri uri;

    CustomFile(OutputStream outputStream, Uri uri) {
        this.outputStream = outputStream;
        this.uri = uri;
    }
}
