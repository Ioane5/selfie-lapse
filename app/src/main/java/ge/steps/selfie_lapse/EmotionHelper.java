package ge.steps.selfie_lapse;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EmotionHelper {

    public enum EmotionType {
        anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
    }

    /**
     * May need context, dont know. Use volley maybe. :S
     *
     * @param selfies
     */
    public static void syncEmotion(List<Selfie> selfies) {
        for(Selfie s: selfies){
            int status = sync(s);
            switch (status){
                case 200:
                    break;
                default:
                    Log.d("blablabla", ""+status);
            }
        }
    }

    private static int sync(Selfie s){
        try{
            URL url = new URL("https://api.projectoxford.ai/emotion/v1.0/recognize");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/octet-stream");

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
                return connection.getResponseCode();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public static void sortByEmotion(List<Selfie> selfies, EmotionType type) {

    }
}
