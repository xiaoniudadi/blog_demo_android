package com.paulniu.imagetextview;


import androidx.annotation.Nullable;

/**
 * Created by shixiaoming on 17/9/8.
 */

public class TextUtil {
    public static boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
