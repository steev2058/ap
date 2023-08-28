package com.apps2you.albaraka.utils.navigation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

public class ActivityResultObserver<Input, Result> implements DefaultLifecycleObserver {

    private final String key;

    private final ActivityResultContract<Input, Result> activityResultContract;

    private final ActivityResultRegistry activityResultRegistry;

    private OnActivityResult<Result> onActivityResult;

    private ActivityResultLauncher<Input> activityResultLauncher;


    public ActivityResultObserver(ActivityResultRegistry activityResultRegistry,
                                  ActivityResultContract<Input, Result> activityResultContract,
                                  String key) {
        this.activityResultRegistry = activityResultRegistry;
        this.activityResultContract = activityResultContract;
        this.key = key;
    }

    @Override
    public void onCreate(@NonNull @NotNull LifecycleOwner owner) {
        activityResultLauncher =
                activityResultRegistry.register(key, owner, activityResultContract, this::callOnActivityResult);
    }

    public void launch(Input input,
                       @Nullable OnActivityResult<Result> onActivityResult) {
        if (onActivityResult != null) {
            this.onActivityResult = onActivityResult;
        }

        activityResultLauncher.launch(input);
    }


    private void callOnActivityResult(Result result) {
        if (onActivityResult != null) onActivityResult.onActivityResult(result);
    }


    public interface OnActivityResult<O> {
        /**
         * Called after receiving a result from the target activity
         */
        void onActivityResult(O result);
    }

}