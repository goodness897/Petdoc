package com.petdoc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by STU on 2016-05-20.
 */
public class SearchActivity extends AppCompatActivity {
    private EditText editText;
    private ListView listView;
    private List<String> items;
    private ArrayAdapter adapter;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editText = (EditText)findViewById(R.id.searchEditText);
        ab = this.getSupportActionBar();
        setCustomActionBar(ab);
        editText.setInputType(0);
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                editText.setInputType(1);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        listView = (ListView)findViewById(R.id.search_listView);
        items = new ArrayList<String>();

    }
    public void searchButtonClicked(View view) {
        items.clear();
        for(int i = 0; i < LoadingActivity.docItemArrayList.size(); i++) {
            if(LoadingActivity.docItemArrayList.get(i).getTitle().contains(editText.getText().toString())) {
                items.add(LoadingActivity.docItemArrayList.get(i).getTitle());
            }
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected;
                selected = parent.getItemAtPosition(position).toString();
                getSelectedID(selected);
            }
        });
    }
    private void getSelectedID(String item){
        for(int i = 0; i < LoadingActivity.docItemArrayList.size(); i++){
            if(LoadingActivity.docItemArrayList.get(i).getTitle() == item){
                int id = LoadingActivity.docItemArrayList.get(i).getId();
                Intent intent = new Intent(getApplicationContext(), DocDetailActivity.class);
                intent.putExtra("position", id-1);
                startActivity(intent);
            }
        }
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

}
