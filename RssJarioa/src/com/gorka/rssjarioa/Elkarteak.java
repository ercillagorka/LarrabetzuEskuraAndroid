package com.gorka.rssjarioa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;

public class Elkarteak extends Activity {

    DbEgokitua db = new DbEgokitua(this);
    public ArrayList<List_Sarrera> arr_data = new ArrayList<List_Sarrera>();
    int ekintza_id= -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_elkarteak);

        Bundle bundle=getIntent().getExtras();
        if (bundle != null) {
            ekintza_id = bundle.getInt("ekintza_id");
        }

        db.zabaldu();
        if(ekintza_id<0){
            try{
                Cursor cursor = db.autorLortuDanak();
                do {
                    int logo = R.drawable.rsslogo;
                    String izena = cursor.getString(0);
                    if (izena.replace(" ","").equalsIgnoreCase("gurpide")) {
                        logo = R.drawable.gurpide;
                    }else if (izena.replace(" ","").equalsIgnoreCase("intxurretajaibatzordea")) {
                        logo = R.drawable.intxurreta;
                    }else if (izena.replace(" ","").equalsIgnoreCase("larrabetzukoeskola")) {
                        logo = R.drawable.eskola;
                    }else if (izena.replace(" ","").equalsIgnoreCase("larrabetzukoudala")) {
                        logo = R.drawable.udala;
                    }else if (izena.replace(" ","").equalsIgnoreCase("horibai")) {
                        logo = R.drawable.horibai;
                    }else if (izena.replace(" ","").equalsIgnoreCase("kukubel")) {
                        logo = R.drawable.kukubel;
                    }
                    arr_data.add(new List_Sarrera(logo, izena, cursor.getString(1), cursor.getString(2)));

                } while(cursor.moveToNext());

            }catch (Exception ex){
                Log.e("arr_data-datubasetik-Elkarteak", ex.toString());
            }
        }else {
            try{
                Cursor c = db.autorLortuId(ekintza_id);
                do{
                    Cursor cautor = db.autorLortu(Integer.parseInt(c.getString(0)));
                    do {
                        int logo = R.drawable.rsslogo;
                        String izena = cautor.getString(0);
                        if (izena.replace(" ","").equalsIgnoreCase("gurpide")) {
                            logo = R.drawable.gurpide;
                        }else if (izena.replace(" ","").equalsIgnoreCase("intxurretajaibatzordea")) {
                            logo = R.drawable.intxurreta;
                        }else if (izena.replace(" ","").equalsIgnoreCase("larrabetzukoeskola")) {
                            logo = R.drawable.eskola;
                        }else if (izena.replace(" ","").equalsIgnoreCase("larrabetzukoudala")) {
                            logo = R.drawable.udala;
                        }else if (izena.replace(" ","").equalsIgnoreCase("horibai")) {
                            logo = R.drawable.horibai;
                        }else if (izena.replace(" ","").equalsIgnoreCase("kukubel")) {
                            logo = R.drawable.kukubel;
                        }
                        arr_data.add(new List_Sarrera(logo, izena, cautor.getString(1), cautor.getString(2)));

                    } while(cautor.moveToNext());
                }while (c.moveToNext());



            }catch (Exception ex){
                Log.e("arr_data-datubasetik-Elkarteak",ex.toString());
            }
        }



        db.zarratu();

        ListView lv = (ListView) findViewById(R.id.elkarteak_listview);
        lv.setAdapter(new List_adaptador(this, R.layout.layout_elkarteak, arr_data){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView logo = (ImageView) view.findViewById(R.id.layout_elkarteak_logo);
                    if (logo != null){
                        logo.setImageResource(((List_Sarrera) entrada).get_idImagen());
                    }
                    TextView nor = (TextView) view.findViewById(R.id.layout_elkarteak_nor);
                    if (nor != null) {
                        nor.setText(((List_Sarrera) entrada).get_nor());
                    }
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogWebEmail(position);
            }
        });
    }

    private void dialogWebEmail(final int position){
        final CharSequence[] items = {"Email", "web"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aukeratu nahi dozuna");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Log.i("email", arr_data.get(position).get_email());
                        String[] to = { arr_data.get(position).get_email()};
                        Intent itSend = new Intent(Intent.ACTION_SEND);
                        itSend.putExtra(Intent.EXTRA_EMAIL,to);
                        itSend.setType("message/rfc822");
                        try {
                            startActivity(Intent.createChooser(itSend, "Aukeratu e-posta bezeroa"));
                        }catch (android.content.ActivityNotFoundException ex){
                            Toast.makeText(Elkarteak.this, "Posta bezeroa ez dago instalatuta.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        String link = arr_data.get(position).get_web();
                        Intent intent=new Intent("webnavigation");
                        Bundle bundle =new Bundle();
                        bundle.putString("weblink", link);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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