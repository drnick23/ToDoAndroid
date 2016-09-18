package com.example.nick.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Item> todoItems;
    ArrayAdapter<Item> aToDoAdapter;
    ListView lvItems;
    EditText etEditText;

    private final int EDIT_ITEM_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateArrayItems();

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);

        etEditText = (EditText) findViewById(R.id.etEditText);

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deletePosition(position);
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CLICK","onItemClick");
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                Item item = todoItems.get(position);
                i.putExtra("content",item.text);
                i.putExtra("position",position);
                startActivityForResult(i,EDIT_ITEM_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RESULT","onActivityResult");
        if (resultCode == RESULT_OK && requestCode == EDIT_ITEM_REQUEST_CODE) {
            // get our content and position back
            String content = data.getExtras().getString("content");
            int position = data.getExtras().getInt("position",0);

            updatePosition(position, content);

            Toast.makeText(this,"Item updated", Toast.LENGTH_SHORT).show();
        }
    }

    protected void deletePosition(int position) {
        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        Item removeItem = todoItems.get(position);
        databaseHelper.deleteItem(removeItem);

        todoItems.remove(position);
        aToDoAdapter.notifyDataSetChanged();
    }

    protected void updatePosition(int position, String text) {
        Item updateItem = todoItems.get(position);
        updateItem.text = text;
        todoItems.set(position, updateItem);

        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        databaseHelper.updateItem(updateItem);

        aToDoAdapter.notifyDataSetChanged();
    }

    public void populateArrayItems() {

        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);

        todoItems = databaseHelper.getAllItems();

        aToDoAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, todoItems);

    }

    public void onAddItem(View view) {

        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        Item item = databaseHelper.createItem(etEditText.getText().toString());

        aToDoAdapter.add(item);
        etEditText.setText("");

    }


}
