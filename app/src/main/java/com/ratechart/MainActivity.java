package com.ratechart;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ratechart.calendar.CalendarRange;
import com.ratechart.formatter.DataFormat;
import com.ratechart.formatter.XAxisValueFormatter;
import com.ratechart.globalcurrencies.CurrencyRates;
import com.ratechart.network.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;

public class MainActivity extends AppCompatActivity {

    LineChart rateChart;
    Context context;
    String startDate;
    String endDate;
    TextView dateRangeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        String dateRange;
        Date date, newDate;
        Calendar calendar;
        ImageButton calendarButton;

        // Initialize chart params
        rateChart = (LineChart) findViewById(R.id.rateChart);
        dateRangeTextView = (TextView) findViewById(R.id.dateRangeTextView);
        rateChart.setDrawGridBackground(true);
        rateChart.setDrawBorders(false);
        rateChart.setEnabled(false);
        rateChart.setGridBackgroundColor(Color.WHITE);
        Description description = new Description();
        description.setText("");
        rateChart.setDescription(description);

        // Calendar: select dates to display charts
        date = Calendar.getInstance().getTime();
        endDate = DataFormat.convert_data_to_str(date);
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_WEEK, -7);
        newDate = calendar.getTime();
        startDate = DataFormat.convert_data_to_str(newDate);
        dateRange = startDate + "-" + endDate;
        dateRangeTextView.setText(dateRange);

        // Date selection receiver
        registerReceiver(calendarBroadcastReceiver, new IntentFilter(""));
        calendarButton = (ImageButton)findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalendarRange.class);
                startActivity(intent);
            }
        });

        // Load currency data
        load_currencies();
    }
    public void load_currencies() {
        if (Network.isNetworkConnected(context)) {

            final ProgressDialog progress = new ProgressDialog(context);
            final CurrencyRates currencyRates = new CurrencyRates(context);
            String progressText = getResources().getString(R.string.progress_text);

            // Show progress while loading data
            progress.setMessage(progressText);
            progress.show();

            // Retrieve currency rates from Xignite
            currencyRates.get_currency_rates(startDate, endDate);
            currencyRates.setEventListener(new CurrencyRates.EventListener() {
                @Override
                public void OnServerResponse(String status, JSONArray... response) {
                    progress.dismiss();
                    if (status.equals("Success")) {
                        draw_charts(response[0]);
                    } else {
                        Toast toast = Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    public void draw_charts(JSONArray rates)
    {
        ArrayList<Double> highExValues = new ArrayList<>();
        ArrayList<Double> lowExValues = new ArrayList<>();
        ArrayList<String> dateValues = new ArrayList<>();
        List<Entry> lowExEntries = new ArrayList<>();
        List<Entry> highExEntries = new ArrayList<>();
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        LineDataSet lowDataSet, highDataSet;
        LineData lineData;
        XAxis x;
        YAxis rightAxis;
        boolean status = true;

        for (int i = 0; i < rates.length(); i++) {
            try {
                JSONObject jsonObject = rates.getJSONObject(i);
                String outCome = jsonObject.getString("Outcome");
                if (outCome.equals("Success")) {
                    Double high = jsonObject.getDouble("High");
                    Double low = jsonObject.getDouble("Low");
                    String date = jsonObject.getString("StartDate");
                    String[] separated = date.split("/");
                    date = separated[0] + "/" + separated[1];
                    highExValues.add(high);
                    lowExValues.add(low);
                    dateValues.add(date);
                } else {
                    status = false;
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        if (status) {
            for (int i = 0; i < lowExValues.size(); i++) {
                lowExEntries.add(new Entry(i, lowExValues.get(i).floatValue()));
            }
            lowDataSet = new LineDataSet(lowExEntries, context.getResources().getString(R.string.low_ex));
            lowDataSet.setLineWidth(3);
            lowDataSet.setColor(ContextCompat.getColor(context, R.color.colorAccent));
            lowDataSet.setCircleColorHole(Color.WHITE);
            lowDataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorAccent));
            lowDataSet.setCircleRadius(5);
            lowDataSet.setCircleHoleRadius(2);
            lowDataSet.setHighlightEnabled(false);
            lowDataSet.setDrawValues(false);

            for (int i = 0; i < highExValues.size(); i++) {
                highExEntries.add(new Entry(i, highExValues.get(i).floatValue()));
            }

            highDataSet = new LineDataSet(highExEntries, context.getResources().getString(R.string.high_ex));
            highDataSet.setLineWidth(3);
            highDataSet.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            highDataSet.setCircleColorHole(Color.WHITE);
            highDataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorPrimary));
            highDataSet.setCircleRadius(5);
            highDataSet.setCircleHoleRadius(2);
            highDataSet.setHighlightEnabled(false);
            highDataSet.setDrawValues(false);

            dataSets.add(highDataSet);
            dataSets.add(lowDataSet);
            lineData = new LineData(dataSets);
            rateChart.setData(lineData);

            x = rateChart.getXAxis();
            x.setValueFormatter(new XAxisValueFormatter(dateValues));
            x.setGranularity(1f);
            x.setLabelRotationAngle(0);
            x.setPosition(BOTTOM);
            x.setDrawGridLines(false);

            rightAxis = rateChart.getAxisRight();
            rightAxis.setDrawLabels(false);
            rightAxis.setDrawAxisLine(false);
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawZeroLine(true);
            rateChart.getAxisRight().setEnabled(false);
            rateChart.invalidate();
            rateChart.fitScreen();
        } else {
            Toast toast = Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    BroadcastReceiver calendarBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");
            String dateRange = startDate + "-" + endDate;
            dateRangeTextView.setText(dateRange);
            load_currencies();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (calendarBroadcastReceiver != null)
            unregisterReceiver(calendarBroadcastReceiver);
    }
}
