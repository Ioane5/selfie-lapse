package ge.steps.selfie_lapse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface StorageAPI {
    Selfie createSelfie();
    void saveSelfie(Selfie selfie) throws IOException, ClassNotFoundException;
    void deleteSelfie(String path) throws IOException, ClassNotFoundException;
    void updateSelfie(Selfie selfie) throws IOException, ClassNotFoundException;

    List<Selfie> getAllSelfies() throws IOException, ClassNotFoundException;

}
