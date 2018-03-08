package io.github.boapps.eSzivacs.Datas;

/**
 * Created by boa on 28/10/17.
 */

public class Note {


    private int id;
    private String type;
    private String title;
    private String content;
    private String teacher;
    private String date;
    private String creationDate;

    public Note(int id, String type, String title, String content, String teacher, String date, String creationDate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.teacher = teacher;
        this.date = date;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
