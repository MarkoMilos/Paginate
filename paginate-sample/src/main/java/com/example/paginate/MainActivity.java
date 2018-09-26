package com.example.paginate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startRecyclerViewExample(View view) {
        startActivity(new Intent(this, RecyclerViewExampleActivity.class));
    }

    public void startAbsListViewExample(View view) {
        startActivity(new Intent(this, AbsListViewExampleActivity.class));
    }

}