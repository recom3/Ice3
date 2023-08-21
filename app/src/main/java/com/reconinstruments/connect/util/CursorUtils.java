package com.reconinstruments.connect.util;

import android.database.Cursor;

/**
 * Created by recom3 on 20/08/2023.
 */

public class CursorUtils {
    public static boolean checkCursor(Cursor cursor) {
        return cursor != null && cursor.moveToFirst();
    }

    public static String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }
}