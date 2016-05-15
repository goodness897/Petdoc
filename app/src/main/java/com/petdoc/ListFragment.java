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
        listView.setItemChecked(position, true);
        listView.setSelection(position);
        listView.smoothScrollToPosition(position);
        adapter.notifyDataSetChanged();

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
            // 이미지 추가 해야함
            // docImage.setImage
            docTitle.setText(docItem.getTitle());
            docAddress.setText(docItem.getAddress());
            docPhone.setText(docItem.getPhone());
            return convertView;
        }
    }
}
