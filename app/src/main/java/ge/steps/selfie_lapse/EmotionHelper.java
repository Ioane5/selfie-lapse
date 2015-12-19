package ge.steps.selfie_lapse;

import java.util.List;

/**
 * Created by ioane5 on 12/19/15.
 */
public class EmotionHelper {

    public enum EmotionType {
        anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
    }

    public interface SyncCallback {
        void onError();

        void onSuccess();
    }

    /**
     * May need context, dont know. Use volley maybe. :S
     *
     * @param selfies
     */
    public static void syncEmotion(List<Selfie> selfies, SyncCallback callback) {

    }

    public static void sortByEmotion(List<Selfie> selfies, EmotionType type) {

    }
}
