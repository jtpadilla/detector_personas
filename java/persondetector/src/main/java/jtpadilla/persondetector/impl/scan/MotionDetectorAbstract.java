package jtpadilla.persondetector.impl.scan;

import jtpadilla.persondetector.impl.param.MultiScaleEnum;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

abstract public class MotionDetectorAbstract {

    final protected CascadeClassifier cascadeClassifier;
    final protected MultiScaleEnum parameters;

    public MotionDetectorAbstract(CascadeClassifier cascadeClassifier, MultiScaleEnum parameters) {
        this.cascadeClassifier = cascadeClassifier;
        this.parameters = parameters;
    }

    abstract public boolean detect(File file);

}
