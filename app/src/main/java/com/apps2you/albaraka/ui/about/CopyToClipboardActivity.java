package com.apps2you.albaraka.ui.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apps2you.albaraka.R;


public class CopyToClipboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        //  String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (uri != null) {
            copyTextToClipboard(uri.toString());
            Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
        }

        // Finish right away. We don't want to actually display a UI.
        finish();
    }

    private void copyTextToClipboard(String url) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", url);
        clipboard.setPrimaryClip(clip);
    }
}
