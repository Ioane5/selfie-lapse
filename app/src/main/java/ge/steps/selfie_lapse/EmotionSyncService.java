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
            // TODO get list of emotion pass and sync them. :)
            // use emotion helper class.
            // this method is in background.
            StorageAPI storage = FileStorage.getSelfieStore(getApplicationContext());
            // don't use quota untill it's finished!
            List<Selfie> selfies = storage.getAllSelfies();
            EmotionHelper.syncEmotion(selfies);
            storage.saveAll(selfies);
        }
    }

}
