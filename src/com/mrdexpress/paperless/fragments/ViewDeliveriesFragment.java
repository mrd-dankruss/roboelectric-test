package com.mrdexpress.paperless.fragments;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.TabsPagerAdapter;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.fragments.ChangeUserDialog;
import com.mrdexpress.paperless.widget.CustomToast;

public class ViewDeliveriesFragment extends Fragment implements TabListener{
	private final String TAG = "TabViewDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	private int currentPage = 0;

	private TabsPagerAdapter mPagerAdapter;
	private static final int NUM_PAGES = 4;

    public interface ViewDeliveriesFragmentInterface{
        public void viewDeliveriesDone();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Paperless.getInstance().setMainActivity(this.getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        initViewHolder(inflater, container);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //holder.actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD);
        //holder.actionBar.removeAllTabs();
        //holder.actionBar.hide();
       // holder.mPager.setAdapter(null);

        ((ViewDeliveriesFragmentInterface)getActivity()).viewDeliveriesDone();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] tabs =	{ getResources().getString(R.string.tab_todo),
                getResources().getString(R.string.tab_completed),
                getResources().getString(R.string.tab_partial),
                getResources().getString(R.string.tab_unsuccessful) };

        // Initilization
        mPagerAdapter = new TabsPagerAdapter(getFragmentManager());

        holder.mPager.setAdapter(mPagerAdapter);
        holder.actionBar.setHomeButtonEnabled(false);
        holder.actionBar.setDisplayHomeAsUpEnabled(false);
        holder.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        holder.actionBar.show();

        // Adding Tabs
        for (String tab_name : tabs)
        {
            holder.actionBar.addTab( holder.actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        holder.mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position)
            {
                // on changing the page
                // make respected tab selected
                holder.actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
            }
        });

        if (!Users.getInstance().getMilkrunactive()){
            CustomToast toast = new CustomToast(getActivity());
            toast.setSuccess(true);
            toast.setText("Delivery run started.");
            toast.show();
            Users.getInstance().setMilkrunactive(true);
        }

        holder.mPager.setCurrentItem(0);
    }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		// on tab selected
		// show respected fragment view
        holder.mPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
	}
	
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }*/
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_home:
                Log.d(TAG, "go Home");
                Intent intent = MiscHelper.getGoHomeIntent(TabViewDeliveriesFragment.this);
                startActivity(intent);
                return true;*/
            case R.id.action_logout:
                Log.d(TAG, "Logout");
                setupChangeUserDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container){
		if (rootView == null)
		{
            rootView = inflater.inflate(R.layout.activity_view_deliveries, container, false);

			if (holder == null)
				holder = new ViewHolder();
			rootView.setTag(holder);

            holder.mPager = (ViewPager) rootView.findViewById(R.id.view_deliveries_pager);
            holder.actionBar = getActivity().getActionBar();

		}
		else
		{
			holder = (ViewHolder) rootView.getTag();
			if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
		}
	}

    private void setupChangeUserDialog() {
    	final ChangeUserDialog dialog_change_user = new ChangeUserDialog(getActivity());
        dialog_change_user.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_change_user.show();

        // LayoutInflater factory = LayoutInflater.from(ScanFragment.this);

        final ImageButton button_close = (ImageButton) dialog_change_user.findViewById(R.id.button_change_user_closeButton);
        final Button button_cancel = (Button) dialog_change_user.findViewById(R.id.button_change_user_cancel);
        final Button button_ok = (Button) dialog_change_user.findViewById(R.id.button_change_user_ok);
        final TextView dialog_content = (TextView) dialog_change_user.findViewById(R.id.text_change_driver_content);

        String user_name = Users.getInstance().getActiveDriver().getFullName();

        dialog_content.setText("Are you sure you want to log out" + user_name + "?");

        button_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_change_user.dismiss();
            }
        });

        button_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_change_user.dismiss();
            }
        });

        button_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(TabViewDeliveriesFragment.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //ServerInterface.getInstance().endMilkrun(Users.getInstance().getActiveDriver().getStringid());
                startActivity(intent);*/
            }
        });
    }

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
        private ViewPager mPager;
        private ActionBar actionBar;
	}
}
