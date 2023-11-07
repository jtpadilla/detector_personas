package jtpadilla.persondetector.impl.param;

public enum MultiScaleEnum {

    MIDDLE(1.1, 3, "pues no se"),
    HIGH(1.05, 5, "pues no se"),
    HIGHEST(1.2, 1, "pues no se");

    private final double scaleFactor;
    private final int minNeigbors;
    private final String desctription;

    MultiScaleEnum(double scaleFactor, int minNeigbors, String desctription) {
        this.scaleFactor = scaleFactor;
        this.minNeigbors = minNeigbors;
        this.desctription = desctription;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public int getMinNeigbors() {
        return minNeigbors;
    }

    public String getDesctription() {
        return desctription;
    }

    static public MultiScaleEnum from(String multiScaleString) throws ParametersException {
        try {
            return MultiScaleEnum.valueOf(multiScaleString);
        } catch (Throwable t) {
            throw new ParametersException(String.format("El parametro de MultiScale '%s' no esta soportado.", multiScaleString));
        }
    }

}
