package ge.steps.selfie_lapse;

import java.io.Serializable;

public class Selfie implements Serializable {
    private String path;
    private long date;
    private Emotion emotion;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Selfie selfie = (Selfie) o;

        return !(path != null ? !path.equals(selfie.path) : selfie.path != null);

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Selfie{" +
                "path='" + path + '\'' +
                ", date=" + date +
                ", emotion=" + emotion +
                '}';
    }
}
