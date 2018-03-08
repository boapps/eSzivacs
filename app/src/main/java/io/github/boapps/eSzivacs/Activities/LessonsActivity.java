package io.github.boapps.eSzivacs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.github.boapps.eSzivacs.Adapters.MainLVAdapter;
import io.github.boapps.eSzivacs.Datas.Evaluation;
import io.github.boapps.eSzivacs.OpenGL.MyGLSurfaceView;
import io.github.boapps.eSzivacs.R;
import io.github.boapps.eSzivacs.Utils.Themer;

import static io.github.boapps.eSzivacs.Activities.MainPage.evaluations;


public class LessonsActivity extends AppCompatActivity {
    public static MyGLSurfaceView mGLView;
    public static float jegyek[];
//    private ArrayList<Evaluation> lessonEvaluations =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themer.selectCurrentTheme(this);
        Intent intent = getIntent();
        System.out.println(intent.getStringExtra("subject"));
        String subject = intent.getStringExtra("subject");
        ArrayList<Evaluation> subEvals = new ArrayList<>();
        ArrayList<Float> average = new ArrayList<>();
//        ArrayList<Integer> evs = new ArrayList<>();


        for (int n = 0; n < evaluations.size(); n++) {
            if (evaluations.get(n).getSubject().equals(subject))
                subEvals.add(evaluations.get(n));
        }
        Collections.sort(subEvals, new Comparator<Evaluation>() {
            @Override
            public int compare(Evaluation o1, Evaluation o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });


        for (int n = 0; n < subEvals.size(); n++) {
            int evsInt = 0;
            int number = 0;
            for (int m = 0; m <= n; m++) {
                evsInt += Integer.valueOf(subEvals.get(m).getNumericValue());
                number++;
            }
            average.add((float) evsInt / (float) number);
            System.out.println(subEvals.get(n).getNumericValue());
            System.out.println((float) evsInt / number);
        }


        jegyek = new float[average.toArray().length];
        for (int i = 0; i < average.toArray().length; i++) {
            jegyek[i] = (float) average.toArray()[i];
        }

        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
//        mGLView.setRenderer(new ClearRenderer());
        setContentView(R.layout.activity_lessons);
        FrameLayout chat = findViewById(R.id.lessons);
        chat.addView(mGLView);


        ListView lessonEvalsLV = findViewById(R.id.lesson_evals_lv);

        System.out.println(subEvals.size());
        MainLVAdapter mainLVAdapter = new MainLVAdapter(subEvals, this);
        lessonEvalsLV.setAdapter(mainLVAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}
