package jtpadilla.persondetector;

import jtpadilla.persondetector.impl.MotionDetector;
import picocli.CommandLine;

public class Launcher {

    public static void main(String[] args) {
        System.exit(new CommandLine(new MotionDetector()).execute(args));
    }

}
