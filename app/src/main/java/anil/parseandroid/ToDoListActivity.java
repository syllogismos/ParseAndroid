package anil.parseandroid;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,DELETE_ID,0, R.string.menu_delete);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {
            return;
        }

        final Bundle extras = intent.getExtras();

        switch (requestCode) {
            case ACTIVITY_CREATE:
                new RemoteDataTask() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        String name = extras.getString("name");
                        ParseObject todo = new ParseObject("Todo");
                        todo.put("name", name);
                        try{
                            todo.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        super.doInBackground();
                        return null;
                    }
                }.execute();
                break;
            case ACTIVITY_EDIT:
                final ParseObject todo;
                todo = todos.get(extras.getInt("position"));
                todo.put("name", extras.getString("name"));

                new RemoteDataTask(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try{
                            todo.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return super.doInBackground(voids);
                    }
                }.execute();
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                final ParseObject todo = todos.get(info.position);

                new RemoteDataTask(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            todo.delete();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        super.doInBackground();
                        return null;
                    }
                }.execute();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case INSERT_ID:
                createTodo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, CreateTodo.class);

        i.putExtra("name", todos.get(position).getString("name").toString());
        i.putExtra("position", position);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
}
