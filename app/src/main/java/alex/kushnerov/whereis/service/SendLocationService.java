package alex.kushnerov.whereis.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import alex.kushnerov.whereis.R;
import alex.kushnerov.whereis.activity.UserAreaActivity;
import alex.kushnerov.whereis.setting.Config;
import alex.kushnerov.whereis.setting.CoordinatesToJsonRequest;

import static com.android.volley.VolleyLog.TAG;

public class SendLocationService extends Service {
    String msg = "CAUTION! If kept open, can consume lots of battery";
    int FORE_ID = 1335;
    private Timer timer;
    private TimerTask timerTask;
    int requestTimer = 1000 * 15;
    private String email, day, time, lat, lon;
    private GPSTracker gps;
    private Handler handler;
    private RequestQueue queue;

    public SendLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        Intent noty_intent = new Intent(this,
                UserAreaActivity.class);
        noty_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, noty_intent,
                0);

        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Sending GPS coordinates...")
                .setContentText(msg).setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent).setAutoCancel(true).setOngoing(true)
                .build();

        startForeground(FORE_ID, n);

        sendingGPSLoc();

        return START_REDELIVER_INTENT;
    }

    public void sendingGPSLoc() {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
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
    }

    private TimerTask createTimerTask() {
        TimerTask t = new TimerTask() {
            public void run() {
                SendLocationService.this.runOnUiThread(new Runnable() {
                    public void run() {
                        long date = System.currentTimeMillis();
                        SimpleDateFormat dayNow = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat timeNow = new SimpleDateFormat("HH:mm:ss");
                        day = dayNow.format(date);
                        time = timeNow.format(date);
                        lat = String.valueOf(gps.getLatitude());
                        lon = String.valueOf(gps.getLongitude());
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        };
                        CoordinatesToJsonRequest coordinatesToJsonRequest = new CoordinatesToJsonRequest(email, day, time, lat, lon, responseListener);
                        if (queue == null) {
                            queue = Volley.newRequestQueue(SendLocationService.this);
                        }
                        queue.add(coordinatesToJsonRequest);

                    }
                });
            }
        };
        return t;
    }

    @Override
    public void onDestroy() {
        gps.stopUsingGPS();
        timerTask.cancel();
        timer.purge();
        timer.cancel();
        super.onDestroy();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
}
