package io.github.boapps.eSzivacs.Datas;

/**
 * Created by boa on 23/09/17.
 */

public class Evaluation {
    private int id;
    private String form;
    private String range;
    private String type;
    private String subject;
    private String subjectCategory;
    private String mode;
    private String weight;
    private String value;
    private String numericValue;
    private String teacher;
    private String date;
    private String creationDate;
    private String theme;

    public Evaluation(int id, String form, String range, String type, String subject, String subjectCategory, String mode, String weight, String value, String numericValue, String teacher, String date, String creationDate, String theme) {
        this.id = id;
        this.form = form;
        this.range = range;
        this.type = type;
        this.subject = subject;
        this.subjectCategory = subjectCategory;
        this.mode = mode;
        this.weight = weight;
        this.value = value;
        this.numericValue = numericValue;
        this.teacher = teacher;
        this.date = date;
        this.creationDate = creationDate;
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectCategory() {
        return subjectCategory;
    }

    public void setSubjectCategory(String subjectCategory) {
        this.subjectCategory = subjectCategory;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(String numericValue) {
        this.numericValue = numericValue;
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
