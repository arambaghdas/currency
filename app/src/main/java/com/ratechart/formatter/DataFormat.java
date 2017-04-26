package com.ratechart.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFormat {

    public static String convert_data_to_str(Date date) {
        SimpleDateFormat sdf_date_format = new SimpleDateFormat("MM/dd/yyyy");
        return sdf_date_format.format(date);
    }
}
