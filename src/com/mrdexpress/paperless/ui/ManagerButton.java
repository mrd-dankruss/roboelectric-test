package com.mrdexpress.paperless.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import com.mrdexpress.paperless.fragments.LoginFragment;
import com.mrdexpress.paperless.interfaces.CallBackFunction;

/**
 * Created by gary on 2014/03/31.
 */
public class ManagerButton extends Button{
    private OnClickListener OnAuthOnClickListener;
    Context myContext;

    public ManagerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
    }

    @Override
    public void setOnClickListener( final OnClickListener l) {
        super.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment dialog;
                final View theView = v;
                Activity pa = (Activity)myContext;
                dialog = new LoginFragment( pa);
                dialog.callback = new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        l.onClick( theView);
                        return false;
                    }
                };
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }


}
