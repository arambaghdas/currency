package com.ratechart.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.util.ArrayList;

public class XAxisValueFormatter implements IAxisValueFormatter {

    private ArrayList<String> values;
    public XAxisValueFormatter(ArrayList<String> values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if(values.size() > (int) value && value != -1) {
            if(values.size() == 1 && value == 1) {
                return "";
            }
            return values.get((int) value);
        } else return "";
    }

}

