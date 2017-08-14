package com.tidalsolutions.magic89_9;

/**
 * Created by Jeffrey on 2/25/2016.
 */
public class Parser {

    public static String StringSplit(String string) {
        String splitted_string;

            String[] separated = string.split(" ");

            if (!separated[0].equals("0")) {
                splitted_string = separated[0] + " " + separated[1];
            } else {
                splitted_string = separated[2] + " " + separated[3];
            }

        return splitted_string;
    }
}
