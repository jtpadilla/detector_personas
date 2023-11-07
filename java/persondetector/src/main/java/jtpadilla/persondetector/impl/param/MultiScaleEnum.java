package jtpadilla.persondetector.impl.param;

public enum MultiScaleEnum {

    MIDDLE(1.1, 3),
    HIGH(1.05, 5),
    HIGHEST(1.2, 1);

    private final double scaleFactor;
    private final int minNeigbors;

    MultiScaleEnum(double scaleFactor, int minNeigbors) {
        this.scaleFactor = scaleFactor;
        this.minNeigbors = minNeigbors;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public int getMinNeigbors() {
        return minNeigbors;
    }

}
