package jtpadilla.persondetector.impl;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Command(name = "MotionDetector", version = "MotionDetector 1.0", mixinStandardHelpOptions = true)
public class MotionDetector implements Runnable {

    @Option(names = { "-c", "--cascade-classifier" }, description = "Nombre del clasificador")
    String cascadeClassifier;

    @Option(names = { "-p", "--classifiers-path" }, description = "Ruta donde residen los clasificadores")
    String classifiersPath;

    @Option(names = { "-t", "--threads" }, description = "Numero de threads a utilizar")
    int theads;

    @Parameters(paramLabel = "<media files path>", arity = "1", description = "Ruta de los ficheros de video a procesar")
    String mediaFilesPath;

    private CascadeClassifier classifier;
    private Executor executor;

    @Override
    public void run() {

        try {

            // Executor
            this.executor = Executors.newFixedThreadPool(theads);

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
        executor.execute(()->consumeMedia(videoFile));
    }

    private void consumeMedia(File videoFile) {

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

}