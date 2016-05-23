package com.petdoc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadingActivity extends AppCompatActivity {

    //private final String URL = "http://192.168.11.20/alldata.php"; //  php 주소
    //private final String URL = "http://192.168.219.146/alldata.php"; // php 주소
    //private static final String ROOT_URL = "http://192.168.11.20/";
    private static final String ROOT_URL = "http://192.168.219.197/";

    // private phpDown task;
    public static ArrayList<DocItem> docItemArrayList = null;
    private TextView textView;
    final Handler handler = new Handler();
    ChangeUI changeUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        textView = (TextView)findViewById(R.id.textView);
        changeUI = new ChangeUI();
        getDocs();

        /*task = new phpDown();
        task.execute(URL);*/
    }
    private void getDocs(){
        //While the app fetched data we are displaying a progress dialog
        //final ProgressDialog loading = ProgressDialog.show(this,"Fetching Data","Please wait...",false,false);

        //Creating a rest adapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL)
                .build();

        //Creating an object of our api interface
        PetDocAPI api = adapter.create(PetDocAPI.class);

        //Defining the method
        api.getPetDocs(new Callback<ArrayList<DocItem>>() {
            @Override
            public void success(ArrayList<DocItem> list, Response response) {
                //Dismissing the loading progressbar
                //loading.dismiss();
                //Storing the data in our list
                if(docItemArrayList == null) {
                    docItemArrayList = list;
                } else {
                    docItemArrayList.clear();
                    docItemArrayList = list;
                }
                changeUI.start();
            }
            @Override
            public void failure(RetrofitError error) {
                //you can handle the errors here
                finish();
            }
        });
    }
    private class ChangeUI extends Thread {
        final Handler handler = new Handler();
        int i = 0;
        @Override
        public void run() {
            while (i < docItemArrayList.size()) {
                handler.post(new Runnable() {
                    public void run() {
                        textView.setText(String.valueOf(i));
                        i++;
                    }
                });
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            onNewActivity();
        }
    }
    /* Params: background 작업 시 필요한 data의 type 지정
       Progress: background 작업 중 진행상황을 표현하는데 사용되는 data를 위한 type 지정
       Result: 작업의 결과로 리턴 할 data 의 type 지정
    */

    /*private class phpDown extends AsyncTask<String, Integer, String> {


        String title;
        String address;
        String phone;
        double latitude;
        double longitude;

        @Override
        protected void onPreExecute() {

            //asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // show dialog
            //asyncDialog.show();
            super.onPreExecute();
            textView = (TextView) findViewById(R.id.textView);

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
                                Log.i("MainActivity", "readline() 끝");
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

            try {
                JSONObject root = new JSONObject(jsonHtml.toString());
                JSONArray ja = root.getJSONArray("results");

                if(docItemArrayList.size() == 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        publishProgress(i);
                        Thread.sleep(5);
                        title = jo.getString("title");
                        address = jo.getString("address");
                        phone = jo.getString("phone");
                        latitude = jo.getDouble("latitude");
                        longitude = jo.getDouble("longitude");

                        docItemArrayList.add(new DocItem(i + 1, title, address, phone, latitude, longitude));
                    }
                } else {
                    docItemArrayList.clear();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        publishProgress(i + 1);
                        Thread.sleep(5);
                        title = jo.getString("title");
                        address = jo.getString("address");
                        phone = jo.getString("phone");
                        latitude = jo.getDouble("latitude");
                        longitude = jo.getDouble("longitude");

                        docItemArrayList.add(new DocItem(i + 1, title, address, phone, latitude, longitude));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            textView.setText(values[0].toString());
        }

        protected void onPostExecute(String str) {

            Log.i("MainActivity", docItemArrayList.get(0).getTitle() + docItemArrayList.get(0).getAddress() + docItemArrayList.get(0).getPhone());
            onNewActivity();

        }

    }*/

    public void onNewActivity() {
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
        finish();
    }
}
