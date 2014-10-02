package anil.parseandroid;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import anil.parseandroid.R;

public class ToDoListActivity extends ListActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    public static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private List<ParseObject> todos;
    private Dialog progressDialog;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            ToDoListActivity.this.progressDialog = ProgressDialog.show(ToDoListActivity.this, "", "loading...", true);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToDoListActivity.this, R.layout.todo_row);
            if (todos != null){
                for (ParseObject todo: todos){
                    adapter.add((String) todo.get("name"));
                }
            }
            setListAdapter(adapter);
            ToDoListActivity.this.progressDialog.dismiss();
            TextView empty = (TextView) findViewById(android.R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Todo");
            query.orderByDescending("_created_at");

            try {
                todos = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        TextView empty = (TextView) findViewById(android.R.id.empty);
        empty.setVisibility(View.INVISIBLE);

        new RemoteDataTask().execute();
        registerForContextMenu(getListView());
    }

    private void createTodo(){
        Intent i = new Intent(this, CreateTodo.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.to_do_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
