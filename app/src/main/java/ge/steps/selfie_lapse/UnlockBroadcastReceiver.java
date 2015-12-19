package ge.steps.selfie_lapse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UnlockBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = UnlockBroadcastReceiver.class.getSimpleName();

    public UnlockBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Unlock : " + action);
        try {
            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                Intent service = new Intent(context, SelfieCaptureService.class);
                context.startService(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
