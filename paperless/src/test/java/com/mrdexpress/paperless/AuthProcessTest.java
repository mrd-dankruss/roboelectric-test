package com.mrdexpress.paperless;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.fest.assertions.api.ANDROID.assertThat;
import static org.robolectric.Robolectric.shadowOf;

/**
 * Created by dan on 2014/06/09.
 */
@Config(reportSdk = 18, emulateSdk = 18, qualifiers = "v10")
@RunWith(RobolectricGradleTestRunner.class)
public class AuthProcessTest {

    private LoginActivity activity;

    private ConnectivityManager connectivityManager;
    private ShadowNetworkInfo shadowOfActiveNetworkInfo;
    private ShadowConnectivityManager shadowConnectivityManager;

    @Before
    public void setup() throws Exception {
//        activity = Robolectric.buildActivity(LoginActivity.class).create().start().resume().get();

        //TODO: Load shadow connectivity manager to simulate network connections
        connectivityManager = (ConnectivityManager) Robolectric.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        shadowConnectivityManager = shadowOf(connectivityManager);
        shadowOfActiveNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());

//        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).create().get();
//        ShadowActivity shadowActivity = Robolectric.shadowOf(loginActivity);
    }

    @Test
    public void getConnectivityManagerShouldNotBeNull() {
        assertNotNull(connectivityManager);
        assertNotNull(connectivityManager.getActiveNetworkInfo());
    }

    @Test
    public void networkInfoShouldReturnTrueCorrectly() {
        shadowOfActiveNetworkInfo.setConnectionStatus(true);

        assertTrue(connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting());
        assertTrue(connectivityManager.getActiveNetworkInfo().isConnected());
    }
//
    @Test
    public void UserInterfaceLoadTest() throws Exception {
        //Activity loaded
//        assertThat(activity).isNotNull();

        //UI elements loaded
//        EditText editText = (EditText) activity.findViewById(R.id.text_mainmenu_password);
//        assertThat(editText).isNotNull();

//        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) activity.findViewById(R.id.text_mainmenu_name);
//        assertThat(autoCompleteTextView).isNotNull();

//        Button button = (Button) activity.findViewById(R.id.button_mainmenu_start_login);
//        assertThat(button).isNotNull();
    }

}
