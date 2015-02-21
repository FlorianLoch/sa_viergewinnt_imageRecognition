package de.dhbw.mbfl.imagedetection;

/**
 * Created by florian on 21.02.15.
 */
public class ImageAnalysisException extends Exception {

    private String message;

    public ImageAnalysisException(String message) {
        super(message);
    }

}
