package com.mrdexpress.paperless.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.GenericDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.workflow.Workflow;

/**
 * Created by gary on 2014-04-30.
 */
public class DriverReturnDialogFragment extends DialogFragment {
    private CallBackFunction callback;

    public DriverReturnDialogFragment(CallBackFunction callback) {
        this.callback = callback;
    }

    public static DriverReturnDialogFragment newInstance( CallBackFunction c)
    {
        DriverReturnDialogFragment f = new DriverReturnDialogFragment(c);

		/*// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);*/

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_return_to_branch, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button button = (Button)v.findViewById(R.id.return_to_branch_button);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.execute(true);
            }
        });

        return v;
    }
}
