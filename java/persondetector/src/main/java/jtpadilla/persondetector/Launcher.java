package jtpadilla.persondetector;

import jtpadilla.persondetector.impl.MotionDetector;
import picocli.CommandLine;

public class Launcher {

    static {
        System.load("/usr/lib/jni/libopencv_java454d.so");
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new MotionDetector()).execute(args));
    }

}
