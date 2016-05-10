package com.petdoc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

public class LoadingActivity extends AppCompatActivity {

    private final String URL = "http://192.168.11.20/alldata.php"; // php 주소
    private phpDown task;
    public static ArrayList<DocItem> docItemArrayList = new ArrayList<DocItem>();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        textView = (TextView) findViewById(R.id.textView);

        task = new phpDown();
        task.execute(URL);
    }

    /* Params: background 작업 시 필요한 data의 type 지정
       Progress: background 작업 중 진행상황을 표현하는데 사용되는 data를 위한 type 지정
       Result: 작업의 결과로 리턴 할 data 의 type 지정
    */

    private class phpDown extends AsyncTask<String, Integer, String> {

        // CustomProgressDialog asyncDialog = new CustomProgressDialog(Loading.this);

        @Override
        protected void onPreExecute() {

            //asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // show dialog
            //asyncDialog.show();


            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("MainActivity", "doInBackground 실행중");
            StringBuilder jsonHtml = new StringBuilder();
            try {
                java.net.URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            String line = br.readLine();
                            if (line == null) {
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

        protected void onPostExecute(String str) {
            String title;
            String address;
            String phone;
            double latitude;
            double longitude;

            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");

                if(docItemArrayList.size() == 0) {

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        title = jo.getString("title");
                        address = jo.getString("address");
                        phone = jo.getString("phone");
                        latitude = jo.getDouble("latitude");
                        longitude = jo.getDouble("longitude");
                        docItemArrayList.add(new DocItem(title, address, phone, latitude, longitude));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("MainActivity", docItemArrayList.get(0).getTitle() + docItemArrayList.get(0).getAddress() + docItemArrayList.get(0).getPhone());

            final Handler handler = new Handler();
            Thread thread = new Thread() {
                @Override
                public void run() {

                    for (int i = 0; i < docItemArrayList.size(); i++) {
                        final int finalI = i;
                        try {
                            Thread.sleep(10);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(String.valueOf(finalI));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } // for 문 종료
                    onNewActivity();

                }
            };
            thread.start();

            // marker 추가
            /*if (marker == null) {
                marker = new MarkerOptions();
                showItems();
            }*/
            // listFragment.listView.invalidate();
            /*listFragment.adapter.notifyDataSetChanged();
            asyncDialog.dismiss();*/
        }

    }

    public void onNewActivity() {
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
        finish();
    }


}
