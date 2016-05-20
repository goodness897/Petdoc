package com.petdoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
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
    public void setDistance(double latitude, double longitude){
        list_latitude = latitude;
        list_longitude = longitude;
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
            TextView docPhone = (TextView) convertView.findViewById(R.id.dataPhone);
            DocItem docItem = loadingActivity.docItemArrayList.get(position);
            String distance = GetDistance(list_latitude, list_longitude, position);
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

    private String GetDistance(double latitude, double longitude, int position) {
        int iDistance = 0;
        String sDistance = "";
        Log.d("xxx","latitude : "+latitude + ", longitude : " + longitude);
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");//from
        urlString.append(latitude);
        urlString.append(",");
        urlString.append(longitude);
        urlString.append("&destination=");//to
        urlString.append(LoadingActivity.docItemArrayList.get(position).getLatitude());
        urlString.append(",");
        urlString.append(LoadingActivity.docItemArrayList.get(position).getLongitude());
        urlString.append("&mode=walking&sensor=true");
        Log.d("xxx","URL="+urlString.toString());

        // get the JSON And parse it to get the directions data.
        HttpURLConnection urlConnection= null;
        URL url = null;

        try {
            url = new URL(urlString.toString());
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            InputStream inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

            String temp, response = "";
            while((temp = bReader.readLine()) != null){
                //Parse data
                response += temp;
            }
            //Close the reader, stream & connection
            bReader.close();
            inStream.close();
            urlConnection.disconnect();
            //Sortout JSONresponse
            JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray array = object.getJSONArray("routes");
            //Log.d("JSON","array: "+array.toString());

            //Routes is a combination of objects and arrays
            JSONObject routes = array.getJSONObject(0);
            //Log.d("JSON","routes: "+routes.toString());

            String summary = routes.getString("summary");
            //Log.d("JSON","summary: "+summary);

            JSONArray legs = routes.getJSONArray("legs");
            //Log.d("JSON","legs: "+legs.toString());

            JSONObject steps = legs.getJSONObject(0);
            //Log.d("JSON","steps: "+steps.toString());

            JSONObject distance = steps.getJSONObject("distance");
            //Log.d("JSON","distance: "+distance.toString());

            sDistance = distance.getString("text");
            iDistance = distance.getInt("value");
            Log.d("xxx","Distance : " + sDistance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sDistance;

    }
}
