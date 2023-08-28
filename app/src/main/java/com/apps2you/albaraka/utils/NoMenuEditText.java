package com.apps2you.albaraka.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatEditText;


public class NoMenuEditText extends AppCompatEditText {

    public NoMenuEditText(Context context) {
        super(context);
        init();
    }

    public NoMenuEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoMenuEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * This is a replacement method for the base TextView class' method of the same name. This method
     * is used in hidden class android.widget.Editor to determine whether the PASTE/REPLACE popup
     * appears when triggered from the text insertion handle. Returning false forces this window
     * to never appear.
     *
     * @return false
     */
    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    @Override
    public int getSelectionStart() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getMethodName().equals("canPaste")
                    || element.getMethodName().equals("canCopy")
                    || element.getMethodName().equals("canCut")
                    || element.getMethodName().equals("canShare")
                    || element.getMethodName().equals("canSelectAllText")) {
                return -1;
            }
        }

        return super.getSelectionStart();
    }

    @Override
    public void selectAll() {
        // Do nothing
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return false;
    }

    private void init() {
        cancelClipBoardContent();

        this.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());

        this.setLongClickable(false);
        this.setTextIsSelectable(false);
    }

    private void cancelClipBoardContent() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
            ClipData clip = ClipData.newPlainText("", "");
            clipboard.setPrimaryClip(clip);
        }
    }


    /**
     * Prevents the action bar (top horizontal bar with cut, copy, paste, etc.) from appearing
     * by intercepting the callback that would cause it to be created, and returning false.
     */
    private static class ActionModeCallbackInterceptor implements ActionMode.Callback {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            return false;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}
