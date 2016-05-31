package com.petdoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PetcastActivity extends AppCompatActivity {

    ArrayList<PetcastItem> petcastItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petcast);

        ListView listView = (ListView)findViewById(R.id.listView);
        petcastItems.add(new PetcastItem(R.drawable.petcast1, "심장사상충이란?", "심장 사상충은 개와 고양이를 죽음으로.."));
        petcastItems.add(new PetcastItem(R.drawable.petcast2, "배워두면 유용한 멍냥이 생활사고 응급처치방법!", "어머! 이건 꼭 알아야해!"));
        petcastItems.add(new PetcastItem(R.drawable.petcast3, "집에서 체크해볼 외부기생충의 종류와 예방법", "봄이오면 필수적으로 알아두고 예방해야할 그것!"));
        PetcastItemListAdapter adapter = new PetcastItemListAdapter(this, petcastItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PetcastDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    public class PetcastItemListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<PetcastItem> list;

        public PetcastItemListAdapter(Context context, ArrayList<PetcastItem> datas){
            list = datas;
            mContext = context;
        }

        public PetcastItemListAdapter(Context context) {
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
                convertView = inflater.inflate(R.layout.petcast_item, parent, false);
            }
            ImageView petcastImage = (ImageView)convertView.findViewById(R.id.petcast_image);
            TextView petcastTitle = (TextView)convertView.findViewById(R.id.petcast_title);
            TextView petcastContent = (TextView)convertView.findViewById(R.id.petcast_content);

            PetcastItem docItem = list.get(position);
            petcastImage.setImageResource(list.get(position).getImageUrl());
            petcastTitle.setText(list.get(position).getTitle());
            petcastContent.setText(list.get(position).getContent());

            notifyDataSetChanged();
            return convertView;
        }
    }
}
