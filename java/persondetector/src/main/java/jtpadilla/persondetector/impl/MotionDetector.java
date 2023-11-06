package jtpadilla.persondetector.impl;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Command(name = "MotionDetector", version = "MotionDetector 1.0", mixinStandardHelpOptions = true)
public class MotionDetector implements Runnable {

    @Option(names = {"-c", "--cascade-classifier"}, description = "Nombre del clasificador")
    String cascadeClassifier;

    @Option(names = {"-p", "--classifiers-path"}, description = "Ruta donde residen los clasificadores")
    String classifiersPath;

    @Parameters(paramLabel = "<media files path>", arity = "1", description = "Ruta de los ficheros de video a procesar")
    String mediaFilesPath;

    private CascadeClassifier classifier;

    @Override
    public void run() {

        try {

            // Cargar la clasificadora de cascada
            File cascadeFile = HearCascade.file(classifiersPath, cascadeClassifier);
            this.classifier = new CascadeClassifier(cascadeFile.getPath());

            // Se crea el proveedor de los ficheros que hay que analizar
            MediaProvider mediaProvider = new MediaProvider(mediaFilesPath);

            // Se procesan los videos
            mediaProvider.collect(this::dispatchMedia);

        } catch (MediaDetectorException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

    private void dispatchMedia(File videoFile) {
        consumeVideo(videoFile);
    }

    private void consumeImage(File videoFile) {

        System.out.format("Procesando fichero '%s'%n", videoFile.getAbsolutePath());

        // Leer el archivo de video
        Mat video = Imgcodecs.imread(videoFile.getPath());

        // Detectar objetos en el video
        MatOfRect objects = new MatOfRect();
        classifier.detectMultiScale(video, objects);

        // Imprimir los resultados
        for (Rect object : objects.toArray()) {
            System.out.println("Encontrado un objeto en " + videoFile.getName() + ": " + object);

            // Dibujar un rectángulo alrededor del objeto
            // Imgproc.rectangle(video, object.tl(), object.br(), new Scalar(0, 255, 0), 2);
        }

        // Guardar el video con los resultados
        // Imgcodecs.imwrite(videoFile.getPath(), video);

    }


    private void consumeVideo(File videoFile) {

        System.out.format("Procesando fichero '%s'%n", videoFile.getAbsolutePath());


        VideoCapture capture = new VideoCapture();
        capture.open(videoFile.getAbsolutePath());

        if (capture.isOpened()) {
            double frmCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
            capture.
            System.out.println("OK: frmCount = " + frmCount);
        } else {
            System.out.println("ERR: Capture is not opened!");
            return;
        }

        capture.release();

        // # loop over frames from the video file stream
//        Mat img = new Mat();
//        double fps = capture.get(Videoio.CAP_PROP_FPS);
//        Size size = new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH), capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
//
//        while (true){
//            capture.read(img);
//            if (img.empty())
//                break;
//            HashMap<String, List> result = forwardImageOverNetwork(img, dnnNet, outputLayers);
//
//            ArrayList<Rect2d> boxes = (ArrayList<Rect2d>)result.get("boxes");
//            ArrayList<Float> confidences = (ArrayList<Float>) result.get("confidences");
//            ArrayList<Integer> class_ids = (ArrayList<Integer>)result.get("class_ids");
//
//        }

    }

}