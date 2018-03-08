package io.github.boapps.eSzivacs.Datas;

import java.util.ArrayList;

/**
 * Created by boa on 24/09/17.
 */

public class Student {

    private int id;
    private String name;
    private String instName;
    private String instCode;
    private ArrayList<Evaluation> evaluations;
    private Teacher teacher;
    private Tutelary tutelary;

    public Student(int id, String name, String instName, String instCode, ArrayList<Evaluation> evaluations, Teacher teacher, Tutelary tutelary) {
        this.id = id;
        this.name = name;
        this.instName = instName;
        this.instCode = instCode;
        this.evaluations = evaluations;
        this.teacher = teacher;
        this.tutelary = tutelary;
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

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public ArrayList<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(ArrayList<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Tutelary getTutelary() {
        return tutelary;
    }

    public void setTutelary(Tutelary tutelary) {
        this.tutelary = tutelary;
    }
}
