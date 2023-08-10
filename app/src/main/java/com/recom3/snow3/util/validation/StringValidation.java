package com.recom3.snow3.util.validation;

import android.util.Patterns;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class StringValidation {
    public static final boolean isEmpty(String paramString) {
        return !(!isNull(paramString) && !"".equals(paramString.trim()));
    }

    public static final boolean isNotEmpty(String paramString) {
        return !isEmpty(paramString);
    }

    public static final boolean isNotNull(String paramString) {
        return (paramString != null);
    }

    public static final boolean isNull(String paramString) {
        return (paramString == null);
    }

    public static final boolean isValidEmail(CharSequence paramCharSequence) {
        return (paramCharSequence == null || isEmpty(paramCharSequence.toString())) ? false : Patterns.EMAIL_ADDRESS.matcher(paramCharSequence).matches();
    }
}
