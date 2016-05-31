package com.petdoc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DocDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private int position;
    private String phone;
    private Button callButton;
    LoadingActivity loadingActivity;
    private ActionBar ab;
    private boolean login_success = false;
    private final String serverUrl = "http://192.168.11.20/review/Viewreview/allreview.php";
    private ListView listView;
    private ArrayList<ReviewItem> items = new ArrayList<>();
    private phpDown task;
    private ReViewItemListAdapter adapter;

    private boolean buttonFlag = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_detail);
        Log.i("DocDetailActivity", "onCreate 실행");


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        login_success = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        ab = this.getSupportActionBar();
        setCustomActionBar(ab);

        titleTextView = (TextView) findViewById(R.id.dataTitle);
        addressTextView = (TextView) findViewById(R.id.dataAddress);


        listView = (ListView)findViewById(R.id.review_listView);
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra("position", 0);
            setData(position);
            System.out.println("position : " + position);
        }

        callButton = (Button) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("DocDetailActivity", "onResume 실행");
        task = new phpDown();
        task.execute(serverUrl);

        adapter = new ReViewItemListAdapter(this, items);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setData(int position) {

        if (loadingActivity.docItemArrayList != null && loadingActivity.docItemArrayList.size() != 0) {
            titleTextView.setText(loadingActivity.docItemArrayList.get(position).getTitle());
            addressTextView.setText(loadingActivity.docItemArrayList.get(position).getAddress());
            phone = loadingActivity.docItemArrayList.get(position).getPhone();

        }
    }

    public void reviewButtonClicked(View view) {

        if(login_success){
            Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
            intent.putExtra("doc_id", loadingActivity.docItemArrayList.get(position).getId());
            startActivity(intent);
        }

        else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void setCustomActionBar(ActionBar ab) {

        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(R.layout.custom_actionbar);
        ab.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        View view = ab.getCustomView();
        ImageButton backButton = (ImageButton) view.findViewById(R.id.drawer_button);
        ImageButton nullButton = (ImageButton) view.findViewById(R.id.search_button);
        nullButton.setImageResource(0);
        backButton.setImageResource(R.drawable.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void seeReviewButtonClicked(View view) {

        if(buttonFlag){
            listView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            buttonFlag = false;
        } else {
            listView.setVisibility(View.GONE);
            buttonFlag = true;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("DocDetailActivity", "onPause 실행");

    }

    public void onMapButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), DetailMapActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private class phpDown extends AsyncTask<String, Integer, String> {

        String content;
        String dateTime;
        String user_id;
        int doc_id;
        float rating;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                progressDialog = new ProgressDialog(DocDetailActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("멍멍멍...");
                progressDialog.show();

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
                String str_doc_id;
                String str_rating;
                if(items.size() == 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        str_doc_id = jo.getString("doc_id");
                        user_id = jo.getString("user_id");
                        content = jo.getString("content");
                        dateTime = jo.getString("created_at");
                        str_rating = jo.getString("rating");
                        doc_id = Integer.valueOf(str_doc_id);
                        rating = Integer.valueOf(str_rating);
                        if(doc_id == (position + 1)){
                            items.add(new ReviewItem(doc_id, user_id, content, dateTime, rating));
                        }
                    }
                } else {
                    items.clear();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        str_doc_id = jo.getString("doc_id");
                        user_id = jo.getString("user_id");
                        content = jo.getString("content");
                        dateTime = jo.getString("created_at");
                        str_rating = jo.getString("rating");
                        doc_id = Integer.valueOf(str_doc_id);
                        rating = Integer.valueOf(str_rating);
                        if(doc_id == (position + 1)){
                            items.add(new ReviewItem(doc_id, user_id, content, dateTime, rating));
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(String str) {
                progressDialog.dismiss();
        }

    }

    public class ReViewItemListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<ReviewItem> list;

        public ReViewItemListAdapter(Context context, ArrayList<ReviewItem> datas){
            list = datas;
            mContext = context;
        }

        public ReViewItemListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return list.size();

        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reviewlistitem, parent, false);
            }

            TextView userId = (TextView)convertView.findViewById(R.id.userId);
            TextView reviewDate = (TextView)convertView.findViewById(R.id.reviewDate);
            TextView reviewContent = (TextView)convertView.findViewById(R.id.reviewcontent);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.review_ratingBar);

            userId.setText(items.get(position).getUser_id());
            reviewDate.setText(items.get(position).getDateTime());
            reviewContent.setText(items.get(position).getContent());
            ratingBar.setRating(items.get(position).getRating());
            notifyDataSetChanged();

            return convertView;
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
