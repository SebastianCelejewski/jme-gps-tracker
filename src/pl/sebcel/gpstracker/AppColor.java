package pl.sebcel.gpstracker;

public class AppColor {

    private int red;
    private int green;
    private int blue;
    private boolean blinking;

    public AppColor(int red, int green, int blue, boolean blinking) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.blinking = blinking;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public boolean isBlinking() {
        return blinking;
    }
}