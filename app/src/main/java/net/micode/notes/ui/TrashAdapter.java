package net.micode.notes.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import net.micode.notes.R;
import net.micode.notes.data.Notes;

public class TrashAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public TrashAdapter(Context context, Cursor c) {
        super(context, c, false);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.trash_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvSnippet = view.findViewById(R.id.tv_snippet);
        TextView tvDeleteTime = view.findViewById(R.id.tv_delete_time);
        Button btnRestore = view.findViewById(R.id.btn_restore);
        Button btnDeleteForever = view.findViewById(R.id.btn_delete_forever);

        final long noteId = cursor.getLong(cursor.getColumnIndex(Notes.NoteColumns.ID));
        String snippet = cursor.getString(cursor.getColumnIndex(Notes.NoteColumns.SNIPPET));
        long deletedTime = cursor.getLong(cursor.getColumnIndex("deleted_time"));

        String title = snippet.length() > 20 ? snippet.substring(0, 20) : snippet;
        if (title.isEmpty()) title = "无标题笔记";
        tvTitle.setText(title);
        tvSnippet.setText(snippet);
        tvDeleteTime.setText("删除于 " + formatTime(deletedTime));

        btnRestore.setOnClickListener(v -> {
            ContentValues values = new ContentValues();
            values.put("is_deleted", 0);
            values.put("deleted_time", 0);
            Uri uri = Uri.withAppendedPath(Notes.CONTENT_TRASH_URI, String.valueOf(noteId));
            context.getContentResolver().update(uri, values, null, null);
            ((TrashActivity) context).refreshCursor();
        });

        btnDeleteForever.setOnClickListener(v -> {
            Uri uri = Uri.withAppendedPath(Notes.CONTENT_TRASH_URI, String.valueOf(noteId));
            context.getContentResolver().delete(uri, null, null);
            ((TrashActivity) context).refreshCursor();
        });
    }

    private String formatTime(long time) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(time));
    }
}