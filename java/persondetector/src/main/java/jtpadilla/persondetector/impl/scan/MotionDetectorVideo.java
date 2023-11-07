package jtpadilla.persondetector.impl.scan;

import jtpadilla.persondetector.impl.param.MultiScaleEnum;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;

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
            ScanResult scanResult = scanImg(videoCapture);
            return new MotionDetectorResult.MotionDetected();

        } finally {
            videoCapture.release();
        }

    }

    record ScanResult(double frmCount, long imagesCount, boolean found) {}

    private ScanResult scanImg(VideoCapture videoCapture) {

        final double frmCount = videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
        long imagesCount = 0;

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
                    imagesCount++;
                    if (objects.toArray().length > 0) {
                        return new ScanResult(frmCount, imagesCount, true);
                    }
                } finally {
                    objects.release();
                }

            }
            return new ScanResult(frmCount, imagesCount, false);

        } finally {
            img.release();
        }

    }

}
