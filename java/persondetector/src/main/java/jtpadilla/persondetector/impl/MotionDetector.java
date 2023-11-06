package jtpadilla.persondetector.impl;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

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
            HearCascade.Type type = HearCascade.getType(cascadeClassifier);
            File cascadeFile = HearCascade.file(classifiersPath, type);
            this.classifier = new CascadeClassifier(cascadeFile.getPath());

            // Se crea el proveedor de los ficheros que hay que analizar
            MediaProvider mediaProvider = new MediaProvider(mediaFilesPath);

            // Informacion general
            System.out.format("Se utilizara el clasificador '%s' (%s)", type, type.getDescription());

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

        // Se lee el fichero de video
        VideoCapture capture = new VideoCapture();
        try {

            // Se carga el video
            capture.open(videoFile.getAbsolutePath());
            if (!capture.isOpened()) {
                System.err.format("'%s' no se puede procesar!%n", videoFile.getAbsolutePath());
            }

            // Se escanean sus imaganes
            ScanResult scanResult = scanImg(capture);
            System.out.format("'%s'-->%s%n", videoFile.getAbsolutePath(), scanResult);

        } finally {
            capture.release();
        }

    }

    record ScanResult(double frmCount, long imagesCount, boolean found) {}

    private ScanResult scanImg(VideoCapture capture) {

        final double frmCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
        long imagesCount = 0;

        // Se procesan cada una de las imaganes que lo componen
        Mat img = new Mat();
        while(true) {

            // Se carga el siguiente frame
            capture.read(img);
            if (img.empty()) {
                break;
            }

            // Se intenta detecatr objetos en el video..,
            MatOfRect objects = new MatOfRect();
            classifier.detectMultiScale(img, objects);
            imagesCount++;
            if (objects.toArray().length > 0) {
                return new ScanResult(frmCount, imagesCount, true);
            }

        }
        return new ScanResult(frmCount, imagesCount, false);

    }

}