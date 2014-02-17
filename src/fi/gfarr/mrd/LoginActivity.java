package fi.gfarr.mrd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import fi.gfarr.mrd.fragments.LoginFragment;

public class LoginActivity extends FragmentActivity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_login_container);
		if (fragment == null)
		{
			fragment = new LoginFragment();
			fm.beginTransaction().add(R.id.activity_login_container, fragment).commit();
		}
	}
	
}
