package jtpadilla.persondetector.impl.param;

import java.io.File;

public class HearCascade {

    public enum Type {
        FRONTALFACE_DEFAULT("haarcascade_frontalface_default.xml", "Para detectar rostros humanos"),
        EYE("haarcascade_eye.xml", "Para detectar ojos"),
        FULLBODY("haarcascade_fullbody.xml", "Para detectar cuerpos enteros"),
        LOWERBODY("haarcascade_lowerbody.xml", "Para detectar cuerpos inferiores"),
        UPPERBODY("haarcascade_upperbody.xml", "Para detectar cuerpos superiores");

        final private String filename;
        final private String description;

        Type(String filename, String description) {
            this.filename = filename;
            this.description = description;
        }

        public String getFilename() {
            return filename;
        }

        public String getDescription() {
            return description;
        }

    }

    static public File file(String path, Type type) throws ParametersException {
        final File result = new File(path, type.getFilename());
        if (result.exists() && result.isFile()) {
            return result;
        } else {
            throw new ParametersException(String.format("El fichero '%s' no existe.", result.getAbsolutePath()));
        }
    }

    static public Type getType(String hearCascade) throws ParametersException {
        try {
            return Type.valueOf(hearCascade);
        } catch (Throwable t) {
            throw new ParametersException(String.format("El tipo de cascada '%s' no esta soportado.", hearCascade));
        }
    }

}
