package com.recom3.snow3.mobilesdk.social;

import android.util.Pair;

import java.util.List;

/**
 * Created by Recom3 on 18/03/2022.
 */


public class SocialStatsMessage$SocialStats {
    public Pair<Integer, Long> a;

    public Pair<Integer, Long> b;

    public Pair<Integer, Long> c;

    public Pair<Integer, Long> d;

    public Pair<Float, Long> e;

    public Pair<String, Long> f;

    public List<String> g;

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.b != null)
            stringBuilder.append(String.format("All time distance: %d at %d\n", new Object[] { this.b.first, this.b.second }));
        if (this.a != null)
            stringBuilder.append(String.format("All time altitude: %d at %d\n", new Object[] { this.a.first, this.a.second }));
        if (this.d != null)
            stringBuilder.append(String.format("All time max speed: %d at %d\n", new Object[] { this.d.first, this.d.second }));
        if (this.c != null)
            stringBuilder.append(String.format("All time vertical: %d at %d\n", new Object[] { this.c.first, this.c.second }));
        if (this.e != null)
            stringBuilder.append(String.format("All time air: %f at %d\n", new Object[] { this.e.first, this.e.second }));
        if (this.f != null)
            stringBuilder.append(String.format("Sports Activity: %s at %d\n", new Object[] { this.f.first, this.f.second }));
        return stringBuilder.toString();
    }
}

