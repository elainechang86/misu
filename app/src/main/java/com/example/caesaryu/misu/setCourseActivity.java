package com.example.caesaryu.misu;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class setCourseActivity extends AppCompatActivity {
    private static SQLiteDatabase myCourseDBHelper;
    ArrayAdapter adapter;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<Integer> listItemsId = new ArrayList<>();
    ListView lv;
    private SQLiteDatabase myScoreDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_course);
        getSupportActionBar().hide();
        lv = findViewById(R.id.lv);
        myCourseDBHelper = new MyCourseDBHelper(setCourseActivity.this, "m107_1")
                .getWritableDatabase();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                listItems.remove(position);
                adapter.notifyDataSetChanged();
                String title = ((TextView) view).getText().toString();
                Cursor c = myCourseDBHelper.rawQuery("SELECT * FROM m107_1 WHERE name LIKE '" + title + "'", null);
                c.moveToFirst();
                int __id = c.getInt(0);
                String scoreTable = "__" + __id;
                try {
                    myCourseDBHelper.execSQL("DELETE FROM m107_1 WHERE name LIKE '" + title + "'");
                    try {
                        myScoreDBHelper = new MyDBHelper(setCourseActivity.this, scoreTable).getWritableDatabase();
                        myScoreDBHelper.execSQL("DELETE FROM m107_1 WHERE name LIKE '" + scoreTable + "'");
                    } catch (Exception e) {
                        Toast.makeText(setCourseActivity.this, "刪除" + scoreTable + " 失敗", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(setCourseActivity.this, "刪除" + title + " 失敗", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = lv.getItemAtPosition(position).toString();
                Intent intent = new Intent(setCourseActivity.this, subjectdetails.class);
                intent.putExtra("course_name", name);
                startActivity(intent);
            }
        });


        init();

    }

    /*   @Override
       public boolean onCreateOptionsMenu(Menu menu) {
           getMenuInflater().inflate(R.menu.menu_top, menu);
           return true;
       }

       @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           if (item.getItemId() == R.id.action_help)
               init();
           return super.onOptionsItemSelected(item);
       }*/
    public void menuClick(View v) {
        if (v.getId() == R.id.tvInsert) {
            Intent intent = new Intent(setCourseActivity.this, setPerCourseActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tvScore) {
            Intent intent = new Intent(setCourseActivity.this, scoreView.class);

            startActivity(intent);
        }
    }

    public void init() {
        listItems.clear();
        listItemsId.clear();
        Cursor c;
        c = myCourseDBHelper.rawQuery("SELECT * FROM m107_1", null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            listItems.add(c.getString(1));
            listItemsId.add(c.getInt(0));
            c.moveToNext();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCourseDBHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void clearDB(View v) {
        if (v.getId() == R.id.btn_CLEAR) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(setCourseActivity.this);
            builder.setTitle("確定清除?").setMessage("提醒!資料清除無法復原喔!")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.setPositiveButton("確定清除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Cursor reset=myCourseDBHelper.rawQuery("SELECT id FROM m107_1",null);
                    reset.moveToFirst();
                    do{
                        String deleteTable="__"+reset.getInt(0);
                        subjectdetails.dbrw.delete(deleteTable,"1",null);
                       // myCourseDBHelper.execSQL("DROP TABLE IF EXISTS "+deleteTable,null);
                    }while (reset.moveToNext());

                    if (myCourseDBHelper != null) {
                        MyCourseDBHelper.resetCourseDB(myCourseDBHelper);
                    }
                   // MyDBHelper.resetScoreDB(subjectdetails.dbrw);



                    myCourseDBHelper.execSQL("Create TABLE IF NOT EXISTS " + "m107_1" + "" +
                            "(id integer PRIMARY KEY AUTOINCREMENT," +
                            "name text NO NULL UNIQUE," +
                            "teacher text NO NULL," +
                            "credit integer NO NULL," +
                            "time text NO NULL," +
                            "classroom text NO NULL," +
                            "color text NO NULL)");
                    init();
                }
            });
            builder.show();
        }
    }

    /*private void dropAllUserTables(SQLiteDatabase db) {
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type IS 'table'" +
                        " AND name NOT IN ('sqlite_master', 'sqlite_sequence')",
                null
        );
        if (c.moveToFirst()) {
            do {
                Log.d("TEST", "dropAllUserTables: 0" + c.getString(c.getColumnIndex("name")));
                db.execSQL("DROP TABLE " + c.getString(c.getColumnIndex("name")));
            } while (c.moveToNext());
        }
    }
        /*Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        //noinspection TryFinallyCanBeTryWithResources not available with API < 19
        try {
            List<String> tables = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0));
            }

            for (String table : tables) {
                if (table.startsWith("sqlite_")) {
                    continue;
                }
                db.execSQL("DROP TABLE IF EXISTS " + table);

            }
        } finally {
            cursor.close();
        }
    }*/

}
