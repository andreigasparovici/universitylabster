package com.kernelpanic.universitylabster;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by andrei on 08.12.2017.
 */

public class WeekAdapter extends ArrayAdapter<String>{
    public static String[] days = new String[]{"Luni", "Marţi", "Miercuri", "Joi", "Vineri"};

    public WeekAdapter(Context context) {
        super(context, 0, days);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String day = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_week, parent, false);
        }
        TextView dayView = convertView.findViewById(R.id.day);

        dayView.setText(day);

//        Date date = new Date();
//
//        Log.e("DATEBUG", String.valueOf(date.getDay()) + " " +String.valueOf(position));
//
//        if(date.getDay() == position) {
//            convertView.setBackgroundColor(Color.rgb(152,251,152));
//        }

        return convertView;
    }

    public String getItem(int x) {
        return days[x];
    }
}
