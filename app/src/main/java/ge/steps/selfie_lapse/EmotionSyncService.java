package ge.steps.selfie_lapse;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.util.List;

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
            StorageAPI storage = FileStorage.getSelfieStore(getApplicationContext());
            List<Selfie> selfies = storage.getAllSelfies();
            EmotionHelper.syncEmotion(selfies);
            storage.saveAll(selfies);
        }
    }

}
