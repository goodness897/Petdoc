package com.petdoc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private ActionBar ab;
    private RatingBar ratingBar;
    private final String serverUrl = "http://192.168.11.20/review/index.php";
    private EditText contentEdit;
    private int doc_id;
    String loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ab = this.getSupportActionBar();
        setCustomActionBar(ab);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        contentEdit = (EditText) findViewById(R.id.input_content);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME
                , Context.MODE_PRIVATE);
        loggedUser = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
        Intent intent = getIntent();
        doc_id = intent.getIntExtra("doc_id", 0);
    }

    private void setCustomActionBar(ActionBar ab) {

        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(R.layout.custom_actionbar);
        ab.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        View view = ab.getCustomView();
        ImageButton backButton = (ImageButton)view.findViewById(R.id.drawer_button);
        ImageButton nullButton = (ImageButton)view.findViewById(R.id.search_button);
        nullButton.setImageResource(0);
        backButton.setImageResource(R.drawable.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void saveReviewButtonClicked(View view) {

        String content = contentEdit.getText().toString();
        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(serverUrl, String.valueOf(doc_id), loggedUser, content);
        finish();
    }
    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            String jsonResult = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("doc_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("user_id", params[2]));
                nameValuePairs.add(new BasicNameValuePair("content", params[3]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8)); // 한글 인코딩

                HttpResponse response = httpClient.execute(httpPost);

                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                System.out.println("Returned Json object " + jsonResult.toString());

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResult;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + result);
            if(result.equals("") || result == null){
                return;
            }
            int jsonResult = returnParsedJsonObject(result);
            if(jsonResult == 0){
                return;
            }
            if(jsonResult == 1){
            }
        }
        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }
    private int returnParsedJsonObject(String result){
        JSONObject resultObject = null;
        int returnedResult = 0;
        try {
            resultObject = new JSONObject(result);
            returnedResult = resultObject.getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResult;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
