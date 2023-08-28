package com.apps2you.albaraka.utils.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Lifecycle;

public class ActivityNavigation {

    private final Context context;

    private FinishType finish;

    private Bundle arguments;
    private Bundle options;

    private Intent intent;

    private ActivityResultObserver<Intent, ActivityResult> activityResultObserver;

    public static ActivityNavigation create(Context context) {
        return new ActivityNavigation(context);
    }

    private ActivityNavigation(Context context) {
        this.context = context;
    }

    public ActivityNavigation setArguments(Bundle arguments) {
        this.arguments = arguments;
        return this;
    }

    public ActivityNavigation setOptions(Bundle options) {
        this.options = options;
        return this;
    }

    public ActivityNavigation finish() {
        this.finish = FinishType.NORMAL;
        return this;
    }

    public ActivityNavigation finishAffinity() {
        this.finish = FinishType.AFFINITY;
        return this;
    }

    public ActivityNavigation forResult(ActivityResultRegistry activityResultRegistry,
                                        Lifecycle lifecycle,
                                        String key) {
        this.activityResultObserver = new ActivityResultObserver<>(
                activityResultRegistry,
                new ActivityResultContracts.StartActivityForResult(),
                key
        );
        lifecycle.addObserver(activityResultObserver);
        return this;
    }


    public void navigate(Class<?> targetClass) {
        intent = new Intent(context, targetClass);
        navigate();
    }

    public void navigate(Intent intent) {
        this.intent = intent;
        navigate();
    }

    private void navigate() {
        if (arguments != null)
            intent.putExtras(arguments);

        if (finish == FinishType.NORMAL)
            ((Activity) context).finish();
        else if (finish == FinishType.AFFINITY)
            ((Activity) context).finishAffinity();

        context.startActivity(intent, options);
    }


    public void navigateForResult(Intent intent,
                                  ActivityResultObserver.OnActivityResult<ActivityResult> onActivityResult) {
        if (activityResultObserver != null){
            activityResultObserver.launch(intent, onActivityResult);
        }else {
            throw new IllegalStateException("ActivityResultHandler hasn't been initialed, you must have forgotten to call forResult");
        }
    }

    private enum FinishType {
        NORMAL, AFFINITY
    }
}
