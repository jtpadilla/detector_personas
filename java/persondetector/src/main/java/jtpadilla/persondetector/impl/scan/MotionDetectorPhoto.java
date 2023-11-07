package jtpadilla.persondetector.impl.scan;

import jtpadilla.persondetector.impl.param.MultiScaleEnum;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

public class MotionDetectorPhoto extends MotionDetectorAbstract {

    public MotionDetectorPhoto(CascadeClassifier cascadeClassifier, MultiScaleEnum parameters) {
        super(cascadeClassifier, parameters);
    }

    public MotionDetectorResult detect(File photoFile) {

        // Leer el archivo de video
        Mat img = Imgcodecs.imread(photoFile.getPath());
        try {

            // Detectar objetos en el video
            MatOfRect objects = new MatOfRect();
            try {
                cascadeClassifier.detectMultiScale(img, objects, parameters.getScaleFactor(), parameters.getMinNeigbors());
                return objects.toArray().length > 0 ? new MotionDetectorResult.MotionDetected() : new MotionDetectorResult.MotionNoDetected();
            } finally {
                objects.release();
            }

        } finally {
            img.release();
        }

    }

}
