package com.petdoc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by STU on 2016-05-03.
 */
public class ListFragment extends Fragment {

    MainActivity main;
    ListView listView;
    DocItemListAdapter adapter;

    public static interface ListItemSelectionCallback {
        public void onListItemSelected(int position);
    }
    public ListItemSelectionCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ListItemSelectionCallback){
            callback = (ListItemSelectionCallback) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        adapter = new DocItemListAdapter(getContext(), main.docItemArrayList);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback != null) {
                    callback.onListItemSelected(position);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("MainActivity","Fragment onResume 실행");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MainActivity","Fragment onPause 실행");
    }
}
