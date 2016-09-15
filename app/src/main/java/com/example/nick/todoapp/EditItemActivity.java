package com.example.nick.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etEditItem;

    String content;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        content = getIntent().getStringExtra("content");
        position = getIntent().getIntExtra("position",0);

        etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.append(content);
    }

    public void onSave(View view) {
        Intent data = new Intent();
        data.putExtra("content",etEditItem.getText().toString());
        data.putExtra("position",position);
        setResult(RESULT_OK, data);
        finish();
    }
}
