package ge.steps.selfie_lapse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface StorageAPI {
    Selfie createSelfie();
    void saveSelfie(Selfie selfie);
    void deleteSelfie(String path);
    void updateSelfie(Selfie selfie);

    List<Selfie> getAllSelfies();

    void saveAll(List<Selfie> selfies);
}
