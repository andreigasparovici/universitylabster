package com.kernelpanic.universitylabster.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.models.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 09.12.2017.
 */

public class MessageAdapter extends ArrayAdapter<Message> {
    private List<Message> messageList;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.messageList = messages;
    }

    @Override
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {
        Message message = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);

        TextView userName = convertView.findViewById(R.id.userName);
        TextView content = convertView.findViewById(R.id.content);

        if (message == null) return convertView;

        userName.setText(message.message);
        content.setText(message.user);

        return convertView;
    }

    public Message getItem(int index) {
        return messageList.get(index);
    }
}
