package com.petdoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadingActivity extends AppCompatActivity {

    //private final String URL = "http://192.168.11.20/alldata.php"; //  php 주소
    //private final String URL = "http://192.168.219.146/alldata.php"; // php 주소
    private static final String ROOT_URL = "http://192.168.11.20/";
    //private static final String ROOT_URL = "http://192.168.219.197/";

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

    public void onNewActivity() {
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
