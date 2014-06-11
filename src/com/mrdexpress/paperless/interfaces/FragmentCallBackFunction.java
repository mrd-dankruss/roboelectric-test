package com.mrdexpress.paperless.interfaces;

import android.content.Intent;

/**
 * Created by gary on 2014/03/31.
 */
public abstract class FragmentCallBackFunction {
    public abstract boolean onFragmentResult( int requestCode, int resultCode, Intent data);
}
