package io.github.boapps.eSzivacs.Datas;

/**
 * Created by boa on 28/10/17.
 */

public class Absence {
    private int id;
    private String type;
    private String typeName;
    private String mode;
    private String modeName;
    private String subject;
    private String subjectName;
    private String delayMinutes;
    private String teacher;
    private String startTime;
    private String creationTime;
    private int lessonNumber;
    private String justificationState;
    private String justificationStateName;
    private String justificationType;
    private String justificationTypeName;

    public Absence(int id, String type, String typeName, String mode, String modeName, String subject, String subjectName, String delayMinutes, String teacher, String startTime, String creationTime, int lessonNumber, String justificationState, String justificationStateName, String justificationType, String justificationTypeName) {
        this.id = id;
        this.type = type;
        this.typeName = typeName;
        this.mode = mode;
        this.modeName = modeName;
        this.subject = subject;
        this.subjectName = subjectName;
        this.delayMinutes = delayMinutes;
        this.teacher = teacher;
        this.startTime = startTime;
        this.creationTime = creationTime;
        this.lessonNumber = lessonNumber;
        this.justificationState = justificationState;
        this.justificationStateName = justificationStateName;
        this.justificationType = justificationType;
        this.justificationTypeName = justificationTypeName;
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

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
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

    public String getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(String delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getJustificationState() {
        return justificationState;
    }

    public void setJustificationState(String justificationState) {
        this.justificationState = justificationState;
    }

    public String getJustificationStateName() {
        return justificationStateName;
    }

    public void setJustificationStateName(String justificationStateName) {
        this.justificationStateName = justificationStateName;
    }

    public String getJustificationType() {
        return justificationType;
    }

    public void setJustificationType(String justificationType) {
        this.justificationType = justificationType;
    }

    public String getJustificationTypeName() {
        return justificationTypeName;
    }

    public void setJustificationTypeName(String justificationTypeName) {
        this.justificationTypeName = justificationTypeName;
    }
}
