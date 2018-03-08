package io.github.boapps.eSzivacs.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import io.github.boapps.eSzivacs.Datas.Absence;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.Datas.Event;
import io.github.boapps.eSzivacs.Datas.Lesson;
import io.github.boapps.eSzivacs.Datas.Note;
import io.github.boapps.eSzivacs.Datas.School;
import io.github.boapps.eSzivacs.Datas.Student;
import io.github.boapps.eSzivacs.Datas.Subject;
import io.github.boapps.eSzivacs.Datas.Teacher;
import io.github.boapps.eSzivacs.Datas.Tutelary;


/**
 * Created by boa on 01/09/17.
 */

public class DataLoader {
    private static ArrayList<Evaluation> evaluationsc = new ArrayList<>();
    private static ArrayList<Evaluation> evaluationsfelevi = new ArrayList<>();
    private static ArrayList<Evaluation> evaluationsevvegi = new ArrayList<>();
    private static ArrayList<Evaluation> newEvaluations = new ArrayList<>();
    private static ArrayList<Evaluation> oldEvaluations = new ArrayList<>();
    private static ArrayList<Event> events = new ArrayList<>();
    private static ArrayList<Note> notes = new ArrayList<>();
    private static ArrayList<Absence> absences = new ArrayList<>();
    private static ArrayList<Subject> subjects = new ArrayList<>();
    private static Student studentInfo;
    private String username = "";
    private String password = "";
    private String instCode = "";
    private String url = "";
    private Context context;
    private SharedPreferences offlineData;

    public DataLoader(Context context) {
        offlineData = context.getSharedPreferences("offlineData", Activity.MODE_PRIVATE);
        this.context = context;
    }

    private static String readStream(InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setLogin(String username, String password, String url, String instCode) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.instCode = instCode;
    }

