package com.apps2you.albaraka.utils.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

final public class ImagesUtil {

    private static String PICTURES_FOLDER = "images";

    private ImagesUtil() {
    }

    public interface OnLoadBitmap {
        void onSuccess(@Nullable Bitmap bitmap);
    }

    public static void loadPictureAsBitmap(Context context, String url, OnLoadBitmap onLoadBitmap) {
        Glide.with(context).asBitmap()
                .load(url)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        onLoadBitmap.onSuccess(null);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        onLoadBitmap.onSuccess(resource);
                        return true;
                    }
                }).submit();
    }

    private static RequestBuilder<Drawable> getRequestBuilder(ImageView imageView, String url, Drawable placeHolder, boolean cache) {
        RequestBuilder<Drawable> requestBuilder;

        requestBuilder = Glide.with(imageView.getContext()).load(url);

        if (placeHolder != null)
            requestBuilder = requestBuilder.placeholder(placeHolder);

        requestBuilder = requestBuilder.diskCacheStrategy(cache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE);
        return requestBuilder;
    }

    /**
     * This function just will load picture from url
     **/
    public static void loadPicture(ImageView imageView, String url, Drawable placeHolder, boolean cache) {
        if (imageView == null)
            throw new RuntimeException("Null imageView");
        if (url == null || url.isEmpty()) {
            loadPlaceHolder(imageView, placeHolder);
            return;
        }

        getRequestBuilder(imageView, url, placeHolder, cache).into(imageView);
    }


    public static void loadPictureAndCache(ImageView imageView, String url, Drawable placeHolder) {
        loadPicture(imageView, url, placeHolder, true);
    }

    public static void loadPictureAndCache(ImageView imageView, String url) {
        loadPictureAndCache(imageView, url, null);
    }

    private static void loadPlaceHolder(ImageView imageView, Drawable placeHolder) {
        if (placeHolder == null)
            imageView.setImageDrawable(null);
        else
            imageView.setImageDrawable(placeHolder);

    }


    public static void loadPictureFromFile(ImageView imageView, File file) {
        Glide.with(imageView.getContext())
                .load(file)
                .into(imageView);
    }

    /**
     * @param url : is remote url
     *            <p>
     *            This function will load the picture from network :
     *            if success : will save the picture in local storage
     *            if failed : will try to load picture from local storage
     **/
    public static void loadPictureAndStore(Context context, ImageView imageView, String url) {
        if (url.isEmpty())
            return;
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        String picturePath = getPicturesFolder(context) + "/" + getFileNameFromUrl(url);
                        loadPictureFromFile(imageView, new File(picturePath));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        String savedPath = getPicturesFolder(context) + "/" + getFileNameFromUrl(url);
                        savePictureOnStorage(imageView, savedPath);
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void savePictureOnStorage(ImageView imageView, String fullPath) {
        new Thread() {
            @Override
            public void run() {
                if (isInterrupted()) {
                    return;
                }
                File file = new File(fullPath);
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(imageViewToByteArray(imageView));
                    fileOutputStream.close();
                    System.out.println("file saved in " + fullPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("error when save the file in = " + fullPath);
                }


            }
        }.start();
    }

    public static void loadPictureFromFile(ImageView imageView, String filePath) {
        loadPictureFromFile(imageView, new File(filePath));
    }

    public static String getPicturesFolder(Context context) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + PICTURES_FOLDER);
        file.mkdirs();
        return file.getAbsolutePath();
    }

    public static String getFileNameFromUrl(String url) {
        String[] array = url.split("/");
        return array[array.length - 1];

    }

    public static byte[] imageViewToByteArray(ImageView imageView) {
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static byte[] bitmapToByteStream(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return Base64.encode(stream.toByteArray(), Base64.NO_WRAP);
    }

    public static Uri getImageUriFromBitmap(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }
}
