package alex.kushnerov.whereis.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import alex.kushnerov.whereis.R;
import alex.kushnerov.whereis.service.GPSTracker;
import alex.kushnerov.whereis.service.SendLocationService;
import alex.kushnerov.whereis.setting.Config;


public class UserAreaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int countClick = 0;
    private TextView tvLatitude, tvLongitude;
    private LocationManager locationManager;
    private String email, lat, lon;
    private GPSTracker gps;
    private Timer timer;
    private TimerTask timerTask;
    private int requestTimer = 1000 * 15;
    private boolean isOn = false;
    private boolean chekButton = false;
    private boolean statusOfGPS;
    private ImageView imageLogo;
    private static long secret_pressed;
    private String coordinatUrl;
    private SharedPreferences sharedPreferences;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        tvLatitude = (TextView) findViewById(R.id.tVLatitude);
        tvLongitude = (TextView) findViewById(R.id.tVLongtitude);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            showGPSDisabledAlertToUser();
        }
        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MAIN MENU");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        TextView tv = (TextView) v.findViewById(R.id.emailTextViewNavHead);
        tv.setText(email);
        imageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secret_pressed + 1000 > System.currentTimeMillis()) {
                    countClick++;
                } else {
                    countClick = 0;
                }
                secret_pressed = System.currentTimeMillis();
                if (countClick == 9) {
                    Intent intentHiddenMenu = new Intent(UserAreaActivity.this, HiddenMenuActivity.class);
                    startActivity(intentHiddenMenu);
                    countClick = 0;
                }
            }
        });
        loadDataPreference();
        if (Config.isOn() != chekButton) {
            getGPSLoc();
            navigationView.getMenu().findItem(R.id.nav_locationOFF).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_locationON).setVisible(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_locationOFF) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                showGPSDisabledAlertToUser();
            } else {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                Intent gpsService = new Intent(this, SendLocationService.class);
                this.startService(gpsService);
                getGPSLoc();
                item.setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_locationON).setVisible(true);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Config.BUTTON_ON_OFF_SHARED_PREF, true);
                editor.commit();
            }
        } else if (id == R.id.nav_locationON) {
            Intent gpsService = new Intent(this, SendLocationService.class);
            this.stopService(gpsService);
            timerTask.cancel();
            timer.purge();
            timer.cancel();
            tvLatitude.setText("");
            tvLongitude.setText("");
            item.setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_locationOFF).setVisible(true);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Config.BUTTON_ON_OFF_SHARED_PREF, false);
            editor.commit();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(UserAreaActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getGPSLoc() {
        gps = new GPSTracker(this);
        gps.getLocation();
        if (timerTask != null) {
            timerTask = null;
        }
        if (timer != null) {
            timer = null;
        }
        timer = new Timer();
        timerTask = createTimerTask();
        timer.schedule(timerTask, 0, requestTimer);
    }

    private TimerTask createTimerTask() {
        TimerTask t = new TimerTask() {
            public void run() {
                UserAreaActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        lat = String.valueOf(gps.getLatitude());
                        lon = String.valueOf(gps.getLongitude());
                        tvLatitude.setText(lat);
                        tvLongitude.setText(lon);
                    }
                });
            }
        };
        return t;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Turn it on, it's necessary for the application to work correctly.")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        editor.putString(Config.EMAIL_SHARED_PREF, "");
                        editor.commit();
                        Intent intent = new Intent(UserAreaActivity.this, LoginActivity.class);
                        startActivity(intent);
                        UserAreaActivity.this.finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }


    public void loadDataPreference() {
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_SERVERS, Context.MODE_PRIVATE);
        coordinatUrl = sharedPreferences.getString(Config.SHARED_PREF_COORDINAT_URL, Config.DEFAUTL_COORDINATES_REQUEST_URL);
        Config.setCoordinatesRequestUrl(coordinatUrl);
        isOn = sharedPreferences.getBoolean(Config.BUTTON_ON_OFF_SHARED_PREF, false);
        Config.setIsOn(isOn);

    }
}
