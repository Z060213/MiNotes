package net.micode.notes.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import net.micode.notes.R;
import net.micode.notes.data.Notes;

public class TrashActivity extends Activity {
    private ListView listView;
    private TrashAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        listView = findViewById(R.id.list_view_trash);
        Button btnEmpty = findViewById(R.id.btn_empty_trash);

        loadData();

        btnEmpty.setOnClickListener(v -> {
            getContentResolver().delete(Notes.CONTENT_TRASH_URI, null, null);
            refreshCursor();
            Toast.makeText(TrashActivity.this, "回收站已清空", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadData() {
        cursor = getContentResolver().query(Notes.CONTENT_TRASH_URI,
                null, null, null, "deleted_time DESC");
        adapter = new TrashAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    public void refreshCursor() {
        Cursor newCursor = getContentResolver().query(Notes.CONTENT_TRASH_URI,
                null, null, null, "deleted_time DESC");
        adapter.changeCursor(newCursor);
        if (cursor != null) cursor.close();
        cursor = newCursor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}