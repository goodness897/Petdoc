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
    ArrayList<String> distances = null;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MainActivity","ListFragment onCreateView 실행");

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        //adapter = new DocItemListAdapter(getContext(), loadingActivity.docItemArrayList);
        /*adapter = new DocItemListAdapter(getContext(), MapFragment.doc_list);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DocDetailActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });*/
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("MainActivity","ListFragment onResume 실행");
        adapter = new DocItemListAdapter(getContext(), MapFragment.doc_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int select_id = MapFragment.doc_list.get(position).getId();
                Intent intent = new Intent(getContext(), DocDetailActivity.class);
                Log.i("MainActivity","select_id:"+ select_id);
                if(select_id > 1) {
                    intent.putExtra("position", select_id - 1);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MainActivity","ListFragment onPause 실행");
    }

    public void setSeletedItem(int id){
        Log.i("MainActivity","ListFragment id : " + id);
        for(int i = 0; i < MapFragment.doc_list.size(); i++){
            if(MapFragment.doc_list.get(i).getId() == id){
                mSelectedItem = i;
                Log.i("MainActivity","ListFragment position : " + mSelectedItem);
            }
        }
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(mSelectedItem, true);
        listView.smoothScrollToPosition(mSelectedItem);
        adapter.notifyDataSetChanged();
    }
    public class DocItemListAdapter extends BaseAdapter {
        private Context mContext;
        LoadingActivity loadingActivity;
        private ArrayList<DocItem> list;

        public DocItemListAdapter(Context context, ArrayList<DocItem> datas){
            list = datas;
            mContext = context;
        }

        public DocItemListAdapter(Context context) {
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
                convertView = inflater.inflate(R.layout.listitem, parent, false);
            }
            ImageView docImage = (ImageView) convertView.findViewById(R.id.dataImage);
            TextView docTitle = (TextView) convertView.findViewById(R.id.dataTitle);
            TextView docAddress = (TextView) convertView.findViewById(R.id.dataAddress);
            TextView docPhone = (TextView) convertView.findViewById(R.id.dataPhone);
            DocItem docItem = list.get(position);
            // 이미지 추가 해야함
            // docImage.setImage
            docTitle.setText(docItem.getTitle());
            docAddress.setText(docItem.getAddress());
            // docPhone.setText(docItem.getPhone());
            //docPhone.setText(distance);
            // docPhone.setText(docItem.getDistance());
            notifyDataSetChanged();
            return convertView;
        }
    }
}
