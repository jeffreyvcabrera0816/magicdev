package com.tidalsolutions.magic89_9;

import android.widget.EditText;

/**
 * Created by Jeffrey on 2/12/2016.
 */
public class Validations {

    public boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