    public void doLogin() throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getStudent(getBearerCode());

    }

    public boolean isValid() throws IOException {
        URL reqURL = new URL(url + "/idp/api/v1/Token");

        HttpsURLConnection request = (HttpsURLConnection) reqURL.openConnection();
        String post = "institute_code=" + instCode + "&userName=" + username + "&password=" + password + "&grant_type=password&client_id=919e0c1c-76a2-4646-a2fb-7085bbbf3c56";

        request.setDoOutput(true);
        request.addRequestProperty("Accept", "application/json");
        request.addRequestProperty("HOST", url.replace("https://", ""));
        request.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestMethod("POST");

        request.connect();
        OutputStreamWriter writer = new OutputStreamWriter(request.getOutputStream());
        writer.write(post);
        writer.flush();

        String result = readStream(request.getInputStream());
        request.disconnect();

//        if (request.getResponseCode()=="100") {
//
//        }
        return true;
    }

    public ArrayList<School> getSchools() throws IOException {

        URL treqURL = new URL("https://" + "kretaglobalmobileapi.ekreta.hu" + "/api/v1/Institute");
        HttpsURLConnection trequest = (HttpsURLConnection) (treqURL.openConnection());
        trequest.addRequestProperty("Accept", "application/json");
        trequest.addRequestProperty("apiKey", "7856d350-1fda-45f5-822d-e1a2f3f1acf0");
        trequest.addRequestProperty("HOST", "kretaglobalmobileapi.ekreta.hu");
//        trequest.addRequestProperty("Accept-Encoding", "gzip");
        //todo implement compression for mobile data
        trequest.addRequestProperty("Connection", "keep-alive");
        trequest.setRequestMethod("GET");
        trequest.connect();

        String tresult = readStream(trequest.getInputStream());

        trequest.disconnect();
        ArrayList<School> suliArray = new ArrayList<>();
        Collections.sort(suliArray, new Comparator<School>() {
            @Override
            public int compare(School o1, School o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        try {
            JSONArray jArray = new JSONArray(tresult);
//            tresult = jObject.getJSONArray("Evaluations").getJSONObject(0).getString("EvaluationId");
            for (int n = 0; n < jArray.length(); n++) {
                int id = jArray.getJSONObject(n).getInt("InstituteId");
                String name = jArray.getJSONObject(n).getString("Name");
                String code = jArray.getJSONObject(n).getString("InstituteCode");
                String url = jArray.getJSONObject(n).getString("Url");
                String city = jArray.getJSONObject(n).getString("City");

                School school = new School(id, name, code, url, city);
                suliArray.add(school);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return suliArray;
    }

    public ArrayList<Lesson> getTimetableOffline(String from, String to, String BearerCode) throws IOException {
        String tresult = offlineData.getString("timetable", " ");

        ArrayList<Lesson> lessons = new ArrayList<>();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            JSONArray jArray = new JSONArray(tresult);
            for (int n = 0; n < jArray.length(); n++) {
                int id = jArray.getJSONObject(n).getInt("LessonId");
                int count = jArray.getJSONObject(n).getInt("Count");
                Date date = simpleDateFormat.parse(jArray.getJSONObject(n).getString("Date"));
                Date start = simpleDateFormat.parse(jArray.getJSONObject(n).getString("StartTime"));
                Date end = simpleDateFormat.parse(jArray.getJSONObject(n).getString("EndTime"));
                String subject = jArray.getJSONObject(n).getString("Subject");
                String subjectName = jArray.getJSONObject(n).getString("SubjectCategoryName");
                String room = jArray.getJSONObject(n).getString("ClassRoom");
                String group = jArray.getJSONObject(n).getString("ClassGroup");
                String teacher = jArray.getJSONObject(n).getString("Teacher");
                String depTeacher = jArray.getJSONObject(n).getString("DeputyTeacher");
                String state = jArray.getJSONObject(n).getString("State");
                String stateName = jArray.getJSONObject(n).getString("StateName");
                String presence = jArray.getJSONObject(n).getString("PresenceType");
                String presenceName = jArray.getJSONObject(n).getString("PresenceTypeName");
                String theme = jArray.getJSONObject(n).getString("Theme");
                String homework = jArray.getJSONObject(n).getString("Homework");

                Lesson lesson = new Lesson(id, count, date, start, end, subject, subjectName, room, group, teacher, depTeacher, state, stateName, presence, presenceName, theme, homework);
                lessons.add(lesson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(lessons, new Comparator<Lesson>() {
            @Override
            public int compare(Lesson o1, Lesson o2) {
                return String.valueOf(o1.getCount()).toLowerCase().compareTo(String.valueOf(o2.getCount()).toLowerCase());
            }
        });

        for (Lesson l : lessons)
            System.out.println(l.getSubjectName());
        return lessons;

    }

    public ArrayList<Lesson> getTimetable(String from, String to, String BearerCode) throws IOException {
        String tresult = "";

        if (isOnline()) {

            URL treqURL = new URL(url + "/mapi/api/v1/Lesson?fromDate=" + from + "&toDate=" + to);
            HttpsURLConnection trequest = (HttpsURLConnection) (treqURL.openConnection());
            trequest.addRequestProperty("Accept", "application/json");
            trequest.addRequestProperty("Authorization", "Bearer " + BearerCode);
            trequest.addRequestProperty("HOST", url.replace("https://", ""));
            trequest.addRequestProperty("Connection", "keep-alive");
            trequest.setRequestMethod("GET");
            trequest.connect();

            tresult = readStream(trequest.getInputStream());
            trequest.disconnect();

            offlineData.edit().putString("timetable", tresult).apply();
        } else {
            tresult = offlineData.getString("timetable", " ");
        }
        ArrayList<Lesson> lessons = new ArrayList<>();
        Collections.sort(lessons, new Comparator<Lesson>() {
            @Override
            public int compare(Lesson o1, Lesson o2) {
                return String.valueOf(o1.getCount()).toLowerCase().compareTo(String.valueOf(o2.getCount()).toLowerCase());
            }
        });


        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            JSONArray jArray = new JSONArray(tresult);
            for (int n = 0; n < jArray.length(); n++) {
                int id = jArray.getJSONObject(n).getInt("LessonId");
                int count = jArray.getJSONObject(n).getInt("Count");
                Date date = simpleDateFormat.parse(jArray.getJSONObject(n).getString("Date"));
                Date start = simpleDateFormat.parse(jArray.getJSONObject(n).getString("StartTime"));
                Date end = simpleDateFormat.parse(jArray.getJSONObject(n).getString("EndTime"));
                String subject = jArray.getJSONObject(n).getString("Subject");
                String subjectName = jArray.getJSONObject(n).getString("SubjectCategoryName");
                String room = jArray.getJSONObject(n).getString("ClassRoom");
                String group = jArray.getJSONObject(n).getString("ClassGroup");
                String teacher = jArray.getJSONObject(n).getString("Teacher");
                String depTeacher = jArray.getJSONObject(n).getString("DeputyTeacher");
                String state = jArray.getJSONObject(n).getString("State");
                String stateName = jArray.getJSONObject(n).getString("StateName");
                String presence = jArray.getJSONObject(n).getString("PresenceType");
                String presenceName = jArray.getJSONObject(n).getString("PresenceTypeName");
                String theme = jArray.getJSONObject(n).getString("Theme");
                String homework = jArray.getJSONObject(n).getString("Homework");

                Lesson lesson = new Lesson(id, count, date, start, end, subject, subjectName, room, group, teacher, depTeacher, state, stateName, presence, presenceName, theme, homework);
                lessons.add(lesson);
                System.out.println(lesson.getSubjectName() + lesson.getId() + lesson.getState() + lesson.getStart() + lesson.getCount());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return lessons;

    }


    public String getBearerCode() throws IOException {
        if (isOnline()) {

            String token = "";
            URL reqURL = new URL(url + "/idp/api/v1/Token");

            HttpsURLConnection request = (HttpsURLConnection) (reqURL.openConnection());
            String post = "institute_code=" + instCode + "&userName=" + username + "&password=" + password + "&grant_type=password&client_id=919e0c1c-76a2-4646-a2fb-7085bbbf3c56";

            request.setDoOutput(true);
            request.addRequestProperty("Accept", "application/json");
            request.addRequestProperty("HOST", url.replace("https://", ""));
            request.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestMethod("POST");
            request.connect();
            OutputStreamWriter writer = new OutputStreamWriter(request.getOutputStream());
            writer.write(post);
            writer.flush();

            String result = readStream(request.getInputStream());
            request.disconnect();

            try {
                JSONObject jObject = new JSONObject(result);
                token = jObject.getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return token;
        }
        return "";
    }

    public ArrayList<Evaluation> getEvaluations() {
        System.out.println("evaluationsc.size" + evaluationsc.size());
        return evaluationsc;
    }

    public ArrayList<Evaluation> getEvaluationsFelevi() {
        return evaluationsfelevi;
    }

    public ArrayList<Evaluation> getEvaluationsEvvegi() {
        return evaluationsevvegi;
    }

    public ArrayList<Evaluation> getNewEvaluations() {
        return newEvaluations;
    }

    public void getStudentOffline() throws IOException {
        String tresult = offlineData.getString("student", " ");
        try {
            JSONObject jObject = new JSONObject(tresult);
            evaluationsc.clear();
            notes.clear();
            absences.clear();
            evaluationsfelevi.clear();
            evaluationsevvegi.clear();
            subjects.clear();

            for (int n = 0; n < jObject.getJSONArray("Evaluations").length(); n++) {
                int id = jObject.getJSONArray("Evaluations").getJSONObject(n).getInt("EvaluationId");
                String form = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Form");
                String range = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("FormName");
                String type = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Type");
                String subject = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Subject");
                String subjectCategory = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("SubjectCategoryName");
                String mode = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Mode");
                String weight = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Weight");
                String value = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Value");
                String numericValue = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("NumberValue");
                String teacher = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Teacher");
                String date = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Date");
                String creationDate = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("CreatingTime");
                String theme = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Theme");

                if (type.equals("MidYear")) {
                    Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                    evaluationsc.add(evaluation);
                } else if (type.equals("HalfYear")) {
                    Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                    evaluationsfelevi.add(evaluation);
                } else if (type.equals("EndYear")) { //todo not sure if the String is "EndYear", just guessing
                    Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                    evaluationsevvegi.add(evaluation);
                }
            }

            for (int n = 0; n < jObject.getJSONArray("Notes").length(); n++) {
                int id = jObject.getJSONArray("Notes").getJSONObject(n).getInt("NoteId");
                String type = jObject.getJSONArray("Notes").getJSONObject(n).getString("Type");
                String title = jObject.getJSONArray("Notes").getJSONObject(n).getString("Title");
                String content = jObject.getJSONArray("Notes").getJSONObject(n).getString("Content");
                String teacher = jObject.getJSONArray("Notes").getJSONObject(n).getString("Teacher");
                String date = jObject.getJSONArray("Notes").getJSONObject(n).getString("Date");
                String creationTime = jObject.getJSONArray("Notes").getJSONObject(n).getString("CreatingTime");

                Note note = new Note(id, type, title, content, teacher, date, creationTime);

                notes.add(note);

            }

            for (int n = 0; n < jObject.getJSONArray("Absences").length(); n++) {
                int id = jObject.getJSONArray("Absences").getJSONObject(n).getInt("AbsenceId");
                String type = jObject.getJSONArray("Absences").getJSONObject(n).getString("Type");
                String typeName = jObject.getJSONArray("Absences").getJSONObject(n).getString("TypeName");
                String mode = jObject.getJSONArray("Absences").getJSONObject(n).getString("Mode");
                String modeName = jObject.getJSONArray("Absences").getJSONObject(n).getString("ModeName");
                String subject = jObject.getJSONArray("Absences").getJSONObject(n).getString("Subject");
                String subjectName = jObject.getJSONArray("Absences").getJSONObject(n).getString("SubjectCategoryName");
                String delayMinutes = jObject.getJSONArray("Absences").getJSONObject(n).getString("DelayTimeMinutes");
                String teacher = jObject.getJSONArray("Absences").getJSONObject(n).getString("Teacher");
                String startTime = jObject.getJSONArray("Absences").getJSONObject(n).getString("LessonStartTime");
                int lessonNumber = jObject.getJSONArray("Absences").getJSONObject(n).getInt("NumberOfLessons");
                String creationTime = jObject.getJSONArray("Absences").getJSONObject(n).getString("CreatingTime");
                String justificationState = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationState");
                String justificationStateName = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationStateName");
                String justificationType = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationType");
                String justificationTypeName = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationTypeName");

                Absence absence = new Absence(id, type, typeName, mode, modeName, subject, subjectName, delayMinutes, teacher, startTime, creationTime, lessonNumber, justificationState, justificationStateName, justificationType, justificationTypeName);
                absences.add(absence);
            }

            for (int n = 0; n < jObject.getJSONArray("SubjectAverages").length(); n++) {
                String subject = jObject.getJSONArray("SubjectAverages").getJSONObject(n).getString("Subject");
                String subjectName = jObject.getJSONArray("SubjectAverages").getJSONObject(n).getString("SubjectCategoryName");
                double value = (double) jObject.getJSONArray("SubjectAverages").getJSONObject(n).get("Value");
                double classValue = (double) jObject.getJSONArray("SubjectAverages").getJSONObject(n).get("ClassValue");
                double diff = (double) jObject.getJSONArray("SubjectAverages").getJSONObject(n).get("Difference");

                Subject subjectu = new Subject(subject, subjectName, value, classValue, diff);
                subjects.add(subjectu);
            }

            Tutelary tutelary = new Tutelary(jObject.getJSONObject("Tutelary").getInt("TutelaryId"), jObject.getJSONObject("Tutelary").getString("Name"));
            Teacher teacher = new Teacher(jObject.getJSONObject("FormTeacher").getInt("TeacherId"), jObject.getJSONObject("FormTeacher").getString("Name"), jObject.getJSONObject("FormTeacher").getString("Email"), jObject.getJSONObject("FormTeacher").getString("PhoneNumber"));
            studentInfo = new Student(jObject.getInt("StudentId"), jObject.getString("Name"), jObject.getString("InstituteName"), jObject.getString("InstituteCode"), evaluationsc, teacher, tutelary);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getStudent(String BearerCode) throws IOException {
        String tresult = "";

        newEvaluations.clear();
        evaluationsc.clear();
        evaluationsfelevi.clear();
        evaluationsevvegi.clear();
        notes.clear();
        absences.clear();
        subjects.clear();


        if (isOnline()) {
            oldEvaluations.clear();

            //gets the old evaluations for the notification

            try {
                JSONObject jObject = new JSONObject(offlineData.getString("student", " "));
                for (int n = 0; n < jObject.getJSONArray("Evaluations").length(); n++) {
                    int id = jObject.getJSONArray("Evaluations").getJSONObject(n).getInt("EvaluationId");
                    String form = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Form");
                    String range = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("FormName");
                    String type = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Type");
                    String subject = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Subject");
                    String subjectCategory = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("SubjectCategoryName");
                    String mode = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Mode");
                    String weight = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Weight");
                    String value = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Value");
                    String numericValue = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("NumberValue");
                    String teacher = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Teacher");
                    String date = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Date");
                    String creationDate = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("CreatingTime");
                    String theme = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Theme");

                    if (type.equals("MidYear")) {
                        Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                        oldEvaluations.add(evaluation);
                    } else if (type.equals("HalfYear")) {
                        Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                        evaluationsfelevi.add(evaluation);
                    } else if (type.equals("EndYear")) { //todo not sure if the String is "EndYear", just guessing
                        Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                        evaluationsevvegi.add(evaluation);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("oldEvaluations");

            for (Evaluation evaluation : oldEvaluations)
                System.out.println(evaluation.getSubject() + evaluation.getNumericValue());

            URL treqURL = new URL(url + "/mapi/api/v1/Student");
            HttpsURLConnection trequest = (HttpsURLConnection) (treqURL.openConnection());
            trequest.addRequestProperty("Accept", "application/json");
            trequest.addRequestProperty("Authorization", "Bearer " + BearerCode);
            trequest.addRequestProperty("HOST", url.replace("https://", ""));
            trequest.addRequestProperty("Connection", "keep-alive");
            trequest.setRequestMethod("GET");
            trequest.connect();

            tresult = readStream(trequest.getInputStream());
            trequest.disconnect();


            if (tresult.length() > 5)
                offlineData.edit().putString("student", tresult).apply();

        } else {
            tresult = offlineData.getString("student", " ");
        }

        try {
            JSONObject jObject = new JSONObject(tresult);
            for (int n = 0; n < jObject.getJSONArray("Evaluations").length(); n++) {
                int id = jObject.getJSONArray("Evaluations").getJSONObject(n).getInt("EvaluationId");
                String form = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Form");
                String range = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("FormName");
                String type = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Type");
                String subject = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Subject");
                String subjectCategory = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("SubjectCategoryName");
                String mode = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Mode");
                String weight = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Weight");
                String value = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Value");
                String numericValue = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("NumberValue");
                String teacher = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Teacher");
                String date = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Date");
                String creationDate = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("CreatingTime");
                String theme = jObject.getJSONArray("Evaluations").getJSONObject(n).getString("Theme");

                if (type.equals("MidYear")) {
                    Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                    evaluationsc.add(evaluation);
                } else if (type.equals("HalfYear")) {
                    Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                    evaluationsfelevi.add(evaluation);
                } else if (type.equals("EndYear")) { //todo not sure if the String is "EndYear", just guessing
                    Evaluation evaluation = new Evaluation(id, form, range, type, subject, subjectCategory, mode, weight, value, numericValue, teacher, date, creationDate, theme);
                    evaluationsevvegi.add(evaluation);
                }

            }
            System.out.println("evaluations");

//            for (Evaluation evaluation: evaluations)
//                System.out.println(evaluation.getSubject() + evaluation.getNumericValue());

            System.out.println("evaluations.size()");
            System.out.println(evaluationsc.size());
            System.out.println("oldEvaluations.size();");
            System.out.println(oldEvaluations.size());

            if (isOnline()) {
                for (int n = 0; n < evaluationsc.size() - oldEvaluations.size(); n++)
                    newEvaluations.add(evaluationsc.get(n));
                System.out.println("newEvaluations");
            }


//            for (Evaluation evaluation: newEvaluations)
//                System.out.println(evaluation.getSubject() + evaluation.getNumericValue());


            for (int n = 0; n < jObject.getJSONArray("Notes").length(); n++) {
                int id = jObject.getJSONArray("Notes").getJSONObject(n).getInt("NoteId");
                String type = jObject.getJSONArray("Notes").getJSONObject(n).getString("Type");
                String title = jObject.getJSONArray("Notes").getJSONObject(n).getString("Title");
                String content = jObject.getJSONArray("Notes").getJSONObject(n).getString("Content");
                String teacher = jObject.getJSONArray("Notes").getJSONObject(n).getString("Teacher");
                String date = jObject.getJSONArray("Notes").getJSONObject(n).getString("Date");
                String creationTime = jObject.getJSONArray("Notes").getJSONObject(n).getString("CreatingTime");

                Note note = new Note(id, type, title, content, teacher, date, creationTime);
                notes.add(note);
            }

            for (int n = 0; n < jObject.getJSONArray("Absences").length(); n++) {
                int id = jObject.getJSONArray("Absences").getJSONObject(n).getInt("AbsenceId");
                String type = jObject.getJSONArray("Absences").getJSONObject(n).getString("Type");
                String typeName = jObject.getJSONArray("Absences").getJSONObject(n).getString("TypeName");
                String mode = jObject.getJSONArray("Absences").getJSONObject(n).getString("Mode");
                String modeName = jObject.getJSONArray("Absences").getJSONObject(n).getString("ModeName");
                String subject = jObject.getJSONArray("Absences").getJSONObject(n).getString("Subject");
                String subjectName = jObject.getJSONArray("Absences").getJSONObject(n).getString("SubjectCategoryName");
                String delayMinutes = jObject.getJSONArray("Absences").getJSONObject(n).getString("DelayTimeMinutes");
                String teacher = jObject.getJSONArray("Absences").getJSONObject(n).getString("Teacher");
                String startTime = jObject.getJSONArray("Absences").getJSONObject(n).getString("LessonStartTime");
                int lessonNumber = jObject.getJSONArray("Absences").getJSONObject(n).getInt("NumberOfLessons");
                String creationTime = jObject.getJSONArray("Absences").getJSONObject(n).getString("CreatingTime");
                String justificationState = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationState");
                String justificationStateName = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationStateName");
                String justificationType = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationType");
                String justificationTypeName = jObject.getJSONArray("Absences").getJSONObject(n).getString("JustificationTypeName");

                System.out.println("justificationState");
                System.out.println(justificationState);

                startTime = startTime.replace("T00:00:00", "");

                Absence absence = new Absence(id, type, typeName, mode, modeName, subject, subjectName, delayMinutes, teacher, startTime, creationTime, lessonNumber, justificationState, justificationStateName, justificationType, justificationTypeName);
                absences.add(absence);
            }

            for (int n = 0; n < jObject.getJSONArray("SubjectAverages").length(); n++) {
                String subject = jObject.getJSONArray("SubjectAverages").getJSONObject(n).getString("Subject");
                String subjectName = jObject.getJSONArray("SubjectAverages").getJSONObject(n).getString("SubjectCategoryName");
                double value = (double) jObject.getJSONArray("SubjectAverages").getJSONObject(n).get("Value");
                double classValue = (double) jObject.getJSONArray("SubjectAverages").getJSONObject(n).get("ClassValue");
                double diff = (double) jObject.getJSONArray("SubjectAverages").getJSONObject(n).get("Difference");

                Subject subjectu = new Subject(subject, subjectName, value, classValue, diff);
                subjects.add(subjectu);
            }

            Tutelary tutelary = new Tutelary(jObject.getJSONObject("Tutelary").getInt("TutelaryId"), jObject.getJSONObject("Tutelary").getString("Name"));
            Teacher teacher = new Teacher(jObject.getJSONObject("FormTeacher").getInt("TeacherId"), jObject.getJSONObject("FormTeacher").getString("Name"), jObject.getJSONObject("FormTeacher").getString("Email"), jObject.getJSONObject("FormTeacher").getString("PhoneNumber"));
            studentInfo = new Student(jObject.getInt("StudentId"), jObject.getString("Name"), jObject.getString("InstituteName"), jObject.getString("InstituteCode"), evaluationsc, teacher, tutelary);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Student getStudentInfo() {
        return studentInfo;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public ArrayList<Absence> getAbsences() {
        return absences;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public ArrayList<Event> getEvents(String BearerCode) throws IOException {
        String tresult = "";
        events.clear();
        if (isOnline()) {

            URL treqURL = new URL(url + "/mapi/api/v1/Event");
            HttpsURLConnection trequest = (HttpsURLConnection) (treqURL.openConnection());
            trequest.addRequestProperty("Accept", "application/json");
            trequest.addRequestProperty("Authorization", "Bearer " + BearerCode);
            trequest.addRequestProperty("HOST", url.replace("https://", ""));
            trequest.addRequestProperty("Connection", "keep-alive");
            trequest.setRequestMethod("GET");
            trequest.connect();

            tresult = readStream(trequest.getInputStream());
            trequest.disconnect();

            offlineData.edit().putString("event", tresult).apply();
        } else {
            tresult = offlineData.getString("event", " ");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            JSONArray jsonArray = new JSONArray(tresult);
            for (int n = 0; n < jsonArray.length(); n++) {
                int id = jsonArray.getJSONObject(n).getInt("EventId");
                Date date = simpleDateFormat.parse(jsonArray.getJSONObject(n).getString("Date"));
                String content = jsonArray.getJSONObject(n).getString("Content");

                Event event = new Event(id, date, content);
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public ArrayList<Event> getEventsOffline() throws IOException {
        String tresult = "";
        events.clear();
        tresult = offlineData.getString("event", " ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            JSONArray jsonArray = new JSONArray(tresult);
            for (int n = 0; n < jsonArray.length(); n++) {
                int id = jsonArray.getJSONObject(n).getInt("EventId");
                Date date = simpleDateFormat.parse(jsonArray.getJSONObject(n).getString("Date"));
                String content = jsonArray.getJSONObject(n).getString("Content");

                Event event = new Event(id, date, content);
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
