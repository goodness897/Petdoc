package com.petdoc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String URL = "http://192.168.11.20/alldata.php";
    TextView textView;
    phpDown task;

    ArrayList<DocItem> docItemArrayList = new ArrayList<DocItem>();
    DocItem docItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task = new phpDown();
        textView = (TextView)findViewById(R.id.textView);
        task.execute(URL);
    }

    private class phpDown extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i("MainActivity", "doInBackground 실행중");
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            String line = br.readLine();
                            if(line == null) {
                                Log.i("MainActivity", "doInBackground 끝");
                                break;
                            }
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str){
            String title;
            String address;
            String phone;
            double latitude;
            double longitude;

            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for(int i = 0; i < ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    title = jo.getString("title");
                    address = jo.getString("address");
                    phone = jo.getString("phone");
                    latitude = jo.getDouble("latitude");
                    longitude = jo.getDouble("longitude");
                    docItemArrayList.add(new DocItem(title, address, phone, latitude, longitude));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            textView.setText("title : " + docItemArrayList.get(0).getTitle() + "\n");
            textView.append("address : " + docItemArrayList.get(0).getAddress() + "\n");
            textView.append("phone : " + docItemArrayList.get(0).getPhone() + "\n");
        }

    }
}
