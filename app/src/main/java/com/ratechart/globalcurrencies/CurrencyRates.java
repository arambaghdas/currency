package com.ratechart.globalcurrencies;

import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ratechart.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CurrencyRates {

    private Context context;

    public interface EventListener {
        void OnServerResponse(String status, JSONArray... response);
    }
    private EventListener listener;
    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }
    public CurrencyRates(Context context) {
        this.context = context;
    }

    public void get_currency_rates(String startDate, String endDate) {
        String token = context.getResources().getString(R.string.token);
        String symbol = context.getResources().getString(R.string.symbol);

        JSONObject obj = new JSONObject();
        try {
            obj.put("Symbol", symbol);
            obj.put("StartDate", startDate);
            obj.put("EndDate", endDate);
            obj.put("_Token", token);
            send_request(obj);
        } catch (JSONException e) {
            listener.OnServerResponse("Fail");
            e.printStackTrace();
        }
    }

    private  void send_request(JSONObject obj) {
        Iterator<?> keys = obj.keys();
        String url_post = context.getString(R.string.api_url);
        boolean isFirst = true;

        while( keys.hasNext() ){
            String key = (String)keys.next();
            try {
                String value = obj.getString(key);
                if (isFirst)
                    url_post = url_post + "?";
                else
                    url_post = url_post + "&";
                url_post = url_post + key + "=" + value;
                isFirst = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONArray js = new JSONArray();
        js.put(obj);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url_post, js,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        listener.OnServerResponse("Success", jsonArray);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                         listener.OnServerResponse("Fail");
                    }
                });
        RetryPolicy policy = new DefaultRetryPolicy(20000, 0, 1);
        request.setRetryPolicy(policy);
        queue.add(request);
    }
}
