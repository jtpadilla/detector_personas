package jtpadilla.persondetector.impl.scan;

import jtpadilla.persondetector.impl.param.MultiScaleEnum;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.Optional;

public class MotionDetectorVideo extends MotionDetectorAbstract {

    public MotionDetectorVideo(CascadeClassifier cascadeClassifier, MultiScaleEnum parameters) {
        super(cascadeClassifier, parameters);
    }


    public MotionDetectorResult detect(File videoFile) {

        // Se lee el fichero de video
        VideoCapture videoCapture = new VideoCapture();
        try {

            // Se carga el video
            videoCapture.open(videoFile.getAbsolutePath());
            if (!videoCapture.isOpened()) {
                return new MotionDetectorResult.UnknownFormat();
            }

            // Se escanean sus imaganes
            return scanImg(videoCapture);

        } finally {
            videoCapture.release();
        }

    }

    private MotionDetectorResult scanImg(VideoCapture videoCapture) {

        final DetectionRules detectionRules = new DetectionRules(videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT));

        // Se procesan cada una de las imaganes que lo componen
        Mat img = new Mat();
        try {

            while (true) {

                // Se carga el siguiente frame
                videoCapture.read(img);
                if (img.empty()) {
                    break;
                }

                // Se intenta detecatr objetos en el video..,
                MatOfRect objects = new MatOfRect();
                try {
                    cascadeClassifier.detectMultiScale(img, objects, parameters.getScaleFactor(), parameters.getMinNeigbors());
                    Optional<MotionDetectorResult> motionDetectorResult = detectionRules.hasMotion(objects);
                    if (motionDetectorResult.isPresent()) {
                        return motionDetectorResult.get();
                    }
                } finally {
                    objects.release();
                }

            }
            return new MotionDetectorResult.MotionNoDetected();

        } finally {
            img.release();
        }

    }

    static private class DetectionRules {

        final double frmCount;
        long imagesCount = 0;

        private DetectionRules(double frmCount) {
            this.frmCount = frmCount;
        }

        public Optional<MotionDetectorResult> hasMotion(MatOfRect objects) {
            imagesCount++;
            if (objects.toArray().length > 0) {
                return Optional.of(new MotionDetectorResult.MotionDetected(imagesCount));
            } else {
                return Optional.empty();
            }
        }

    }

}
