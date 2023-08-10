package com.recom3.connect.music;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MusicListCursor extends CursorWrapper {
    Cursor cursor;

    String extraId;

    String extraTitle;

    boolean hasExtraRow = false;

    boolean onExtraRow = false;

    public MusicListCursor(Cursor paramCursor) {
        super(paramCursor);
        this.cursor = paramCursor;
    }

    public void addRow(String paramString1, String paramString2) {
        if (!this.hasExtraRow) {
            this.hasExtraRow = true;
            this.extraTitle = paramString1;
            this.extraId = paramString2;
        }
    }

    public int getCount() {
        return this.hasExtraRow ? (getCursorCount() + 1) : getCursorCount();
    }

    public int getCursorCount() {
        return (this.cursor == null) ? 0 : super.getCount();
    }

    public long getLong(int paramInt) {
        return (this.onExtraRow && this.hasExtraRow) ? 0L : super.getLong(paramInt);
    }

    public String getString(int paramInt) {
        if (this.onExtraRow && this.hasExtraRow) {
            switch (paramInt) {
                default:
                    return this.extraTitle;
                case 0:
                    break;
            }
            return this.extraId;
        }
        return super.getString(paramInt);
    }

    public boolean hasData() {
        return (this.cursor != null);
    }

    public boolean moveToPosition(int paramInt) {
        //null = true;
        if (this.hasExtraRow && paramInt >= 0) {
            if (paramInt == 0) {
                this.onExtraRow = true;
                //return null;
                //!!!!
                return false;
            }
            this.onExtraRow = false;
            return super.moveToPosition(paramInt - 1);
        }
        return super.moveToPosition(paramInt);
    }

    public boolean onExtraRow() {
        return (this.onExtraRow && this.hasExtraRow);
    }
}
