package com.tidalsolutions.magic89_9;

/**
 * Created by Jeffrey on 2/12/2016.
 */

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Jeffrey on 2/12/2016.
 */
@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText et_reg_bday;
    public DateDialog(View view){
        et_reg_bday=(EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
        int year = 2000;
        int month = 0;
        int day = 1;
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
//        int day_prepend;
        String day_prepend;
        String month_prepend;

        if ((month+1) < 10) {
            month_prepend = "0"+(month+1);
        } else {
            month_prepend = ""+(month+1);
        }

        if (day < 10) {
            day_prepend = "0"+day;
        } else {
            day_prepend = ""+day;
        }

        String date=year+"-"+month_prepend+"-"+day_prepend;
        et_reg_bday.setText(date);
    }
}
