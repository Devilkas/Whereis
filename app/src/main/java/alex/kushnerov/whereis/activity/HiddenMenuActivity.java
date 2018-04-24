package alex.kushnerov.whereis.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import alex.kushnerov.whereis.R;
import alex.kushnerov.whereis.service.SendLocationService;
import alex.kushnerov.whereis.setting.Config;


public class HiddenMenuActivity extends AppCompatActivity {

    private Button btnSeaveSetting, btnCancel;
    private ArrayList<String> servers;
    private JSONArray resultServer;
    private SharedPreferences sharedPreferences;
    private String serverLoginUrl, serverName, serverCoordinatUrl;
    private boolean giveServerName = false;
    private JSONObject j;
    private Button btnSelectServer;
    private AlertDialog.Builder dialogBuilderServers;
    private TextView textViewServerSelected;
    private Context context;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = HiddenMenuActivity.this;
        textViewServerSelected = (TextView) findViewById(R.id.textViewServerSelected);
        btnSeaveSetting = (Button) findViewById(R.id.btnSaveSetting);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiddenMenuActivity.this.finish();
            }
        });
        servers = new ArrayList<String>();
        btnSeaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert();
            }
        });
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_SERVERS, Context.MODE_PRIVATE);
        serverName = sharedPreferences.getString(Config.SHARED_PREF_SELECTED_ITEM_STRING, "");
        textViewServerSelected.setText(serverName);
        getDataServers();
        btnSelectServer = (Button) findViewById(R.id.btnSelectServer);
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Save setting");
        alertDialog.setMessage("You must restart the application in order for the settings to apply. Reload?");
        alertDialog.setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent gpsService = new Intent(HiddenMenuActivity.this, SendLocationService.class);
                stopService(gpsService);
                saveServerData();
                System.exit(0);
            }
        });
        alertDialog.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    private void getDataServers() {
        StringRequest stringRequest = new StringRequest(Config.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            j = new JSONObject(response);
                            resultServer = j.getJSONArray(Config.JSON_ARRAY_SERVER);
                            getServerData(resultServer);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error Server Connection", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getServerData(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                servers.add(json.getString(Config.TAG_NAME_SERVER));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getServerName(int position) {
        try {
            JSONObject json = resultServer.getJSONObject(position);
            serverName = json.getString(Config.TAG_NAME_SERVER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serverName;
    }

    private String getServerLoginUrl(int position) {
        try {
            JSONObject json = resultServer.getJSONObject(position);
            serverLoginUrl = json.getString(Config.TAG_LOGIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serverLoginUrl;
    }

    private String getServerCoordinatUrl(int position) {
        try {
            JSONObject json = resultServer.getJSONObject(position);
            serverCoordinatUrl = json.getString(Config.URL_COORDINATS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serverCoordinatUrl;
    }

    public void saveServerData() {
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_SERVERS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.SHARED_PREF_SELECTED_ITEM_STRING, serverName);
        editor.putString(Config.SHARED_PREF_LOGIN_URL, serverLoginUrl);
        editor.putString(Config.SHARED_PREF_COORDINAT_URL, serverCoordinatUrl);
        editor.putBoolean(Config.GIVE_SERVER_NAME_SHARED_PREF, true);
        editor.putBoolean(Config.BUTTON_ON_OFF_SHARED_PREF, false);
        editor.commit();
        Toast.makeText(getApplicationContext(), "Setting Saved", Toast.LENGTH_SHORT).show();
    }


    public void onClickSelectServer(View view) {
        switch (view.getId()) {
            case R.id.btnSelectServer: {
                ArrayAdapter<String> dialogAdapterServers = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, servers);
                dialogBuilderServers = new AlertDialog.Builder(HiddenMenuActivity.this);
                dialogBuilderServers.setTitle("Server List:");
                dialogBuilderServers.setAdapter(dialogAdapterServers, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_SERVERS, Context.MODE_PRIVATE);
                        giveServerName = sharedPreferences.getBoolean(Config.GIVE_SERVER_NAME_SHARED_PREF, false);
                        if (giveServerName) {
                            serverName = getServerName(which);
                            serverLoginUrl = getServerLoginUrl(which);
                            serverCoordinatUrl = getServerCoordinatUrl(which);
                        }
                        if (getServerName(which) == serverName) {
                            textViewServerSelected.setText(serverName);
                            Toast.makeText(getApplicationContext(), "Server Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBuilderServers.create().show();
            }
        }
    }
}