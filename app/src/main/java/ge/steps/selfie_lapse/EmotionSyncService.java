package ge.steps.selfie_lapse;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

public class EmotionSyncService extends IntentService {

    public static final String TAG = EmotionSyncService.class.getSimpleName();

    public static final String ACTION_SYNC_EMOTION = "emotion.sync";

    public EmotionSyncService() {
        super("EmotionSyncService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i(TAG, "now we handle emotion sync");
            // TODO get list of emotion pass and sync them. :)
            // use emotion helper class.
            // this method is in background.
            FileStorage storage = FileStorage.getSelfieStore(getApplicationContext());
            for (Selfie s : storage.getAllSelfies()) {
                // remove bad faces.
            }
        }
    }

}
