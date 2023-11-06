package jtpadilla.persondetector.impl;

import java.io.File;
import java.util.function.Consumer;

public class MediaProvider {

    final private File rootDir;

    public MediaProvider(String mediaFilesPath) {
        this.rootDir = new File(mediaFilesPath);
        if (!rootDir.isDirectory() || !rootDir.exists()) {
            throw new IllegalArgumentException(String.format("La ruta proporcionada para obtener los videos no existe o no es un directorio '%s", mediaFilesPath));
        }
    }

    public void collect(Consumer<File> mediaConsumer) {
        collect(mediaConsumer, rootDir);
    }

    private void collect(Consumer<File> mediaConsumer, File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File current : files) {
                if (current.isFile()) {
                    mediaConsumer.accept(current);
                } else if (current.isDirectory()) {
                    collect(mediaConsumer, current);
                } else {
                    System.err.printf("El fichero '%s' no se procesara.%n", current.getAbsolutePath());
                }
            }
        }
    }

}
