package com.example.ms_project_android;

//import com.jlibrosa.audio.JLibrosa;
//import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
//import com.jlibrosa.audio.wavFile.WavFileException;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import java.io.IOException;

public class MFCCExtractor {
    private JLibrosa jlibrosa;
    private int n_mfcc;

    public MFCCExtractor(int n_mfcc) {
        this.jlibrosa = new JLibrosa();
        this.n_mfcc = n_mfcc;
    }

    private float[] openFile(String file_path) {
        this.jlibrosa = new JLibrosa();

        float[] audioFeatureValues = new float[0];
        try {
            audioFeatureValues = this.jlibrosa.loadAndRead(file_path, 44100, 5);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        } catch (FileFormatNotSupportedException e) {
            e.printStackTrace();
        }

        return audioFeatureValues;
    }

    public float[][] getMFCC(String file_path) {
        float[] audioFeatureValues = this.openFile(file_path);

        return this.jlibrosa.generateMFCCFeatures(audioFeatureValues, this.jlibrosa.getSampleRate(), this.n_mfcc);
    }

    public float[] getMeanMFCC(String file_path) {
        float[][] mfccValues = getMFCC(file_path);
        float[] meanMFCCValues = this.jlibrosa.generateMeanMFCCFeatures(mfccValues, mfccValues.length, mfccValues[0].length);

        return meanMFCCValues;
    }
}
