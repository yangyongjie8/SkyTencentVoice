package com.skyworthdigital.voice.baidu_module.stock;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.skyworthdigital.voice.baidu_module.R;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 股票类
 * Created by SDT03046 on 2017/6/23.
 */

public class Stock {
    private String TAG = Stock.class.getSimpleName();
    private StockData mStockData;
    private static final String NAME = "name";
    private static final String VALUE = "value";

    public Stock(String result) {
        mStockData = getStockData(result);
    }

    private StockData getStockData(String result) {
        StockData stockData = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonData = jsonObject.getJSONObject("result").getJSONObject("resource").getJSONObject("data");
            MLog.i(TAG, "data:" + jsonData.toString());
            Gson gson = new Gson();

            stockData = gson.fromJson(jsonData.toString(), StockData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockData;
    }

    private int[] mStock_items = {
            R.id.stock_item1,
            R.id.stock_item2,
            R.id.stock_item3,
            R.id.stock_item4,
            R.id.stock_item5,
            R.id.stock_item6,
    };

    public void show(View view) {
        TextView textName = (TextView) view.findViewById(R.id.stock_name);
        TextView textCode = (TextView) view.findViewById(R.id.code);
        TextView textCurPrice = (TextView) view.findViewById(R.id.current_price);
        TextView textChange1 = (TextView) view.findViewById(R.id.change1);
        TextView textChange2 = (TextView) view.findViewById(R.id.change2);

        StockResult firstResult = mStockData.getResultFirst();
        if (firstResult != null) {
            String change = firstResult.getChange();
            String code = firstResult.getCode();
            String current_price = firstResult.getCurrent_price();

            textName.setText(firstResult.getName());
            textCode.setText("(" + code + ")");
            if (change.contains("+")) {
                int redcolor = view.getResources().getColor(R.color.color_red);
                textChange1.setTextColor(redcolor);
                textChange2.setTextColor(redcolor);
                textCurPrice.setTextColor(redcolor);
            } else {
                int greencolor = view.getResources().getColor(R.color.color_green);
                textChange1.setTextColor(greencolor);
                textChange2.setTextColor(greencolor);
                textCurPrice.setTextColor(greencolor);
            }
            textCurPrice.setText(current_price);

            change = change.replace("(", ",");
            change = change.replace(")", "");

            textChange1.setText(change.split(",")[0]);
            textChange2.setText(change.split(",")[1]);
            MLog.i(TAG, "chaneg:" + change.split(",")[0] + " " + change.split(",")[1]);

            for (int temp : mStock_items) {
                TextView textview = (TextView) view.findViewById(temp);
                textview.setText("");
            }

            List<LinkedTreeMap> infolist = (ArrayList<LinkedTreeMap>) firstResult.getInfo();
            int j = 0;
            for (int i = 0; i < infolist.size(); i++) {
                String name = "";
                String value = "";
                LinkedTreeMap lhsMap = infolist.get(i);
                Iterator lit = lhsMap.entrySet().iterator();
                while (lit.hasNext()) {
                    Map.Entry e = (Map.Entry) lit.next();
                    MLog.i(TAG, "Key: " + e.getKey() + "--Value: "
                            + e.getValue());
                    if (TextUtils.equals((String) e.getKey(), NAME)) {
                        name = (String) e.getValue();
                    }
                    if (TextUtils.equals((String) e.getKey(), VALUE)) {
                        value = (String) e.getValue();
                    }
                }
                if (i < mStock_items.length) {
                    TextView textview = (TextView) view.findViewById(mStock_items[j]);
                    textview.setText(name + " : " + value);
                    j = j + 1;
                }

            }
        }
    }

}
