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
//import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;
//import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

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

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(1f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        mChart.setBackgroundColor(getR);
        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData();

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
//         l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);

        // // dont forget to refresh the drawing
        mChart.invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    private void setData() {
        StorageAPI storage = FileStorage.getSelfieStore(getApplicationContext());
        ArrayList<Selfie> selfies = (ArrayList<Selfie>) storage.getAllSelfies();
        for (int i = 0; i < selfies.size(); i++) {
            if (selfies.get(i).getEmotion() == null) {
                selfies.remove(i);
                i--;
            }
        }
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