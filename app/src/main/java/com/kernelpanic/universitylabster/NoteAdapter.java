package com.kernelpanic.universitylabster;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kernelpanic.universitylabster.models.Course;
import com.kernelpanic.universitylabster.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 09.12.2017.
 */

public class NoteAdapter extends ArrayAdapter<Note> {
    List<Note> notes;

    public NoteAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.userName);
        TextView content = convertView.findViewById(R.id.content);

        userName.setText(note.user);
        content.setText(note.message);

        return convertView;
    }

    public Note getItem(int x) {
        return notes.get(x);
    }
}
