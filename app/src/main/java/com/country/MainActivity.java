package com.country;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private TextView txtName,txtCity,txtCountry;
    private ListView lstView;
    private RadioButton radioName,radioCuntry,radioCity;
    private List<String> result;

    private Bilgiler bilgiler;
    private Database appBilgiDatabase;

    private void init(){
        txtName=findViewById(R.id.txtName);
        txtCity=findViewById(R.id.txtCity);
        txtCountry=findViewById(R.id.txtCountry);
        lstView=findViewById(R.id.lstView);
        radioName=findViewById(R.id.radioName);
        radioCity=findViewById(R.id.radioCity);
        radioCuntry=findViewById(R.id.radioCountry);

        result=new ArrayList<>();
        bilgiler=new Bilgiler();
        appBilgiDatabase=new Database(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog alertMessage = new AlertDialog.Builder(MainActivity.this).create();
                alertMessage.setTitle("Sil");
                alertMessage.setMessage(adapterView.getAdapter().getItem(i).toString()+" şehrini silmek istediğinizden emin misiniz?");

                alertMessage.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        if (radioName.isChecked()){
                            appBilgiDatabase.getNameID(adapterView.getAdapter().getItem(i).toString());
                        }

                        else if(radioCuntry.isChecked()) {
                            appBilgiDatabase.getCountID(adapterView.getAdapter().getItem(i).toString());
                        }
                        else if(radioCity.isChecked()) {
                            appBilgiDatabase.getCityID(adapterView.getAdapter().getItem(i).toString());
                        }
                        Yenile();
                    }
                });

                alertMessage.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertMessage.show();
                return false;

            }
        });
    }

    public void onVerileriGetir(View view) {
        new Veriler().execute();
    }

    public void onKaydet(View view) {
        appBilgiDatabase.bilgiEkle(bilgiler);
    }

    private void Yenile(){
        result.clear();
        String gecici="";
        if (radioName.isChecked()){
            for(Bilgiler bilgi:appBilgiDatabase.getListAll()){
                gecici="Name :  "+bilgi.getName();
                result.add(gecici);
            }
        }

        else if(radioCuntry.isChecked()) {
            for(Bilgiler bilgi:appBilgiDatabase.getListAll()){
                gecici="Country :  "+bilgi.getCountry();
                result.add(gecici);
            }
        }
        else if(radioCity.isChecked()) {
            for(Bilgiler bilgi:appBilgiDatabase.getListAll()){
                gecici="City :  "+bilgi.getCity();
                result.add(gecici);
            }
        }
        else {
            Toast.makeText(this, "Seçim yapınız", Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                this,android.R.layout.simple_list_item_1,result);
        lstView.setAdapter(arrayAdapter);
        if(result.isEmpty())
            Toast.makeText(this, "Liste boş. Lütfen veri kaydedin", Toast.LENGTH_SHORT).show();


        //Nesne olarak listeyi ekrana basıyor.
       /* ArrayAdapter<KurBilgileri> arrayAdapter=new ArrayAdapter<>(
                this,android.R.layout.simple_list_item_1,kurDatabase.getAllList());
        lstView.setAdapter(arrayAdapter);*/
    }
    public void onListele(View view) {
      Yenile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Uygulama ilk açıldığında web servise gidip verileri çekip rastgele gösterecek
        radioName.setChecked(true);
        new Veriler().execute();
    }

    public class Veriler extends AsyncTask<String,String,String> {

        private Bilgiler bilgi=new Bilgiler();
        private Random uret=new Random();
        private String URL_KAYNAK="http://www.androidevreni.com/dersler/json_veri.txt";
        private int uretilenSayi;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uretilenSayi=uret.nextInt(17);
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection connection=null;
            BufferedReader bufferedReader=null;

            try{
                URL url=new URL(URL_KAYNAK);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String satir;
                String dosya="";
                while((satir=bufferedReader.readLine())!=null){
                    dosya+=satir;
                }
                JSONArray jsonArray=new JSONArray(dosya);
                JSONObject jsonObject=jsonArray.getJSONObject(uretilenSayi);

                bilgi.setName(jsonObject.getString("Name"));
                bilgi.setCountry(jsonObject.getString("Country"));
                bilgi.setCity(jsonObject.getString("City"));
                bilgiler=bilgi;
                return dosya;
            }
            catch (Exception e){
                Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtName.setText(bilgi.getName());
            txtCity.setText(bilgi.getCity());
            txtCountry.setText(bilgi.getCountry());
        }
    }


}
