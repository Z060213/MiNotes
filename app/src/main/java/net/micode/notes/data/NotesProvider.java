/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import net.micode.notes.R;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.data.NotesDatabaseHelper.TABLE;

/**
 * 小米便签的 ContentProvider，负责统一管理对 SQLite 数据库的访问。
 * 对外通过 URI 匹配提供笔记、文件夹、数据等资源的增、删、改、查接口。
 * 内部使用 UriMatcher 解析请求，将操作分发到对应的数据库表。
 */
public class NotesProvider extends ContentProvider {
    /** URI 匹配器，用于识别不同的请求类型 */
    private static final UriMatcher mMatcher;

    /** 数据库辅助类实例 */
    private NotesDatabaseHelper mHelper;

    private static final String TAG = "NotesProvider";

    /** URI 类型：笔记集合 */
    private static final int URI_NOTE            = 1;
    /** URI 类型：单条笔记 */
    private static final int URI_NOTE_ITEM       = 2;
    /** URI 类型：数据集合 */
    private static final int URI_DATA            = 3;
    /** URI 类型：单条数据 */
    private static final int URI_DATA_ITEM       = 4;

    /** URI 类型：搜索 */
    private static final int URI_SEARCH          = 5;
    /** URI 类型：搜索建议 */
    private static final int URI_SEARCH_SUGGEST  = 6;

