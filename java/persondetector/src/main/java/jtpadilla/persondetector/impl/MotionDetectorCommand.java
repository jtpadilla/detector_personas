package jtpadilla.persondetector.impl;

import jtpadilla.persondetector.impl.fs.MediaProvider;
import jtpadilla.persondetector.impl.param.HearCascade;
import jtpadilla.persondetector.impl.param.MultiScaleEnum;
import jtpadilla.persondetector.impl.param.ParametersException;
import jtpadilla.persondetector.impl.scan.MotionDetectorPhoto;
import jtpadilla.persondetector.impl.scan.MotionDetectorVideo;
import org.opencv.objdetect.CascadeClassifier;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Optional;

@Command(name = "MotionDetector", version = "MotionDetector 1.0", mixinStandardHelpOptions = true)
public class MotionDetectorCommand implements Runnable {

    @Option(names = {"-c", "--cascade-classifier"}, description = "Nombre del clasificador")
    String cascadeClassifier;

    @Option(names = {"-p", "--classifiers-path"}, description = "Ruta donde residen los clasificadores")
    String classifiersPath;

    @Parameters(paramLabel = "<media files path>", arity = "1", description = "Ruta de los ficheros de video a procesar")
    String mediaFilesPath;

    @Override
    public void run() {

        try {

            // Cargar la clasificadora de cascada
            HearCascade.Type type = HearCascade.getType(cascadeClassifier);
            File cascadeFile = HearCascade.file(classifiersPath, type);
            CascadeClassifier classifier = new CascadeClassifier(cascadeFile.getPath());

            // Se crea el proveedor de los ficheros que hay que analizar
            MediaProvider mediaProvider = new MediaProvider(mediaFilesPath);

            // Informacion general
            System.out.format("Se utilizara el clasificador '%s' (%s)", type, type.getDescription());

            // Se procesan los videos
            final DetectorImpl detector = new DetectorImpl(classifier, MultiScaleEnum.MIDDLE);
            mediaProvider.collect(detector::dispatchMedia);

        } catch (ParametersException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

    static private class DetectorImpl {

        final private MotionDetectorVideo motionDetectorVideo;
        final private MotionDetectorPhoto motionDetectorPhoto;

        public DetectorImpl(CascadeClassifier cascadeClassifier, MultiScaleEnum parameters) {
            this.motionDetectorVideo = new MotionDetectorVideo(cascadeClassifier, parameters);
            this.motionDetectorPhoto = new MotionDetectorPhoto(cascadeClassifier, parameters);
        }

        private void dispatchMedia(File file) {
            Optional<String> extension = fileExtension(file);
            if (extension.isEmpty()) {
                System.out.format("%s: No se reconoce el tipo de fichero.", file.getAbsolutePath());
            } else {
                boolean detected = switch (extension.get()) {
                    case "AVI" -> motionDetectorVideo.detect(file);
                    case "XXX" -> motionDetectorPhoto.detect(file);
                    default -> false;
                };
                if (detected) {
                    System.out.format("%s: SI se ha detectado movimiento.", file.getAbsolutePath());
                } else {
                    System.out.format("%s: NO ha detectado movimiento.", file.getAbsolutePath());
                }
            }
        }

    }

    static private Optional<String> fileExtension(File file) {
        String name = file.getName();
        String[] split = name.split("\\.");
        if (split.length < 2) {
            return Optional.empty();
        } else {
            return Optional.of(split[split.length - 1].trim().toUpperCase());
        }
    }

}