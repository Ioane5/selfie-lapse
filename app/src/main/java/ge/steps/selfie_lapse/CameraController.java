package ge.steps.selfie_lapse;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ioane5 on 12/19/15.
 */
public class CameraController {

    private Context context;

    private boolean hasCamera;

    private Camera camera;
    private int cameraId;

    public CameraController(Context c) {
        context = c.getApplicationContext();

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            cameraId = getFrontCameraId();

            if (cameraId != -1) {
                hasCamera = true;
            } else {
                hasCamera = false;
            }
        } else {
            hasCamera = false;
        }
    }

    public boolean hasCamera() {
        return hasCamera;
    }

    public void getCameraInstance(SurfaceHolder surfaceView, int w, int h) {
        camera = null;
        if (hasCamera) {
            try {
                camera = Camera.open(cameraId);
                prepareCamera(surfaceView, w, h);
            } catch (Exception e) {
                hasCamera = false;
            }
        }
    }

    public interface GetURI {
        void onPhotoSaved(String uri);
    }

    private GetURI mUriListener;

    public void takePicture(GetURI callback) {
        mUriListener = callback;
        if (hasCamera && camera != null) {
            camera.getParameters().setRotation(0);
            camera.takePicture(null, null, mPicture);
        }
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private int getFrontCameraId() {
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo ci = new android.hardware.Camera.CameraInfo();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camId = i;
            }
        }
        return camId;
    }

    private void prepareCamera(SurfaceHolder holder, int w, int h) {
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes, w, h);

        camera.getParameters().setPreviewSize(optimalSize.width, optimalSize.height);
        camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        camera.startPreview();

        Camera.Parameters params = camera.getParameters();
        params.setJpegQuality(100);
        camera.setParameters(params);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.005;
        double targetRatio = (double) w / h;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


//    public static byte[] RotateBitmap(byte[] source, float angle) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap sourceBitmap = BitmapFactory.decodeByteArray(source, 0, source.length, options);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true).by;
//    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = rotateBitmap(bitmap, -90);


            if (pictureFile == null) {
                Log.d("TEST", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream out = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                mUriListener.onPhotoSaved(pictureFile.getPath());
            } catch (Exception e) {
                Log.d("TEST", "File not found: " + e.getMessage());
            } finally {
                mUriListener = null; // avoid cycle
            }
        }
    };

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "timeline");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + "img_" + System.currentTimeMillis() + ".jpg");
    }

}
