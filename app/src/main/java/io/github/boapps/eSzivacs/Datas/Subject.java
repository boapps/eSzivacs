package io.github.boapps.eSzivacs.Datas;

/**
 * Created by boa on 28/10/17.
 */

public class Subject {

    private String subject;
    private String subjectName;
    private double value;
    private double classValue;
    private double diff;

    public Subject(String subject, String subjectName, double value, double classValue, double diff) {
        this.subject = subject;
        this.subjectName = subjectName;
        this.value = value;
        this.classValue = classValue;
        this.diff = diff;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getClassValue() {
        return classValue;
    }

    public void setClassValue(double classValue) {
        this.classValue = classValue;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

}
