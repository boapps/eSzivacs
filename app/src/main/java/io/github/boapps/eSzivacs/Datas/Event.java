package io.github.boapps.eSzivacs.Datas;

import java.util.Date;

/**
 * Created by boa on 28/10/17.
 */

public class Event {
    private int id;
    private Date date;
    private String content;

    public Event(int id, Date date, String content) {
        this.id = id;
        this.date = date;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
