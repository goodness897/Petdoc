package com.petdoc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by STU on 2016-05-03.
 */
public class MapFragment extends Fragment {

    ImageView imageView;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        return rootView;

    }

}
