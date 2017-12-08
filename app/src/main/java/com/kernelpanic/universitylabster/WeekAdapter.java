package com.kernelpanic.universitylabster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        return convertView;
    }

    public String getItem(int x) {
        return days[x];
    }
}
