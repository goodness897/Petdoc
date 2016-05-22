package com.petdoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by STU on 2016-05-03.
 */
public class ListFragment extends Fragment {

    MainActivity main;
    ListView listView;
    DocItemListAdapter adapter;
    LoadingActivity loadingActivity;
    private int mSelectedItem;
    private double list_latitude;
    private double list_longitude;
    private double cur_latitude;
    private double cur_longitude;
    Handler handler = new Handler();
    TextView docPhone;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MainActivity","ListFragment onCreateView 실행");

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        adapter = new DocItemListAdapter(getContext(), loadingActivity.docItemArrayList);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DocDetailActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("MainActivity","ListFragment onResume 실행");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MainActivity","ListFragment onPause 실행");
    }

    public void setSeletedItem(int position){
        Log.i("MainActivity","ListFragment position : " + position);
        mSelectedItem = position;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(position-1, true);
        listView.smoothScrollToPosition(position);
        adapter.notifyDataSetChanged();
    }

    public void setDistance(double current_latitude, double current_longitude, double dis_latitude, double dis_longitude){
        cur_latitude = current_latitude;
        cur_longitude = current_longitude;
        list_latitude = dis_latitude;
        list_longitude = dis_longitude;
        Log.i("MainActivity","setDistance 호출 : " + list_latitude + "," + list_longitude);


    }

    public class DocItemListAdapter extends BaseAdapter {
        private Context mContext;

        LoadingActivity loadingActivity;

        public DocItemListAdapter(Context context, ArrayList<DocItem> datas){

            loadingActivity.docItemArrayList = datas;
            mContext = context;
        }

        public DocItemListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return loadingActivity.docItemArrayList.size();

        }

        @Override
        public Object getItem(int position) {
            return loadingActivity.docItemArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listitem, parent, false);
            }
            ImageView docImage = (ImageView) convertView.findViewById(R.id.dataImage);
            TextView docTitle = (TextView) convertView.findViewById(R.id.dataTitle);
            TextView docAddress = (TextView) convertView.findViewById(R.id.dataAddress);
            docPhone = (TextView) convertView.findViewById(R.id.dataPhone);
            DocItem docItem = loadingActivity.docItemArrayList.get(position);
            String distance = getDistance(cur_latitude, cur_longitude, list_latitude, list_longitude);
            // 이미지 추가 해야함
            // docImage.setImage
            docTitle.setText(docItem.getTitle());
            docAddress.setText(docItem.getAddress());
            // docPhone.setText(docItem.getPhone());
            docPhone.setText(distance);
            notifyDataSetChanged();
            return convertView;
        }
    }

    private String getDistance(double current_latitude, double current_longitude, double dis_latitude, double dis_longitude) {
        int iDistance = 0;
        String sDistance = "";
        StringBuilder urlString = null;
        Log.d("xxx","latitude : "+dis_latitude + ", longitude : " + dis_longitude);
        urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?origin=" + current_latitude
                + "," + current_longitude + "&destination=" + dis_latitude + "," + dis_longitude + "&mode=transit");
        Log.d("xxx", "URL=" + urlString.toString());
        ConnectThread thread = new ConnectThread(urlString.toString());

        thread.start();

        return sDistance;

    }
    class ConnectThread extends Thread {
        String urlStr;

        public ConnectThread(String inStr) {
            urlStr = inStr;
        }

        public void run() {
            try {
                final String output = request(urlStr);
                JSONObject object = null;
                JSONArray legs = null;
                JSONObject step = null;
                String distance = null;
                String sDistance = "";
                try {
                    object = new JSONObject(output);
                    JSONArray array = object.getJSONArray("routes");
                    for(int i =0; i < array.length(); i++){
                        legs = ((JSONObject)array.get(i)).getJSONArray("legs");
                        for(int j = 0; j < legs.length(); j++){
                            step = ((JSONObject)legs.get(j)).getJSONObject("distance");
                            distance = step.getString("text");
                        }
                    }
                    //Log.d("JSON","array: "+array.toString());
                    //Routes is a combination of objects and arrays
                    //JSONObject routes = array.getJSONObject(0);
                    //Log.d("JSON","routes: "+routes.toString());
                    //JSONArray legs = routes.getJSONArray("legs");
                    //Log.d("JSON","legs: "+legs.toString());
                    //JSONObject steps = legs.getJSONObject(0);
                    //JSONObject distance = steps.getJSONObject("distance");
            /*JSONObject steps = legs.getJSONObject(0);
            //Log.d("JSON","steps: "+steps.toString());
            JSONObject distance = steps.getJSONObject("distance");
            //Log.d("JSON","distance: "+distance.toString());*/
                    sDistance = distance;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String finalSDistance = sDistance;
                handler.post(new Runnable() {
                    public void run() {
                    }
                });

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        private String request(String urlStr) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                        String line = null;
                        while(true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }

                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch(Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }

            return output.toString();
        }

    }
}
