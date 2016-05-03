package com.petdoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by STU on 2016-05-03.
 */
public class DocItemListAdapter extends BaseAdapter {
    private Context mContext;

    MainActivity main;

    public DocItemListAdapter(Context context, ArrayList<DocItem> datas){

        main.docItemArrayList = datas;
        mContext = context;
    }

    public DocItemListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return main.docItemArrayList.size();

    }

    @Override
    public Object getItem(int position) {
        return main.docItemArrayList.get(position);
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

        DocItem docItem = main.docItemArrayList.get(position);
        // 이미지 추가 해야함
        // docImage.setImage
        docTitle.setText(docItem.getTitle());
        docAddress.setText(docItem.getAddress());
        docPhone.setText(docItem.getPhone());

        return convertView;
    }
}
