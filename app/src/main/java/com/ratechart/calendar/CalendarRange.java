package com.ratechart.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.ratechart.R;
import com.ratechart.formatter.DataFormat;
import com.squareup.timessquare.CalendarPickerView;
import java.util.Calendar;
import java.util.Date;

public class CalendarRange extends Activity {

    Context context;
    String startDateStr;
    String endDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_range_activity);

        ImageButton calendarRangeSaveBtn;
        final CalendarPickerView calendar;
        Calendar prevYear, tomorrow;
        Date today;

        context = this;
        calendarRangeSaveBtn = (ImageButton)findViewById(R.id.calendarRangeSaveBtn);
        calendarRangeSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent("");
                intent.putExtra("startDate", startDateStr);
                intent.putExtra("endDate",  endDateStr);
                context.sendBroadcast(intent);
            }
        });
        prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -5);
        tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        startDateStr = DataFormat.convert_data_to_str(Calendar.getInstance().getTime());
        endDateStr = startDateStr;
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        today = new Date();
        calendar.init(prevYear.getTime(), tomorrow.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(today);
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateUnselected(Date date) {
            }

            @Override
            public void onDateSelected(Date date) {

                Date startDate, endDate;
                startDate = calendar.getSelectedDates().get(0);
                int size = calendar.getSelectedDates().size();
                if (size == 1)
                    endDate = startDate;
                else
                    endDate =  calendar.getSelectedDates().get(size - 1);
                startDateStr = DataFormat.convert_data_to_str(startDate);
                endDateStr = DataFormat.convert_data_to_str(endDate);
            }
        });
    }
}

