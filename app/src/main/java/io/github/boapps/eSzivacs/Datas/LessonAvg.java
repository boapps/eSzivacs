package io.github.boapps.eSzivacs.Datas;

/**
 * Created by boa on 22/10/17.
 */

public class LessonAvg {

    private int id;
    private String name;
    private String teacher;
    private float avg;

    public LessonAvg(int id, String name, String teacher, float avg) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.avg = avg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

}
