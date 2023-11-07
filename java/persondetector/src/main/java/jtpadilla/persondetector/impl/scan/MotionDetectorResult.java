package jtpadilla.persondetector.impl.scan;

public sealed interface MotionDetectorResult {
    record UnknownExtension() implements MotionDetectorResult {};
    record UnknownFormat() implements MotionDetectorResult {};
    record MotionDetected(long initialFrame) implements MotionDetectorResult {};
    record MotionNoDetected() implements MotionDetectorResult {};
}
