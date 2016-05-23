package com.petdoc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class DocDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private int position;
    MainActivity main;

    private String phone;
    private Button callButton;

    LoadingActivity loadingActivity;
    private ActionBar ab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_detail);

        ab = this.getSupportActionBar();
        setCustomActionBar(ab);

        titleTextView = (TextView) findViewById(R.id.dataTitle);
        addressTextView = (TextView) findViewById(R.id.dataAddress);
        callButton = (Button) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra("position", 0);
            setData(position);
        }

    }

    private void setData(int position) {

        if (loadingActivity.docItemArrayList != null && loadingActivity.docItemArrayList.size() != 0) {
            titleTextView.setText(loadingActivity.docItemArrayList.get(position).getTitle());
            addressTextView.setText(loadingActivity.docItemArrayList.get(position).getAddress());
            phone = loadingActivity.docItemArrayList.get(position).getPhone();
        }
    }

    public void reviewButtonClicked(View view) {

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
