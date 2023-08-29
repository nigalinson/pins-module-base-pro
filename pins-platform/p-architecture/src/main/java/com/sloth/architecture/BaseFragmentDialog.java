package com.sloth.architecture;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class BaseFragmentDialog extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(dialogStyle(), dialogTheme());
    }


    protected abstract int dialogStyle();

    protected abstract int dialogTheme();

    protected abstract int getFragmentLayoutRes();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.beforeSetContentView(savedInstanceState);
        return inflater.inflate(getFragmentLayoutRes(), container, false);
    }

    protected void beforeSetContentView(Bundle savedInstanceState) { }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adjustWindow();
    }

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener != null){
            onDismissListener.onDismiss(dialog);
        }
    }

    private void adjustWindow() {
        if(getContext() == null){ return; }
        Context context = getContext();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.setCancelable(windowCancelable());
            dialog.setCanceledOnTouchOutside(windowCancelable());

            Window window = dialog.getWindow();
            if(window != null){
                window.setBackgroundDrawable(backgroundDrawable());
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = windowWidth();
                lp.height = windowHeight();
                lp.dimAmount = windowDim();
                window.setAttributes(lp);
            }

        }
    }

    protected boolean windowCancelable() {
        return false;
    }

    protected Drawable backgroundDrawable() {
        return new ColorDrawable(Color.TRANSPARENT);
    }

    protected int windowWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int windowHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected float windowDim() {
        return 0.6f;
    }

}
