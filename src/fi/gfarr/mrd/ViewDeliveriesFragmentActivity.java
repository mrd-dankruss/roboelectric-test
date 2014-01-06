package fi.gfarr.mrd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import fi.gfarr.mrd.adapters.ScreenSlidePagerAdapter;
import fi.gfarr.mrd.widget.TabManager;

public class ViewDeliveriesFragmentActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	private ViewHolder holder;
	private View rootView;
	private TabHost mTabHost;
	private TabManager mTabManager;
	private int currentPage = 0;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	/**
	 * The number of pages (wizard steps) to show in this demo.
	 */
	private static final int NUM_PAGES = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_deliveries);
		initViewHolder(); // Inflate ViewHolder static instance

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.view_deliveries_pager);

		// Set pager to listen for page changes
		mPager.setOnPageChangeListener(this);

		mPagerAdapter = new ScreenSlidePagerAdapter(
				getSupportFragmentManager(), NUM_PAGES);
		mPager.setAdapter(mPagerAdapter);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, R.id.view_deliveries_pager);

		// Add tabs to tabhost
		setupTab(new TextView(this), getString(R.string.tab_completed));
		setupTab(new TextView(this), getString(R.string.tab_todo));
		setupTab(new TextView(this), getString(R.string.tab_unsuccessful));

		// set tabhost to listen for tab changes
		setCurrentPage(0);
		mTabHost.setOnTabChangedListener(this);
	}

	// Add tab to tabhost
	private void setupTab(final View view, final String tag) {

		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(new TabContentFactory() {

					public View createTabContent(String tag) {
						return view;
					}
				});

		mTabHost.addTab(setContent);

	}

	// Inflate custom tab (tabs_bg)
	private static View createTabView(final Context context, final String text) {

		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);

		TextView tv = (TextView) view.findViewById(R.id.tabsText);

		tv.setText(text);

		return view;

	}

	@Override
	public void onTabChanged(String tag) {
		final int pos = this.mTabHost.getCurrentTab();
		setCurrentPage(pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		this.mTabHost.setCurrentTab(arg0);
		currentPage = mPager.getCurrentItem();
	}

	public void setCurrentPage(int i) {
		currentPage = i;
		mPager.setCurrentItem(currentPage);
	}

	public void initViewHolder() {

		if (rootView == null) {

			rootView = this.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (holder == null) {
				holder = new ViewHolder();
			}

			// Store the holder with the view.
			rootView.setTag(holder);

		} else {
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null)
					&& (rootView.getParent() instanceof ViewGroup)) {
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			} else {
			}
		}
	}

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder {

	}
}
