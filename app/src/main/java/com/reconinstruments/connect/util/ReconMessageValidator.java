package com.reconinstruments.connect.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by recom3 on 21/08/2023.
 */

public class ReconMessageValidator {
    Matcher mRegexMatcher;
    StringBuilder sb = new StringBuilder();
    Pattern mRegexPattern = Pattern.compile("(<recon.+?</recon>)", 32);

    public void reset() {
        this.sb = new StringBuilder();
    }

    public void appendString(String s) {
        this.sb.append(s);
    }

    public void appendString(byte[] b, int len) {
        String s = new String(b, 0, len);
        this.sb.append(s);
    }

    public String validate() {
        this.mRegexMatcher = this.mRegexPattern.matcher(this.sb.toString());
        if (this.mRegexMatcher.find()) {
            String s = this.mRegexMatcher.group(0);
            this.sb.delete(this.mRegexMatcher.start(), this.mRegexMatcher.end());
            return s;
        }
        return null;
    }
}
