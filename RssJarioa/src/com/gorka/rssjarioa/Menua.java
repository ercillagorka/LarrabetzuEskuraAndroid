package com.gorka.rssjarioa;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class Menua extends Activity {

	DbEgokitua db=new DbEgokitua(this);
    boolean hariaEginda = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menua);
        ParseAnalytics.trackAppOpened(getIntent());

        final TextView bertsioa = (TextView) findViewById(R.id.menua_bertsioa);
        try {
            bertsioa.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Menua bertsiojarri",e.toString());
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        if(networkAvailable()) {
            // Badago Interneta
                Log.d("INTERNET", "Badago");
                bertsioaBegitu();
                haria();
        }else{
            // Ez dago internetik
                networkNoAvailableDialog();
                Log.d("INTERNET", "EZ dago");
            }
        final LinearLayout info = (LinearLayout) findViewById(R.id.menua_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menua.this.openOptionsMenu();
            }
        });
        if (networkAvailable()) {
            info.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        startActivity(new Intent("menupush"));
                    } else {
                        startActivity(new Intent("loginpush"));
                    }
                    return true;
                }
            });
        }
        if(isTablet(this)){
            Button btnHobespenak = (Button)findViewById(R.id.btnhobespenak);
            Button btnKontaktua = (Button)findViewById(R.id.btnkontaktua);
            Button btnNortzuk = (Button)findViewById(R.id.btnnortzuk);

            btnHobespenak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent("hobespenak"));
                }
            });
            btnKontaktua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent("kontaktua"));
                }
            });
            btnNortzuk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent("eskura"));
                }
            });
        }

    }

    public void onclickbtnberriak(@SuppressWarnings("UnusedParameters")View view){
        if (networkAvailable()) {
            startActivity(new Intent("berriak"));
        }else{
            Toast.makeText(Menua.this,"EZ zaude internetari konektatuta",Toast.LENGTH_LONG).show();
        }
    }
	    
    public void onclickbtnagenda(@SuppressWarnings("UnusedParameters")View view){
        if(hariaEginda || !networkAvailable()){
            startActivity(new Intent("agenda"));
        }else {
            Toast.makeText(this,"zerbitzariarekin konektatzen",Toast.LENGTH_LONG).show();
        }
    }

    public void onclickbtnelkarteak(@SuppressWarnings("UnusedParameters")View view){
        if(hariaEginda || !networkAvailable()){
            startActivity(new Intent("elkarteak"));
        }else {
            Toast.makeText(this,"zerbitzariarekin konektatzen",Toast.LENGTH_LONG).show();
        }
    }

    public void onclickbtntwitter(@SuppressWarnings("UnusedParameters")View view){
        Intent intent=new Intent("webnavigation");
        Bundle bundle =new Bundle();
        bundle.putString("weblink", "https://twitter.com/search?q=larrabetzu");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isTablet(this)){
            getMenuInflater().inflate(R.menu.menu_menua, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isTablet(this)){
            switch (item.getItemId()) {
                case R.id.menu_hobespenak:
                    startActivity(new Intent("hobespenak"));
                    return true;
                case R.id.menu_kontaktua:
                    startActivity(new Intent("kontaktua"));
                    return true;
                case R.id.menu_nortzuk:
                    startActivity(new Intent("eskura"));
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }else {
            return false;
        }
    }

    private void haria(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                long time_start, time_end;
                time_start = System.currentTimeMillis();
                Log.e("hilo1", "on");

                db.zabaldu();

                try{
                    db.eguneratuElkarteak();
                }catch (Exception e){
                    Log.e("eguneratuElkarteak",e.toString());
                    exceptionTracker(e);
                }
                try{
                    db.eguneratuEkintzak();
                }catch (Exception e){
                    Log.e("eguneratuEkintzak",e.toString());
                    exceptionTracker(e);
                }
                try{
                    db.garbitu();
                }catch (Exception e){
                    Log.e("garbitu",e.toString());
                    exceptionTracker(e);
                }
                db.zarratu();
                Log.e("hilo1", "off");
                time_end = System.currentTimeMillis();
                EasyTracker tracker = EasyTracker.getInstance(Menua.this);
                tracker.send(MapBuilder.createTiming("Menua",(time_end - time_start), "haria", null).build());
                hariaEginda = true;
            }

        }).start();
    }

    private void bertsioaBegitu(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("hilo2", "on");
                boolean bertsiozaharra = false;
                final Calendar c = Calendar.getInstance();
                int mWeek = c.get(Calendar.WEEK_OF_YEAR);
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Menua.this);
                if(mWeek != sharedPrefs.getInt("bertsioaBegituData",0)){
                    try {
                        URL url = new URL("http://larrabetzu.net/Bertsioa/");
                        URLConnection uc = url.openConnection();
                        uc.connect();
                        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                        String inputLine=in.readLine();
                        Log.e("",inputLine);
                        if(inputLine != null ) {
                            int webversionCode = 0;
                            try{
                                webversionCode = Integer.parseInt(inputLine);
                            }catch (Exception ex){
                                Log.e("Menua-bertsiobegitu",ex.toString());
                            }
                            if(webversionCode>(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode)){
                                Log.e("bertsio","ezberdinak");
                                bertsiozaharra = true;
                            }else{
                                Log.e("bertsio","berdinak");
                            }
                        }
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putInt("bertsioaBegituData",mWeek);
                        editor.commit();
                        in.close();
                    }catch (FileNotFoundException e){
                        Log.e("Menua-bertsioaBegitu","ezin da serbitzariarekin konektatu");
                        Tracker myTracker = EasyTracker.getInstance(Menua.this);
                        myTracker.send(MapBuilder.createException(new StandardExceptionParser(Menua.this, null)
                                .getDescription(Thread.currentThread().getName(), e), false).build());
                    } catch (Exception e) {
                        Log.e("Menua-bertsioaBegitu", e.toString());
                        Tracker myTracker = EasyTracker.getInstance(Menua.this);
                        myTracker.send(MapBuilder.createException(new StandardExceptionParser(Menua.this, null)
                                .getDescription(Thread.currentThread().getName(), e), false).build());
                    }
                }
                Message msg = bertsioHandler.obtainMessage();
                msg.obj = bertsiozaharra;
                bertsioHandler.sendMessage(msg);
                Log.e("hilo2", "off");
            }
        }).start();
    }

    private final Handler bertsioHandler = new Handler() {
        public void handleMessage(Message msg) {
            if((Boolean)msg.obj){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Menua.this)
                                .setSmallIcon(android.R.drawable.stat_sys_warning)
                                .setLargeIcon((((BitmapDrawable) getResources().getDrawable(R.drawable.rsslogo)).getBitmap()))
                                .setContentTitle("Aplikazioan eguneraketa bat dago")
                                .setContentText("Eguneratu nahi dozu?")
                                .setTicker("Eguneratu!");

                Intent notIntent;
                PendingIntent contIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    try {
                        notIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.gorka.rssjarioa"));
                        contIntent = PendingIntent.getActivity(Menua.this, 0, notIntent, 0);
                        mBuilder.setContentIntent(contIntent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        notIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+"com.gorka.rssjarioa"));
                        contIntent = PendingIntent.getActivity(Menua.this, 0, notIntent, 0);
                        mBuilder.setContentIntent(contIntent);
                    }
                }else {
                    notIntent =  new Intent(Menua.this, Menua.class);
                    contIntent = PendingIntent.getActivity(Menua.this, 0, notIntent, 0);
                    mBuilder.setContentIntent(contIntent);
                    bertsioaEguneratu();
                }
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1 ,mBuilder.build());
            }
        }
    };

    public boolean networkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }else{
            Log.d("INTERNET", "EZ dago internetik");
        }
        return false;
    }

    public void networkNoAvailableDialog(){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setIcon(R.drawable.warning);
        alertbox.setTitle("EZ zaude internetari konektatuta");
        alertbox.setPositiveButton("Bale", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {}
        });
        alertbox.show();
    }

    private void bertsioaEguneratu(){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setTitle("Aplikazioan eguneraketa bat dago");
        alertbox.setMessage("Eguneratu nahi dozu?");
        alertbox.setPositiveButton("Bai", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                final String appName = "com.gorka.rssjarioa";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
                }
                finish();
            }
        });
        alertbox.setNegativeButton("Ez", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void exceptionTracker(Exception e){
        Tracker myTracker = EasyTracker.getInstance(Menua.this);
        myTracker.send(MapBuilder.createException(new StandardExceptionParser(Menua.this, null)
                .getDescription(Thread.currentThread().getName(), e), false).build());
    }


    @Override
    public void onStart() {
        super.onStart();
        // The rest of your onStart() code.
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        EasyTracker.getInstance(this).activityStop(this);
    }
}
