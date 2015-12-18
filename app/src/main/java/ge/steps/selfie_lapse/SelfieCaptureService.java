package ge.steps.selfie_lapse;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class SelfieCaptureService extends Service {

    private ImageView chatHead;
    private View removeView;
    private WindowManager.LayoutParams chParams, rvParams;
    private WindowManager windowManager;
    private Point displaySize;

    public SelfieCaptureService() {
    }

    private void saveChPoint(Point point) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        edit.putInt("ch.x", point.x);
        edit.putInt("ch.y", point.y);
        edit.commit();
    }

    private Point getChPoint() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Point point = new Point();
        point.x = prefs.getInt("ch.x", 0);
        point.y = prefs.getInt("ch.y", 100);
        return point;
    }

    private void initViews() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        displaySize = new Point();
        windowManager.getDefaultDisplay().getSize(displaySize);

        chatHead = new ImageView(this);
        chatHead.setImageResource(android.R.drawable.sym_def_app_icon);

        chParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        chParams.gravity = Gravity.TOP | Gravity.LEFT;
        Point point = getChPoint();
        chParams.x = point.x;
        chParams.y = point.y;

        removeView = LayoutInflater.from(this).inflate(R.layout.remove_view, null, false);
        windowManager.addView(chatHead, chParams);

        rvParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        rvParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        windowManager.addView(removeView, rvParams);
    }

    private void showRemoveView(boolean show) {
        if (show) {
            removeView.setAlpha(0);
            removeView.animate().alpha(1).setDuration(500);
            removeView.setVisibility(View.VISIBLE);
        } else {
            removeView.setAlpha(1);
            removeView.animate().alpha(0).setDuration(500);
            removeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initViews();

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            private Point currPoint = new Point();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = chParams.x;
                        initialY = chParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        showRemoveView(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        chParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        chParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        Point currPoint = new Point(chParams.x, chParams.y);
                        if (isInRemoveRange(chParams.x, chParams.y)) {
                            showRemoveView(true);
                        } else {
                            showRemoveView(false);
                        }
                        windowManager.updateViewLayout(chatHead, chParams);
                        return true;
                }
                return false;
            }
        });
    }

    private boolean isInRemoveRange(int x, int y) {
        return y > displaySize.y / 3;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
