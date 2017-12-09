package com.kernelpanic.universitylabster.models;


/**
 * Created by andrei on 08.12.2017.
 */

enum EventType { COURSE, LABORATORY }

public class Event {
    public int id, day, year, up;
    public String location;
    public String teacher;
    public String name;
    public String time;
    public long date;
    public EventType eventType;
}
