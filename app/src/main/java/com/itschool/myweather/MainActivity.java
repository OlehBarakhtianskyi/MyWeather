package com.itschool.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ImageView windImage;
    Button button;
    String jsonIn, text;
    TextView textView;
    WebView webView;
    Resources res;
    Main main;
    boolean isDataLoaded;
    boolean isConnected;
    String currWeatherURL;
    Document page = null;
    private String FLAG;
    WeatherGetter wg;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        windImage = findViewById(R.id.windImage);
        button = findViewById(R.id.buttonLoadData);
        textView = findViewById(R.id.textView);
        jsonIn = "";
        text = "";
        isDataLoaded = false;
        isConnected = true;
        message = "";
        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";

        wg = new WeatherGetter();
        wg.execute();
    }

    public void RefreshWeather(){}

    public void ParseWeather()
    {
        boolean cont = false;
        JSONObject json = null;
        try{
            json = new JSONObject(jsonIn);
            cont = true;
        }catch (JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        if (cont)
            try{
                String temp1 = "";
                JSONObject jsonMain = (JSONObject) json.get("main");      //ДОСТАЁМ ПО ИМЕНАМ
                double temp = jsonMain.getDouble("temp") - 273.15;
                int pressure = jsonMain.getInt("pressure");
                int humidity = jsonMain.getInt("humidity");
                //ФОРМАТ ВРЕМЕНИ
                SimpleDateFormat sm = new SimpleDateFormat("d.M.Y H:m");
                sm.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                Date date = new Date(json.getLong("dt") * 1000);

                JSONArray jsonWeather = (JSONArray) json.get("weather");
                String description = jsonWeather.getJSONObject(0).getString("description");

                JSONObject jsonWind = (JSONObject) json.get("wind");
                int speed = jsonWind.getInt("speed");
                int deg = jsonWind.getInt("deg");

                JSONObject jsonClouds = (JSONObject) json.get("clouds");
                int clouds = jsonClouds.getInt("all");

                String name = json.getString("name");

                main = new Main(temp, pressure, humidity, date, description, speed, deg, clouds, name);
                isDataLoaded = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        textView.setText(main.toString());
        drawWeather();
    }

    public void btnLoadData(View view) {

        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        //currWeatherURL = "https://api.openweathermap.org/data/2.5/forecast/daily?lat="+Coordinates.latitude+"&lon="+Coordinates.longitude+"&appid=b1b15e88fa797225412429c1c50c122a1";
        if (wg.getStatus() == AsyncTask.Status.RUNNING)
            wg.cancel(true);

        wg = new WeatherGetter();
        wg.execute();
        ParseWeather();
    }

    public void drawWeather() {

        if (isConnected) {
            if (main.getClouds() < 5) {
                imageView.setImageResource(R.drawable.transparent);
            } else if (main.getClouds() < 25) {
                imageView.setImageResource(R.drawable.cloud1);
            } else if (main.getClouds() < 50) {
                imageView.setImageResource(R.drawable.cloud2);
            } else if (main.getClouds() < 75) {
                imageView.setImageResource(R.drawable.cloud3);
            } else
                imageView.setImageResource(R.drawable.cloud4);

            imageView.setBackgroundResource(R.drawable.sun);

            // draw wind direction
            windImage.setImageResource(R.drawable.arrow);
            windImage.setRotation(main.getDeg() + 180);
            windImage.setScaleX(0.5f);
            windImage.setScaleY(0.5f);
            windImage.animate();
        } else
            imageView.setImageResource(R.drawable.nodata);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1) && (resultCode == 1)) {
            Coordinates.longitude = data.getDoubleExtra("longitude", Coordinates.longitude);
            Coordinates.latitude = data.getDoubleExtra("latitude", Coordinates.latitude);

            textView.setText(Coordinates.longitude + ", " + Coordinates.latitude);
        }
    }

    class WeatherGetter extends AsyncTask<Void, Void, Void> {
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {          //ПРочитывает джсон в одну строковую переменную
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public void ConnectAndGetData(String url) {
            InputStream is = null;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  //Проверка подключения к сети И ПОДКЛЮЧИТЬСЯ
            NetworkInfo netInfo = cm.getActiveNetworkInfo();                                                //ПРОВЕРИТЬ СОСТОЯНИЕ СЕТИ

            if (netInfo.isConnected()) {
                try {
                    is = new URL(url).openStream(); // открываем поток
                    try {
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                        try {
                            jsonIn = readAll(rd);      //Считываем
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                isConnected = false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectAndGetData(currWeatherURL);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textView.setText("\n------------------\n" + jsonIn+"\n--------------------\n");
            ParseWeather();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d("", "Process canceling");
        }
    }
}
