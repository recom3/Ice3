package com.recom3.snow3.mobilesdk.mediaplayer;

import android.database.Cursor;

import com.recom3.snow3.mobilesdk.MediaPlayerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class ReconNextTrackFinder {
    public static final String TAG = "NextTrackFinder";

    private static boolean generateNewShuffleCursor = false;

    private static Cursor mPlayingCursor = null;

    private static List<Integer> mShuffleListCursor = null;

    private static int mShuffleListCursorIndex = 0;

    static int findNext(boolean paramBoolean, MediaPlayerService paramMediaPlayerService) {
        return (getPlayingCursor() == null) ? -1 : findNextByCursor(paramBoolean, paramMediaPlayerService);
    }

    private static int findNextByCursor(boolean paramBoolean, MediaPlayerService paramMediaPlayerService) {
        byte b = -1;
        Cursor cursor = mPlayingCursor;
        boolean bool1 = paramMediaPlayerService.getMediaManager().getMPlayer().isLoop();
        boolean bool2 = paramMediaPlayerService.getMediaManager().getMPlayer().isShuffle();
        int i = cursor.getCount();
        if (!bool2) {
            if (paramBoolean) {
                cursor.moveToNext();
            } else {
                cursor.moveToPrevious();
            }
            if (paramBoolean) {
                if (!cursor.isAfterLast())
                    return cursor.getInt(0);
                if (bool1) {
                    cursor.moveToFirst();
                    return cursor.getInt(0);
                }
                cursor.moveToLast();
                return b;
            }
            if (!cursor.isBeforeFirst())
                return cursor.getInt(0);
            if (bool1) {
                cursor.moveToLast();
                return cursor.getInt(0);
            }
            cursor.moveToFirst();
            return b;
        }
        if (generateNewShuffleCursor) {
            ArrayList<Integer> arrayList = new ArrayList();
            int j;
            for (j = 0; j < i; j++)
                arrayList.add(Integer.valueOf(j));
            Collections.shuffle(arrayList);
            j = cursor.getPosition();
            arrayList.set(arrayList.indexOf(Integer.valueOf(j)), arrayList.get(0));
            arrayList.set(0, Integer.valueOf(j));
            mShuffleListCursor = arrayList;
            mShuffleListCursorIndex = 1;
            generateNewShuffleCursor = false;
            j = b;
            if (paramBoolean) {
                cursor.moveToPosition(((Integer)mShuffleListCursor.get(1)).intValue());
                j = cursor.getInt(0);
            }
            return j;
        }
        if (paramBoolean) {
            mShuffleListCursorIndex++;
        } else {
            mShuffleListCursorIndex--;
        }
        if (paramBoolean) {
            if (mShuffleListCursorIndex < i) {
                cursor.moveToPosition(((Integer)mShuffleListCursor.get(mShuffleListCursorIndex)).intValue());
                return cursor.getInt(0);
            }
            if (bool1) {
                generateNewShuffleCursor = true;
                mShuffleListCursorIndex = 0;
                cursor.moveToPosition((new Random()).nextInt(i));
                return cursor.getInt(0);
            }
            mShuffleListCursorIndex = i - 1;
            return b;
        }
        if (mShuffleListCursorIndex >= 0) {
            cursor.moveToPosition(((Integer)mShuffleListCursor.get(mShuffleListCursorIndex)).intValue());
            return cursor.getInt(0);
        }
        if (bool1) {
            mShuffleListCursorIndex = i - 1;
            cursor.moveToPosition(((Integer)mShuffleListCursor.get(mShuffleListCursorIndex)).intValue());
            return cursor.getInt(0);
        }
        mShuffleListCursorIndex = 0;
        return b;
    }

    static void flagToGenerateNewShuffleCursor() {
        generateNewShuffleCursor = true;
    }

    static Cursor getPlayingCursor() {
        return mPlayingCursor;
    }

    static void setPlayingCursor(Cursor paramCursor) {
        mPlayingCursor = paramCursor;
    }
}