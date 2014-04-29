package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.fragments.UnauthorizedUseDialog;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.LoginInterface;
import com.mrdexpress.paperless.net.NetworkStatus;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.service.LocationService;
import com.mrdexpress.paperless.service.PaperlessService;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.CheckConnectivity;
import com.squareup.otto.Subscribe;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginActivity extends Activity implements LoginInterface {
    private ViewHolder holder;
    private View root_view;
    private LoginActivity globalthis = this;
    private final String TAG = "LoginActivity";
    private ArrayList<Users.UserData> person_item_list;
    private Users.UserData selected_user;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String SENDER_ID = "426772637351";
    static final int ACTIVITY_CHECK_CONNECTIVITY = 1001001;
    private GoogleCloudMessaging gcm;
    private String regid;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    private boolean is_registration_successful;
    public ProgressDialog dialog_main;
    public ProgressDialog dialog_progress;
    public Dialog dialog_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Paperless.getInstance().setMainActivity(this);
        initViewHolder();

        setTitle(R.string.title_actionbar_mainmenu);

        dialog_main = new ProgressDialog( this );
        dialog_progress = new ProgressDialog(this);
        dialog_file = new Dialog(this);

        NetworkStatus.getInstance().register(context);
        NetworkStatus.getInstance().AddAndEnableWifiNetwork("MRDELIVERY","3EWruHam", 1, true);

        checkConnected();
    }


    @Subscribe public void mygcm(Bundle extra){
        if (!extra.isEmpty()){
            String test123 = extra.getString("data");
            Device.getInstance().displayInfo(test123);
            //Toast.makeText(getApplicationContext() , extra.getString("data") , Toast.LENGTH_LONG);

        }
    }

    private void checkConnected(){

        if( NetworkStatus.getInstance().connected()){
            String token = ServerInterface.getInstance(null).requestToken( new CallBackFunction() {
                @Override
                public boolean execute( Object args) {
                    if (args != null){
                        ServerInterface.getInstance(null).getUsers( new CallBackFunction() {
                            @Override
                            public boolean execute( Object args) {
                                afterSetup();
                                //new UpdateApp().execute();
                                return true;
                            }
                        });
                    } else {
                        UnauthorizedUseDialog diag = new UnauthorizedUseDialog(LoginActivity.this);
                        diag.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        diag.setOnCancelListener( new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                LoginActivity.this.finish();
                            }
                        });
                        diag.show();
                    }
                    return true;
                }
            });
        } else {
            startActivityForResult(new Intent(this, CheckConnectivity.class), ACTIVITY_CHECK_CONNECTIVITY);
            //displayToast("Device is offline");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == ACTIVITY_CHECK_CONNECTIVITY){
            checkConnected();
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                return true;
        }
        return super.onKeyDown(keycode, e);
    }

    private void afterSetup()
    {
        person_item_list = Users.getInstance().driversList;

        UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter(getApplicationContext(), person_item_list);

        // Set the adapter
        holder.text_name.setAdapter(adapter);
        holder.text_name.setThreshold(1);

        holder.text_name.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selected_user = ((Users.UserData) holder.text_name.getAdapter().getItem(position));
                Users.getInstance().setActiveDriverIndex(Users.getInstance().driversList.indexOf(selected_user));
                try {
                    if ( selected_user.getdriverPin() == null ){
                        //do something here
                        Intent intent = new Intent(getApplicationContext(), CreatePinActivity.class);
                        startActivity(intent);
                    }else{
                        holder.text_password.requestFocus();
                    }
                }catch(Exception e){
                    Log.e("MRD-EX" , e.getMessage());
                }
            }
        });

        AQuery ac = new AQuery(root_view);
        ac.id(R.id.button_mainmenu_start_login).progress(dialog_main).clicked(this, "triggerLogin");

        startService(new Intent(this, LocationService.class));
        startService(new Intent(this , PaperlessService.class));

        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance( this);
            regid = getRegistrationId( this);
            is_registration_successful = false;
            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        Device.getInstance().displayInfo("Checking for updates");

        String updateURL = "http://www.mrdexpress.com/updates/Paperless/UpdateDescriptor.xml";
        ac.ajax(updateURL , String.class , new AjaxCallback<String>(){
            @Override
            public void callback(String urll, String json, AjaxStatus status) {
                String callstatus = null;
                String path = "/sdcard/paperless";
                final PackageInfo pInfo;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    double versionCode = 0;
                    String url = "";
                    XPath xpath = XPathFactory.newInstance().newXPath();
                    try {
                        InputSource xml = new InputSource(new StringReader(json));
                        String updateURL = "http://www.mrdexpress.com/updates/Paperless/UpdateDescriptor.xml";
                        versionCode = (Double)xpath.evaluate("/update/versionCode", xml , XPathConstants.NUMBER);
                        xml = new InputSource(new StringReader(json));
                        final String versionpath = (String)xpath.evaluate("/update/url", xml, XPathConstants.STRING);
                        final String urlpath = path + versionCode + ".apk";
                        if (versionCode > pInfo.versionCode) {
                            try {
                                Toast.makeText(getBaseContext() , "Downloading an update , please be patient" , Toast.LENGTH_SHORT).show();
                                url = versionpath;
                                File ext = Environment.getExternalStorageDirectory();
                                File target = new File(ext, "paperless.apk");
                                AQuery aq = new AQuery(root_view);

                                dialog_progress.setTitle("Please wait.");
                                dialog_progress.setMessage("Downloading update.");
                                dialog_progress.setIndeterminate(true);
                                dialog_progress.setCancelable(false);
                                dialog_progress.setInverseBackgroundForced(false);
                                dialog_progress.setCanceledOnTouchOutside(false);

                                aq.progress(dialog_progress).download(url, target, new AjaxCallback<File>(){
                                    public void callback(String url, File file, AjaxStatus status) {
                                        if(file != null){
                                            //showResult("File:" + file.length() + ":" + file, status);
                                            Intent i = new Intent();
                                            i.setAction(Intent.ACTION_VIEW);
                                            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                            startActivity(i);
                                        }else{
                                            //showResult("Failed", status);
                                            Toast.makeText(getBaseContext() , "Update failed , please contact MRD." , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }catch(Exception e){
                                Log.e("MRD-EX" , e.getMessage());
                            }
                        }
                    } catch (XPathExpressionException e) {
                        e.printStackTrace();
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void triggerLogin(View view){
        if (checkPin() && selected_user != null) {
            if ( selected_user.getdriverPin() == null ){
                //Cant login yet , needs to create PIN
                Intent intent = new Intent(getApplicationContext(), CreatePinActivity.class);
                startActivity(intent);
            } else {
                loginUser(selected_user.getUsertype());
            }
        }
    }

    public void showD(){

    }

    private void loginUser(Users.Type type) {
        dialog_main.setMessage("Logging you in " + Users.getInstance().getActiveDriver().getFullName() + " please be patient");
        dialog_main.show();
        ServerInterface.getInstance(getApplicationContext()).authDriver(holder.text_password.getText().toString(), this);
    }

    @Override
    protected void onPause() {
        //NetworkStatus.getInstance().unregister();
        super.onPause();
    }

    /* (non-Javadoc)
         * @see android.app.Activity#onResume()
         */
    @Override
    protected void onResume() {
        SharedPreferences prefs = getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();

        //NetworkStatus.getInstance().register(context);
        super.onResume();
    }

    /**
     * Trigger Login Action
     */

    @Override
    public void onLoginComplete(Paperless.PaperlessStatus result){
        //
        dialog_main.dismiss();
        if (result == Paperless.PaperlessStatus.FAILED){
            CustomToast toast = new CustomToast(LoginActivity.this);
            toast.setText(getString(R.string.text_unauthorised));
            toast.setSuccess(false);
            toast.show();
        } else if (result == Paperless.PaperlessStatus.SUCCESS){
            Intent intent = new Intent(getApplicationContext(), DriverHomeActivity.class);
            startActivity(intent);
        } else if (result == Paperless.PaperlessStatus.SPECIAL){
            Intent intent = new Intent(getApplicationContext(), CreatePinActivity.class);
            //this.finish();
            startActivity(intent);
        }
    }
 


    /**
     * Check PIN's validity (data validation)
     *
     * @return True is valid.
     */
    private boolean checkPin() {
        // Check for 4-digit format
        String msg = PinManager.checkPin(holder.text_password.getText().toString(), this);
        if (msg.equals("OK")) {
            return true;
        } else {
            displayToast(msg);
            return false;
        }
    }


    /**
     * Display a toast using the custom Toaster class
     *
     * @param msg
     */
    private void displayToast(String msg) {
        CustomToast toast_main_menu = new CustomToast(this);
        toast_main_menu.setSuccess(false);
        toast_main_menu.setText(msg);
        toast_main_menu.show();
    }

    private void displayCustomToast(String msg) {
        CustomToast toast_main_menu = new CustomToast(this);
        toast_main_menu.setSuccess(false);
        toast_main_menu.setText(msg);
        toast_main_menu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = Device.getInstance().getGCMGOOGLEID();
        if (registrationId == null || registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.i(TAG, "GCM registration ID: " + regid);
                    msg = "Device registered, registration ID=" + regid;
                    Device.getInstance().setGCMGOOGLEID(regid);
                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                /*if (is_registration_successful) {
                    CustomToast toast = new CustomToast(LoginActivity.this);
                    toast.setText("Sending device registration ID successful!");
                    toast.setSuccess(true);
                    toast.show();
                } else {
                    CustomToast toast = new CustomToast(LoginActivity.this);
                    toast.setText("Sending device registration ID failed!");
                    toast.setSuccess(false);
                    toast.show();
                }*/
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regid) {
        ServerInterface.getInstance(getApplicationContext()).registerDeviceGCM(regid);
        is_registration_successful = true;
        int appVersion = getAppVersion(context);
        Device.getInstance().setAppVersion(appVersion);
    }


    private class UpdateApp extends AsyncTask<Void, Void, Void> {
        String path = "/sdcard/paperless-mrd.apk";
        boolean mustInstall = false;


        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
            Toast.makeText(getBaseContext() , "Checking for updates" , Toast.LENGTH_LONG).show();
            try {
                // delete the original file
                new File(path).delete();
            }
            catch (Exception e) {
                Log.e("MRD-EX", e.getMessage());
            }
        }

        @Override
        protected Void doInBackground(Void... urls) {

        final PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                double versionCode = 0;
                String url = "";
                XPath xpath = XPathFactory.newInstance().newXPath();
                try {
                    String updateURL = "http://www.mrdexpress.com/updates/Paperless/UpdateDescriptor.xml";
                    versionCode = (Double)xpath.evaluate("/update/versionCode", new InputSource( updateURL), XPathConstants.NUMBER);
                    url = (String)xpath.evaluate("/update/url", new InputSource( updateURL), XPathConstants.STRING);

                    if (versionCode > pInfo.versionCode) {
                        try {
                            Toast.makeText(getBaseContext() , "Downloading an update , please be patient" , Toast.LENGTH_LONG).show();
                            downloadAPK( url , path) ;

                        }catch(Exception e){
                            Log.e("MRD-EX" , e.getMessage());
                        }
                        mustInstall = true;
                    }
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (mustInstall) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                startActivity(i);
            }
        }
    }

    private void downloadAPK( String Url , String path) {
        try {

            try {
                // delete the original file
                new File(path).delete();
            }
            catch (Exception e) {
                Log.e("MRD-EX", e.getMessage());
            }

            URL url = new URL(Url);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                //pb.setProgress( (int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            startActivity(i);


        } catch (Exception e) {
            Log.e("MRD-EX", e.getMessage());
        }
    }

    public void initViewHolder() {

        if (root_view == null) {

            root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));
            Typeface typeface_roboto_regular = Typeface.createFromAsset(getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_REGULAR));
            holder.button_login = (Button) root_view.findViewById(R.id.button_mainmenu_start_login);
            holder.button_login.setTypeface(typeface_roboto_bold);
            holder.text_name = (AutoCompleteTextView) root_view
                    .findViewById(R.id.text_mainmenu_name);
            holder.text_password = (EditText) root_view.findViewById(R.id.text_mainmenu_password);
            holder.text_password.setTypeface(typeface_roboto_regular);
            root_view.setTag(holder);

            holder.text_password.setOnKeyListener( new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                        holder.button_login.callOnClick();
                        return true;
                    }
                    return false;
                }
            });

        } else {
            holder = (ViewHolder) root_view.getTag();
            if ((root_view.getParent() != null) && (root_view.getParent() instanceof ViewGroup)) {
                ((ViewGroup) root_view.getParent()).removeAllViewsInLayout();
            } else {
            }
        }
    }

    // ViewHolder stores static instances of views in order to reduce the number
    // of times that findViewById is called, which affected listview performance
    static class ViewHolder {
        Button button_login;
        AutoCompleteTextView text_name;
        EditText text_password;
    }
}
