package ge.steps.selfie_lapse.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.txusballesteros.SnakeView;
//import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;
//import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

import ge.steps.selfie_lapse.Emotion;
import ge.steps.selfie_lapse.FileStorage;
import ge.steps.selfie_lapse.R;
import ge.steps.selfie_lapse.Selfie;
import ge.steps.selfie_lapse.StorageAPI;

public class EmotionGraphActivity extends AppCompatActivity {

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_graph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Graphs");

        ArrayList<Selfie> selfies = getValidSelfes();

        SnakeView anger = (SnakeView) findViewById(R.id.snake_anger);
        SnakeView contempt = (SnakeView) findViewById(R.id.snake_contempt);
        SnakeView disgust = (SnakeView) findViewById(R.id.snake_disgust);
        SnakeView fear = (SnakeView) findViewById(R.id.snake_fear);
        SnakeView happiness = (SnakeView) findViewById(R.id.snake_happiness);
        SnakeView neutral = (SnakeView) findViewById(R.id.snake_neutral);
        SnakeView sadness = (SnakeView) findViewById(R.id.snake_sadness);
        SnakeView surprise = (SnakeView) findViewById(R.id.snake_surprise);
        for (Selfie s : selfies) {
            Emotion emotion = s.getEmotion();
            anger.addValue((float) emotion.getAnger());
            contempt.addValue((float) emotion.getContempt());
            disgust.addValue((float) emotion.getDisgust());
            fear.addValue((float) emotion.getFear());
            happiness.addValue((float) emotion.getHappiness());
            neutral.addValue((float) emotion.getNeutral());
            sadness.addValue((float) emotion.getSadness());
            surprise.addValue((float) emotion.getSurprise());
        }

    }

    private ArrayList<Selfie> getValidSelfes() {
        StorageAPI storage = FileStorage.getSelfieStore(getApplicationContext());
        ArrayList<Selfie> selfies = (ArrayList<Selfie>) storage.getAllSelfies();
        for (int i = 0; i < selfies.size(); i++) {
            if (selfies.get(i).getEmotion() == null) {
                selfies.remove(i);
                i--;
            }
        }
        return selfies;
    }

    private void setData() {
        ArrayList<Selfie> selfies = getValidSelfes();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < selfies.size(); i++)
            xVals.add((i) + "");


        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < selfies.size(); i++) {

            yVals.add(new Entry((float) selfies.get(i).getEmotion().getHappiness(), i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = getSet(yVals, "Happiness", Color.WHITE);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    private LineDataSet getSet(ArrayList<Entry> data, String name, int color) {
        LineDataSet set = new LineDataSet(data, name);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(1f);
        set.setCircleSize(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setFillAlpha(65);
        set.setFillColor(Color.BLACK);
        return set;
    }
}