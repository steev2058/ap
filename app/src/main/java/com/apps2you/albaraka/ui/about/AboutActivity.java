package com.apps2you.albaraka.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityAboutBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.AboutViewModel;


public class AboutActivity extends BaseActivity<ActivityAboutBinding, AboutViewModel> {


    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public Class<AboutViewModel> setViewModel() {
        return AboutViewModel.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.About_us));
    }

    @Override
    public void fetchData() {
        getViewModel().getAboutModel().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                    break;

                case ERROR:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    showToast(resource.message);
                    break;

                case SUCCESS:
                    assert resource.data != null;
                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                    Spanned contentText;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                        contentText = Html.fromHtml(resource.data.getContent(), Html.FROM_HTML_MODE_LEGACY);
                    else
                        contentText = Html.fromHtml(resource.data.getContent());
                    getViewDataBinding().contentTv.setText(contentText);
                    getViewDataBinding().contentTv.setClickable(true);
                    getViewDataBinding().contentTv.setMovementMethod(LinkMovementMethod.getInstance());

                    getViewDataBinding().contactsCv.setVisibility(View.VISIBLE);

                    getViewDataBinding().facebookIb.setOnClickListener(view -> btnFacebook(resource.data.getFacebook_link(), resource.data.getFacebook_page_id()));
                    getViewDataBinding().linkedinIb.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(resource.data.getLinkedin_link()));
                        startActivity(intent);
                    });
                    getViewDataBinding().websiteIb.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(resource.data.getWebsite_link()))));
                    getViewDataBinding().telegramIb.setOnClickListener(view -> openTelegram(resource.data.getTelegram_link()));
                    getViewDataBinding().instaIb.setOnClickListener(view -> openInstagram(resource.data.getInstagram_link()));
                    getViewDataBinding().shareBtn.setOnClickListener(view -> shareMessage(resource.data.getShareText()));
                    break;
            }
        });
    }

    private void openTelegram(String telegram_link) {
        try {
           Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("org.telegram.messenger");
            intent.setData(Uri.parse(telegram_link));
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(telegram_link)));
        }

    }

    private void openInstagram(String instagram_link) {
        Uri uri = Uri.parse(instagram_link);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.setPackage("com.instagram.android");

        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private void shareMessage(String msg) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, msg);

        // when using generic type "text/*" Facebook responded with an error
        intent.setType("text/plain");

        Intent clipboardIntent = new Intent(AboutActivity.this, CopyToClipboardActivity.class);
        clipboardIntent.setData(Uri.parse(msg));

        Intent shareIntent = Intent.createChooser(intent, null);
        shareIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{clipboardIntent});
        startActivity(shareIntent);
    }

    @Override
    public void listenToVariables() {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void btnFacebook(String url, String pageNumber) {
        try {
            Intent intent = getOpenFacebookIntent(AboutActivity.this, url, pageNumber);
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    private Intent getOpenFacebookIntent(Context context, String url, String pageId) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/".concat(pageId)));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }
    }
}
