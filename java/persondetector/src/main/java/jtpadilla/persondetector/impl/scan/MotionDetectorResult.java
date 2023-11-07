package jtpadilla.persondetector.impl.scan;

public sealed interface MotionDetectorResult {

    record UnknownExtension() implements MotionDetectorResult {};
    record UnknownFormat() implements MotionDetectorResult {};

    record MotionDetectedInVideo(long totalFrames, long initialFrame) implements MotionDetectorResult {};
    record MotionNoDetectedInVideo() implements MotionDetectorResult {};

    record MotionDetectedInPhoto() implements MotionDetectorResult {};
    record MotionNoDetectedInPhoto() implements MotionDetectorResult {};

}
