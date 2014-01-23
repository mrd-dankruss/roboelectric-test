package fi.gfarr.mrd.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import fi.gfarr.mrd.R;


public class TrafficTimeDelayDialog extends DialogFragment {
    int mNum;
    
    private Button delayButton_5min;
	private Button delayButton_10min;
    private Button delayButton_20min;
    private Button delayButton_30min;
    private Button delayButton_1hour;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static TrafficTimeDelayDialog newInstance(int num) {
    	TrafficTimeDelayDialog f = new TrafficTimeDelayDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_traffic_delay, container, false);
        
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        ImageButton closeDialogButton = (ImageButton) v.findViewById(R.id.button_trafficDelay_closeButton);
        delayButton_5min = (Button) v.findViewById(R.id.button_trafficDelay_5min);
        delayButton_10min = (Button) v.findViewById(R.id.button_trafficDelay_10min);
        delayButton_20min = (Button) v.findViewById(R.id.button_trafficDelay_20min);
        delayButton_30min = (Button) v.findViewById(R.id.button_trafficDelay_30min);
        delayButton_1hour = (Button) v.findViewById(R.id.button_trafficDelay_1hour);
        
        closeDialogButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
        
        delayButton_5min.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = getActivity().getIntent();
				intent.putExtra("SOMETHING", "5 min");
				getActivity().setResult(1, intent);
				dismiss();
			}
		});
        
        delayButton_10min.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO: View Map button
			}
		});
        
        delayButton_20min.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO: Change to new activity
				
			}
		});
        
        delayButton_30min.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO: Call button
			}
		});
        
        delayButton_1hour.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO: Message button
			}
		});

        return v;
    }
    
    public Button getDelayButton_5min()
	{
		return delayButton_5min;
	}

	public Button getDelayButton_10min()
	{
		return delayButton_10min;
	}

	public Button getDelayButton_20min()
	{
		return delayButton_20min;
	}

	public Button getDelayButton_30min()
	{
		return delayButton_30min;
	}

	public Button getDelayButton_1hour()
	{
		return delayButton_1hour;
	}
}