    static {
        // 初始化 URI 匹配器，注册所有支持的 URI 格式
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(Notes.AUTHORITY, "note", URI_NOTE);
        mMatcher.addURI(Notes.AUTHORITY, "note/#", URI_NOTE_ITEM);
        mMatcher.addURI(Notes.AUTHORITY, "data", URI_DATA);
        mMatcher.addURI(Notes.AUTHORITY, "data/#", URI_DATA_ITEM);
        mMatcher.addURI(Notes.AUTHORITY, "search", URI_SEARCH);
        mMatcher.addURI(Notes.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, URI_SEARCH_SUGGEST);
        mMatcher.addURI(Notes.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", URI_SEARCH_SUGGEST);
    }

    /**
     * 搜索结果的投影列定义。
     * x'0A' 代表 SQLite 中的换行符 '\n'，为了让搜索结果展示更多信息，会去除标题和内容中的换行符及空白。
     */
    private static final String NOTES_SEARCH_PROJECTION = NoteColumns.ID + ","
            + NoteColumns.ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA + ","
            + "TRIM(REPLACE(" + NoteColumns.SNIPPET + ", x'0A','')) AS " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ","
            + "TRIM(REPLACE(" + NoteColumns.SNIPPET + ", x'0A','')) AS " + SearchManager.SUGGEST_COLUMN_TEXT_2 + ","
            + R.drawable.search_result + " AS " + SearchManager.SUGGEST_COLUMN_ICON_1 + ","
            + "'" + Intent.ACTION_VIEW + "' AS " + SearchManager.SUGGEST_COLUMN_INTENT_ACTION + ","
            + "'" + Notes.TextNote.CONTENT_TYPE + "' AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA;

    /** 用于搜索笔记摘要的 SQL 查询语句 */
    private static String NOTES_SNIPPET_SEARCH_QUERY = "SELECT " + NOTES_SEARCH_PROJECTION
            + " FROM " + TABLE.NOTE
            + " WHERE " + NoteColumns.SNIPPET + " LIKE ?"
            + " AND " + NoteColumns.PARENT_ID + "<>" + Notes.ID_TRASH_FOLER
            + " AND " + NoteColumns.TYPE + "=" + Notes.TYPE_NOTE;

    /**
     * Provider 初始化时调用，创建数据库并建表。
     * 表结构主要包括：笔记/文件夹表、数据表（文本笔记、通话记录等）。
     * 同时初始化系统预置文件夹（根目录、回收站等）。
     */
    @Override
    public boolean onCreate() {
        mHelper = NotesDatabaseHelper.getInstance(getContext());
        return true;
    }

    /**
     * 处理各种 URI 的查询请求。
     * 根据 UriMatcher 的匹配结果，构建不同的 SQL 查询语句。
     * 支持的 URI 包括：笔记/文件夹、具体笔记、数据、搜索等。
     * @return 返回查询结果的 Cursor
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor c = null;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String id = null;
        switch (mMatcher.match(uri)) {
            case URI_NOTE:
                // 查询所有笔记/文件夹
                c = db.query(TABLE.NOTE, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case URI_NOTE_ITEM:
                // 根据 ID 查询单条笔记
                id = uri.getPathSegments().get(1);
                c = db.query(TABLE.NOTE, projection, NoteColumns.ID + "=" + id
                        + parseSelection(selection), selectionArgs, null, null, sortOrder);
                break;
            case URI_DATA:
                // 查询所有数据条目
                c = db.query(TABLE.DATA, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case URI_DATA_ITEM:
                // 根据 ID 查询单条数据
                id = uri.getPathSegments().get(1);
                c = db.query(TABLE.DATA, projection, DataColumns.ID + "=" + id
                        + parseSelection(selection), selectionArgs, null, null, sortOrder);
                break;
            case URI_SEARCH:
            case URI_SEARCH_SUGGEST:
                // 搜索请求，不允许额外指定排序或投影
                if (sortOrder != null || projection != null) {
                    throw new IllegalArgumentException(
                            "do not specify sortOrder, selection, selectionArgs, or projection" + "with this query");
                }

                String searchString = null;
                if (mMatcher.match(uri) == URI_SEARCH_SUGGEST) {
                    if (uri.getPathSegments().size() > 1) {
                        searchString = uri.getPathSegments().get(1);
                    }
                } else {
                    searchString = uri.getQueryParameter("pattern");
                }

                if (TextUtils.isEmpty(searchString)) {
                    return null;
                }

                // 构建模糊搜索条件
                try {
                    searchString = String.format("%%%s%%", searchString);
                    c = db.rawQuery(NOTES_SNIPPET_SEARCH_QUERY,
                            new String[] { searchString });
                } catch (IllegalStateException ex) {
                    Log.e(TAG, "got exception: " + ex.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (c != null) {
            // 设置数据变化通知 URI
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    /**
     * 插入新数据（笔记或数据条目）。
     * @return 新插入行的 URI
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long dataId = 0, noteId = 0, insertedId = 0;
        switch (mMatcher.match(uri)) {
            case URI_NOTE:
                // 插入笔记
                insertedId = noteId = db.insert(TABLE.NOTE, null, values);
                break;
            case URI_DATA:
                // 插入数据条目，必须携带 NOTE_ID
                if (values.containsKey(DataColumns.NOTE_ID)) {
                    noteId = values.getAsLong(DataColumns.NOTE_ID);
                } else {
                    Log.d(TAG, "Wrong data format without note id:" + values.toString());
                }
                insertedId = dataId = db.insert(TABLE.DATA, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // 通知笔记列表更新
        if (noteId > 0) {
            getContext().getContentResolver().notifyChange(
                    ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId), null);
        }

        // 通知数据列表更新
        if (dataId > 0) {
            getContext().getContentResolver().notifyChange(
                    ContentUris.withAppendedId(Notes.CONTENT_DATA_URI, dataId), null);
        }

        return ContentUris.withAppendedId(uri, insertedId);
    }

    /**
     * 删除符合条件的笔记或数据。
     * @return 被删除的行数
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String id = null;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        boolean deleteData = false;
        switch (mMatcher.match(uri)) {
            case URI_NOTE:
                // 删除所有匹配的笔记（但保留 ID>0，避免删除系统文件夹）
                selection = "(" + selection + ") AND " + NoteColumns.ID + ">0 ";
                count = db.delete(TABLE.NOTE, selection, selectionArgs);
                break;
            case URI_NOTE_ITEM:
                id = uri.getPathSegments().get(1);
                /** 系统文件夹 ID <= 0，不允许删除 */
                long noteId = Long.valueOf(id);
                if (noteId <= 0) {
                    break;
                }
                count = db.delete(TABLE.NOTE,
                        NoteColumns.ID + "=" + id + parseSelection(selection), selectionArgs);
                break;
            case URI_DATA:
                count = db.delete(TABLE.DATA, selection, selectionArgs);
                deleteData = true;
                break;
            case URI_DATA_ITEM:
                id = uri.getPathSegments().get(1);
                count = db.delete(TABLE.DATA,
                        DataColumns.ID + "=" + id + parseSelection(selection), selectionArgs);
                deleteData = true;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (count > 0) {
            // 如果删除了数据，通知笔记列表更新
            if (deleteData) {
                getContext().getContentResolver().notifyChange(Notes.CONTENT_NOTE_URI, null);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    /**
     * 更新符合条件的笔记或数据。
     * @return 被更新的行数
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        String id = null;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        boolean updateData = false;
        switch (mMatcher.match(uri)) {
            case URI_NOTE:
                // 更新笔记前，先自增版本号
                increaseNoteVersion(-1, selection, selectionArgs);
                count = db.update(TABLE.NOTE, values, selection, selectionArgs);
                break;
            case URI_NOTE_ITEM:
                id = uri.getPathSegments().get(1);
                increaseNoteVersion(Long.valueOf(id), selection, selectionArgs);
                count = db.update(TABLE.NOTE, values, NoteColumns.ID + "=" + id
                        + parseSelection(selection), selectionArgs);
                break;
            case URI_DATA:
                count = db.update(TABLE.DATA, values, selection, selectionArgs);
                updateData = true;
                break;
            case URI_DATA_ITEM:
                id = uri.getPathSegments().get(1);
                count = db.update(TABLE.DATA, values, DataColumns.ID + "=" + id
                        + parseSelection(selection), selectionArgs);
                updateData = true;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (count > 0) {
            // 如果更新了数据，通知笔记列表更新
            if (updateData) {
                getContext().getContentResolver().notifyChange(Notes.CONTENT_NOTE_URI, null);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    /**
     * 将附加的 selection 条件用 " AND (...)" 包裹，用于拼接 SQL。
     */
    private String parseSelection(String selection) {
        return (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
    }

    /**
     * 自增笔记的版本号（VERSION），用于同步判定数据变更。
     * @param id          笔记 ID，小于等于 0 时使用 selection 条件
     * @param selection   额外条件
     * @param selectionArgs 条件的参数
     */
    private void increaseNoteVersion(long id, String selection, String[] selectionArgs) {
        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(TABLE.NOTE);
        sql.append(" SET ");
        sql.append(NoteColumns.VERSION);
        sql.append("=" + NoteColumns.VERSION + "+1 ");

        if (id > 0 || !TextUtils.isEmpty(selection)) {
            sql.append(" WHERE ");
        }
        if (id > 0) {
            sql.append(NoteColumns.ID + "=" + String.valueOf(id));
        }
        if (!TextUtils.isEmpty(selection)) {
            String selectString = id > 0 ? parseSelection(selection) : selection;
            for (String args : selectionArgs) {
                selectString = selectString.replaceFirst("\\?", args);
            }
            sql.append(selectString);
        }

        mHelper.getWritableDatabase().execSQL(sql.toString());
    }

    @Override
    public String getType(Uri uri) {
        // 暂未实现 MIME 类型返回
        return null;
    }
}