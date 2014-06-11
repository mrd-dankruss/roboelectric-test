package com.mrdexpress.paperless;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.fragments.ManagerHomeFragment;

public class ManagerHomeActivity extends Activity {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Fragment: Home Begin
        FragmentManager fm = getFragmentManager();
        fragment = fm.findFragmentById(R.id.activity_manager_container);
        if (fragment == null) {
            fragment = new ManagerHomeFragment();
            fm.beginTransaction().add(R.id.activity_manager_container, fragment).commit();
        }
        // Fragment: Home End
    }

}
