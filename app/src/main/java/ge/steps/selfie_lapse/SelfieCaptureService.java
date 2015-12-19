package ge.steps.selfie_lapse;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.os.Handler;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelfieCaptureService extends Service {

    private static final String TAG = SelfieCaptureService.class.getSimpleName();

    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView;
    private View removeImg;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private CameraController cameraController;
    private TextSwitcher countDownTextView;

    private boolean isCanceled = false;

    private void onCancelCapture() {
        isCanceled = true;
        stopSelf();
    }

    private void onStartCapture(SurfaceView preview) {
        try {
            if (cameraController.hasCamera()) {
                preview.getHolder().addCallback(
                        new SurfaceHolder.Callback() {
                            public void surfaceCreated(SurfaceHolder holder) {
                                // no-op -- wait until surfaceChanged()
                            }

                            public void surfaceChanged(SurfaceHolder holder,
                                                       int format, int width,
                                                       int height) {
                                Log.d(TAG, "holder initiated " + holder);
                                cameraController.getCameraInstance(holder, width, height);
                                new CountDownTimer(5000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        countDownTextView.setText("" + (millisUntilFinished / 1000));
                                    }

                                    @Override
                                    public void onFinish() {
                                        cameraController.takePicture(new Camera.PictureCallback() {
                                            @Override
                                            public void onPictureTaken(byte[] data, Camera camera) {
                                                // TODO save image here. and call database with saved URI
                                                stopSelf();
                                            }
                                        });
                                    }
                                }.start();

                            }

                            public void surfaceDestroyed(SurfaceHolder holder) {
                                // no-op
                            }
                        });
            } else {
                onCancelCapture();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cameraController = new CameraController(getApplicationContext());
        handleStart();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleStart() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        removeView = (RelativeLayout) inflater.inflate(R.layout.remove_view, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
        removeImg = removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);


        chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
        countDownTextView = (TextSwitcher) chatheadView.findViewById(R.id.countdown);
        countDownTextView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(getBaseContext());
            }
        });
        countDownTextView.setInAnimation(this, android.R.anim.fade_in);
        countDownTextView.setOutAnimation(this, android.R.anim.fade_out);

//        TextSwitcher textSwitcher = new TextSwitcher(getBaseContext());
//        textSwitcher.addView(countDownTextView);

        SurfaceView preview = (SurfaceView) chatheadView.findViewById(R.id.camera_preview);
        onStartCapture(preview);

        windowManager.getDefaultDisplay().getSize(szWindow);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(chatheadView, params);

        chatheadView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    Log.d(TAG, "Into runnable_longClick");
                    isLongclick = true;
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 600);

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {
                            int x_bound_left = (szWindow.x - removeImg.getWidth()) / 2 - 250;
                            int x_bound_right = (szWindow.x + removeImg.getWidth()) / 2 + 100;

                            int y_bound_top = szWindow.y - (removeView.getHeight() + getStatusBarHeight()) - 200;

                            if ((x_cord_Destination >= x_bound_left && x_cord_Destination <= x_bound_right) && y_cord_Destination >= y_bound_top) {
                                inBounded = true;

                                layoutParams.x = (szWindow.x - chatheadView.getWidth()) / 2;
                                layoutParams.y = szWindow.y - (removeView.getHeight() + getStatusBarHeight()) + 70;

                                if (removeImg.getLayoutParams().height == remove_img_height) {
                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                    int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));
                                    param_remove.y = y_cord_remove;
                                    windowManager.updateViewLayout(removeView, param_remove);
                                }


                                windowManager.updateViewLayout(chatheadView, layoutParams);
                                break;
                            } else {
                                inBounded = false;
                                removeImg.getLayoutParams().height = remove_img_height;
                                removeImg.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeView, param_remove);
                            }

                        }
                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;
                        windowManager.updateViewLayout(chatheadView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongclick = false;
                        removeView.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {
                            onCancelCapture();
                            inBounded = false;
                            break;
                        }

                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (x_diff < 5 && y_diff < 5) {
                            time_end = System.currentTimeMillis();
                        }

                        x_cord_Destination = x_init_margin + x_diff;
                        y_cord_Destination = y_init_margin + y_diff;

                        int x_start;
                        x_start = x_cord_Destination;


                        int BarHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight);
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(x_start);
                        break;
                    default:
                        Log.d(TAG, "chatheadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        windowManager.getDefaultDisplay().getSize(szWindow);

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "ChatHeadService.onConfigurationChanged -> landscap");

            if (layoutParams.y + (chatheadView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                layoutParams.y = szWindow.y - (chatheadView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }

            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "ChatHeadService.onConfigurationChanged -> portrait");

            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x);
            }

        }

    }

    private void resetPosition(int x_cord_now) {
        int w = chatheadView.getWidth();

        if (x_cord_now == 0 || x_cord_now == szWindow.x - w) {

        } else if (x_cord_now + w / 2 <= szWindow.x / 2) {
            moveToLeft(x_cord_now);

        } else if (x_cord_now + w / 2 > szWindow.x / 2) {
            moveToRight(x_cord_now);
        }
    }

    private void moveToLeft(int x_cord_now) {

        final int x = x_cord_now;
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = (int) (double) bounceValue(step, x);
                try {
                    windowManager.updateViewLayout(chatheadView, mParams);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "view not found");
                }
            }

            public void onFinish() {
                mParams.x = 0;
                try {
                    windowManager.updateViewLayout(chatheadView, mParams);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "view not found");
                }
            }
        }.start();
    }

    private void moveToRight(int x_cord_now) {
        final int x = x_cord_now;
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, szWindow.x - x - 2 * chatheadView.getWidth()) - chatheadView.getWidth();
                try {
                    windowManager.updateViewLayout(chatheadView, mParams);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "view not found");
                }
            }

            public void onFinish() {
                mParams.x = szWindow.x - chatheadView.getWidth();
                try {
                    windowManager.updateViewLayout(chatheadView, mParams);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "view not found");
                }
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        return scale * java.lang.Math.exp(-0.055 * step) * java.lang.Math.cos(0.08 * step);
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }


    private void chathead_longclick() {
        Log.d(TAG, "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraController != null)
            cameraController.releaseCamera();
        if (chatheadView != null) {
            windowManager.removeView(chatheadView);
        }
        if (removeView != null) {
            windowManager.removeView(removeView);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
