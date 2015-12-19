package ge.steps.selfie_lapse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileStorage implements StorageAPI {
    private String dir;
    private String file;
    public FileStorage(String dir, String file){
        this.dir = dir;
        this.file = file;
    }

    @Override
    public Selfie createSelfie() {
        Selfie s = new Selfie();
        s.setDate(System.currentTimeMillis());
        return s;
    }

    @Override
    public void saveSelfie(Selfie selfie) throws IOException, ClassNotFoundException {
        File f = new File(dir, file);
        ArrayList<Selfie> selfies = new ArrayList<>();

        selfies.addAll(readSelfies(f));
        selfies.add(selfie);

        saveFile(f, selfies);

    }

    @Override
    public void deleteSelfie(String path) throws IOException, ClassNotFoundException {
        File f = new File(dir, file);
        ArrayList<Selfie> selfies = readSelfies(f);

        for(Selfie s: selfies){
            if(s.getPath().equals(path)){
                selfies.remove(s);
                break;
            }
        }

        saveFile(f, selfies);
    }

    @Override
    public void updateSelfie(Selfie selfie) throws IOException, ClassNotFoundException {
        File f = new File(dir, file);
        ArrayList<Selfie> selfies = readSelfies(f);

        for(Selfie s: selfies){
            if(s.getPath().equals(selfie.getPath())){
                selfies.remove(s);
                selfies.add(selfie);
                break;
            }
        }

        saveFile(f, selfies);
    }

    @Override
    public List<Selfie> getAllSelfies() throws IOException, ClassNotFoundException {
        return readSelfies(new File(dir, file));
    }


    private ArrayList<Selfie> readSelfies(File f) throws IOException, ClassNotFoundException {
        if (f.exists()) {
            ObjectInputStream fi = new ObjectInputStream(new FileInputStream(f));
            return (ArrayList<Selfie>) fi.readObject();
        }
        return new ArrayList<>();
    }

    private void saveFile(File f, ArrayList<Selfie> selfies) throws IOException {
        FileOutputStream fo = new FileOutputStream(f);
        new ObjectOutputStream(fo).writeObject(selfies);
    }
}