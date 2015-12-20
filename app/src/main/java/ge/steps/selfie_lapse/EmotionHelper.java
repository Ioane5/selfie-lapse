package ge.steps.selfie_lapse;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EmotionHelper {

    private static final String TAG = EmotionHelper.class.getSimpleName();

    public enum EmotionType {
        anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
    }

    /**
     * May need context, dont know. Use volley maybe. :S
     *
     * @param selfies
     */
    public static void syncEmotion(List<Selfie> selfies) {
        for (Selfie s : selfies) {
            int status = sync(s);
            switch (status) {
                case 200:
                    break;
                default:
                    Log.d(TAG, "" + status);
            }
        }
    }

    private static int sync(Selfie s) {
        if (s.getEmotion() != null)
            return 200; // don't need to sync same images.
        try {
            URL url = new URL("https://api.projectoxford.ai/emotion/v1.0/recognize");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/octet-stream");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", "119957c1637b495ca5f671096fc6c55b");


            OutputStream output = connection.getOutputStream();
            InputStream file = new FileInputStream(new File(s.getPath()));
            System.out.println("Size: " + file.available());
            try {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = file.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                if (connection.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    updateSelfie(sb.toString(), s);
                }

                return connection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                file.close();
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void updateSelfie(String json, Selfie s) throws JSONException, NoFaceDetectedException {
        Log.d(TAG, "JSON " + json);
        JSONArray j = new JSONArray(json);
        if (j.length() == 0) throw new NoFaceDetectedException();
        else {
            JSONObject scores = j.getJSONObject(0).getJSONObject("scores");
            Emotion emotion = new Emotion();
            emotion.setAnger(scores.getDouble("anger"));
            emotion.setContempt(scores.getDouble("contempt"));
            emotion.setDisgust(scores.getDouble("disgust"));
            emotion.setFear(scores.getDouble("fear"));
            emotion.setHappiness(scores.getDouble("happiness"));
            emotion.setNeutral(scores.getDouble("neutral"));
            emotion.setSadness(scores.getDouble("sadness"));
            emotion.setSurprise(scores.getDouble("surprise"));
            s.setEmotion(emotion);
        }
    }
}
