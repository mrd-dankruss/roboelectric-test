package com.mrdexpress.paperless.workflow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.net.NetworkStatus;

/**
 * Created by gary on 2014/04/23.
 */
public class CheckConnectivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_connection);

        View root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);
        Button button_retry = (Button) root_view.findViewById(R.id.button_connectivity_retry);

        NetworkStatus.getInstance().addCallback( new CallBackFunction() {
            @Override
            public boolean execute(Object args) {
                finish();
                return true;
            }
        });

        button_retry.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}