package com.petdoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_detail);

        titleTextView = (TextView) findViewById(R.id.dataTitle);
        addressTextView = (TextView) findViewById(R.id.dataAddress);
        phoneTextView = (TextView) findViewById(R.id.dataPhone);
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
            phoneTextView.setText(loadingActivity.docItemArrayList.get(position).getPhone());
            phone = loadingActivity.docItemArrayList.get(position).getPhone();
        }
    }

    public void reviewButtonClicked(View view) {

    }
}
