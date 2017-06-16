package com.klj.rollcall.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.klj.rollcall.R;

public class LoadingDialogFragment extends DialogFragment {

    private static final String PARAMS_MSG = "paramMsg";
    private static final String LOG_TAG = "LoadingDialogFragment";
    private ProgressBar mProgressWheel;
    private TextView mDes;
    private View mContainer;
    private TextView mResult;
    private View mProgressContainer;


    public LoadingDialogFragment() {
    }

    public static LoadingDialogFragment newInstance(String des) {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAMS_MSG, des);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_NoTitle);
        dialog.setContentView(R.layout.fragment_loading);
        dialog.setCanceledOnTouchOutside(false);
        mProgressWheel = (ProgressBar) dialog.findViewById(R.id.progress);
        mProgressContainer = dialog.findViewById(R.id.progressContainer);
        mDes = (TextView) dialog.findViewById(R.id.des);
        mContainer = dialog.findViewById(R.id.container);
        mResult = (TextView) dialog.findViewById(R.id.result);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String des = bundle.getString(PARAMS_MSG);
            if (des != null) {
                mDes.setText(des);
            }
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }


    public void setResult(String resultDes, final LoadingDialogListener listener) {
        mProgressContainer.setVisibility(View.INVISIBLE);
        mResult.setVisibility(View.VISIBLE);
        mResult.setText(resultDes);

        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(mContainer, "alpha", 1f, 1f, 0.3f);
        animatorAlpha.setDuration(1000);
        animatorAlpha.setInterpolator(new AccelerateInterpolator());
        animatorAlpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onAnimationFinish();
                }
            }
        });
        animatorAlpha.start();
    }

    public interface LoadingDialogListener {
        void onAnimationFinish();
    }
}