package com.mrdexpress.paperless;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mrdexpress.paperless.adapters.TabsPagerAdapter;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.fragments.ChangeUserDialog;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.helper.VariableManager;

public class ViewDeliveriesFragmentActivity extends FragmentActivity implements TabListener

{
	private final String TAG = "ViewDeliveriesFragmentActivity";
	private ViewHolder holder;
	private View rootView;
	private int currentPage = 0;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private TabsPagerAdapter mPagerAdapter;

	/**
	 * The action bar, where the tabs will be placed.
	 */
	private ActionBar actionBar;

	/**
	 * The number of pages (wizard steps) to show in this demo.
	 */
	private static final int NUM_PAGES = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_deliveries);

		String[] tabs =	{ getResources().getString(R.string.tab_todo),
                            getResources().getString(R.string.tab_completed),
                            getResources().getString(R.string.tab_partial),
                            getResources().getString(R.string.tab_unsuccessful) };

		// Initilization
		mPager = (ViewPager) findViewById(R.id.view_deliveries_pager);
		actionBar = getActionBar();
		mPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		mPager.setAdapter(mPagerAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs)
		{
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
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
		mPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Log.d(TAG, "go Home");
                Intent intent = MiscHelper.getGoHomeIntent(ViewDeliveriesFragmentActivity.this);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                Log.d(TAG, "Logout");
                setupChangeUserDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void initViewHolder()
	{

		if (rootView == null)
		{

			rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			// Store the holder with the view.
			rootView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
			{
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			}
			else
			{
			}
		}
	}
	
	@Override
    public void onBackPressed() 
	{
		// Do not allow going back
		return;
    }
	
	
	/**
	 * display change user dialog
	 * TODO basically duplicate of code on ScanActivity -- recommend create project activity superclass that include common funcs like this OR make static to helper method
	 */
    private void setupChangeUserDialog() {
    	final ChangeUserDialog dialog_change_user = new ChangeUserDialog(this);
        dialog_change_user.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_change_user.show();

        // LayoutInflater factory = LayoutInflater.from(ScanActivity.this);

        final ImageButton button_close = (ImageButton) dialog_change_user.findViewById(R.id.button_change_user_closeButton);
        final Button button_cancel = (Button) dialog_change_user.findViewById(R.id.button_change_user_cancel);
        final Button button_ok = (Button) dialog_change_user.findViewById(R.id.button_change_user_ok);
        final TextView dialog_content = (TextView) dialog_change_user.findViewById(R.id.text_change_driver_content);

        SharedPreferences prefs = this.getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
        String driverId = prefs.getString(VariableManager.PREF_DRIVERID, "");
        String user_name = "";
        if (MiscHelper.isNonEmptyString(driverId))
        {
        	user_name = DbHandler.getInstance(this).getDriverName(driverId);
        }
        dialog_content.setText("Are you sure you want to log out" + ((MiscHelper.isNonEmptyString(user_name) ? (" "+user_name) : "")) + "?");

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
                Intent intent = new Intent(ViewDeliveriesFragmentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{

	}
}
