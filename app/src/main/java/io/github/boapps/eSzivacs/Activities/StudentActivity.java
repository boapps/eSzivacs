package io.github.boapps.eSzivacs.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.github.boapps.eSzivacs.OpenGL.StudentGLSurfaceView;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.subjects;
import static io.github.boapps.eSzivacs.Activities.MainPage.user;
import static io.github.boapps.eSzivacs.OpenGL.StudentGLRenderer.mAngle;
import static io.github.boapps.eSzivacs.OpenGL.StudentGLRenderer.selectedLesson;

public class StudentActivity extends AppCompatActivity {
    public static StudentGLSurfaceView mGLViewst;
    private FrameLayout chart;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.selectCurrentTheme(this);

        if (subjects.size() > 2)
            mGLViewst = new StudentGLSurfaceView(this);

        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView name_tv = findViewById(R.id.name_tv);
        TextView inst_tv = findViewById(R.id.inst_tv);
        TextView teacher_tv = findViewById(R.id.teacher_tv);
        if (user != null) {
            name_tv.setText(getString(R.string.userinfo_name, user.getName()));
            inst_tv.setText(getString(R.string.userinfo_institution, user.getInstName()));
            teacher_tv.setText(getString(R.string.userinfo_teacher, user.getTeacher().getName()));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (subjects.size() > 2) {

            chart = findViewById(R.id.chart);

            chart.addView(mGLViewst);

            tv = new TextView(getApplicationContext());
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(14);
            tv.setX(chart.getWidth() / 2);
            tv.setY(chart.getHeight() / 2);
            chart.addView(tv);

            final int oldal = subjects.size();
            final float angle = (float) 360 / oldal;

            mGLViewst.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int n = (Math.round((mAngle + 180 / (oldal) - angle / 2) / 360 * (oldal)) + 1);
                    tv.setText(subjects.get((n + oldal - 1) % oldal).getValue() + " - " + subjects.get((n + oldal - 1) % oldal).getSubjectName());
                    selectedLesson = (n + oldal - 1) % oldal;
                    System.out.println(selectedLesson);
                    return false;
                }
            });
            mGLViewst.post(new Runnable() {

                @Override
                public void run() {

            /*    for (int n = 0; n < oldal; n++) {
//                            0, 0, 0,
//                            (float) Math.cos(angle * n + Math.toRadians(270)), , 0,
//                            (float) Math.cos(angle * (n+1) + Math.toRadians(270)), (float) Math.sin(angle * (n+1) + Math.toRadians(270)), 0
                    FrameLayout frameLayout = new FrameLayout(getApplicationContext());
//                    frameLayout.setBackgroundColor(Color.WHITE);
                    frameLayout.setPivotX(0);
                    frameLayout.setPivotY(0);
                    frameLayout.setX(mGLViewst.getWidth()/2 * -0.8f * (float) subjects.get(n).getValue() * (float) Math.cos(angle * n + Math.toRadians(270)) / 5 + mGLViewst.getWidth()/2 - 90);
                    frameLayout.setY(mGLViewst.getHeight()/2 * -0.8f * (float) subjects.get(n).getValue() * (float) Math.sin(angle * n + Math.toRadians(270)) / 5 + mGLViewst.getHeight()/2);
                    frameLayout.addView(tv);
                    chart.addView(frameLayout);
//                    frameLayout.getLayoutParams().width=100;

                }*/
                }

            });
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLViewst != null)
            mGLViewst.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGLViewst != null)
            mGLViewst.onResume();
    }

}
