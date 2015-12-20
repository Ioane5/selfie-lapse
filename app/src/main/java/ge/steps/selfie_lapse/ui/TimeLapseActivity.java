package ge.steps.selfie_lapse.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import ge.steps.selfie_lapse.R;
import ge.steps.selfie_lapse.Selfie;

public class TimeLapseActivity extends AppCompatActivity {

    public static final String ARG_IMAGES = "ARG_IMG";

    private ArrayList<Selfie> selfies;
    private static final long duration = 1500;
    private ImageView imageView;
    private boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_lapse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selfies = (ArrayList<Selfie>) getIntent().getSerializableExtra(ARG_IMAGES);
        imageView = (ImageView) findViewById(R.id.image);
    }


    public void showLapse() {
        new CountDownTimer(selfies.size() * duration, duration) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused)
                    cancel();
                else {
                    int pos = (int) (selfies.size() - millisUntilFinished / duration) - 1;
                    Selfie selfie = selfies.get(pos);
                    selfie.getPath();
                    final File image = new File(selfie.getPath());
                    final Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), null);
                    imageView.animate().alpha(0).setDuration(200).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            imageView.animate().alpha(1f).setDuration(200);
                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                if (!isPaused)
                    showLapse();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        showLapse();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }
}
