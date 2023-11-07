package jtpadilla.persondetector.impl;

import jtpadilla.persondetector.impl.fs.MediaProvider;
import jtpadilla.persondetector.impl.param.HearCascadeParameter;
import jtpadilla.persondetector.impl.param.MultiScaleEnum;
import jtpadilla.persondetector.impl.param.ParametersException;
import jtpadilla.persondetector.impl.scan.MotionDetectorPhoto;
import jtpadilla.persondetector.impl.scan.MotionDetectorResult;
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
    String cascadeClassifierParameter;

    @Option(names = {"-s", "--multi-scale"}, description = "Parametros para el MultiScale")
    String multiScaleParameter;

    @Option(names = {"-p", "--classifiers-path"}, description = "Ruta donde residen los clasificadores")
    String classifiersPathParemeter;

    @Parameters(paramLabel = "<media files path>", arity = "1", description = "Ruta de los ficheros de video a procesar")
    String mediaFilesPathParemeter;

    @Override
    public void run() {

        try {

            // Cargar la clasificadora de cascada
            HearCascadeParameter.Type type = HearCascadeParameter.getType(cascadeClassifierParameter);
            final File cascadeFile = HearCascadeParameter.file(classifiersPathParemeter, type);
            final CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadeFile.getPath());
            System.out.format("Se utilizara el clasificador '%s' (%s)%n", type, type.getDescription());

            // Cargar el parametro de MulstiScale
            final MultiScaleEnum multiScaleEnum = MultiScaleEnum.from(multiScaleParameter);
            System.out.format("Se utilizaran los parametros de MultiScale '%s' (%s)%n", multiScaleEnum, multiScaleEnum.getDesctription());

            // Se crea el proveedor de los ficheros que hay que analizar
            final MediaProvider mediaProvider = new MediaProvider(mediaFilesPathParemeter);

            // Se instancian los clasificadores
            final DetectorImpl detector = new DetectorImpl(cascadeClassifier, MultiScaleEnum.MIDDLE);

            // Se procesan los videos
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
                System.out.format("%s: El fichero no tiene extension.%n", file.getAbsolutePath());
            } else {
                MotionDetectorResult result = switch (extension.get()) {
                    case "AVI", "MP4" -> motionDetectorVideo.detect(file);
                    case "XXX" -> motionDetectorPhoto.detect(file);
                    default -> new MotionDetectorResult.UnknownExtension();
                };

                // #java21
                if (result instanceof MotionDetectorResult.MotionDetectedInVideo detectedInVideo) {
                    System.out.format("%s: DETECTADO MOVIMIENTO EN VIDEO (%d/%d)!%n", file.getAbsolutePath(), detectedInVideo.initialFrame(), detectedInVideo.totalFrames());
                } else if (result instanceof MotionDetectorResult.MotionNoDetectedInVideo) {
                    System.out.format("%s: No se ha detectedo movimiento en video%n", file.getAbsolutePath());
                } else if (result instanceof MotionDetectorResult.MotionDetectedInPhoto) {
                    System.out.format("%s: DETECTADO MOVIMIENTO EN FOTO%n", file.getAbsolutePath());
                } else if (result instanceof MotionDetectorResult.MotionNoDetectedInPhoto) {
                    System.out.format("%s: No se ha detectedo movimiento en foto%n", file.getAbsolutePath());
                } else if (result instanceof MotionDetectorResult.UnknownFormat) {
                    System.out.format("%s: Dificultades al procesar el fichero%n", file.getAbsolutePath());
                } else if (result instanceof MotionDetectorResult.UnknownExtension) {
                    System.out.format("%s: Extension del fichero no soportada%n", file.getAbsolutePath());
                } else {
                    throw new RuntimeException("Resultado inesperado!");
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