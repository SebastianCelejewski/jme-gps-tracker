package pl.sebcel.gpstracker.plugins.smsnotifier;

import javax.microedition.location.Coordinates;

public class CustomCoordinates extends Coordinates {
    private double latitude;
    private double longitude;
    private float altitude;

    public CustomCoordinates(double latitude, double longitude, float altitude) {
        super(latitude, longitude, altitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    private double toRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public float distance(Coordinates to) {
        double lat1 = to.getLatitude();
        double lat2 = this.getLatitude();

        double lon1 = to.getLongitude();
        double lon2 = this.getLongitude();

        double R = 6371000; // metres
        double f1 = toRadians(lat1);
        double f2 = toRadians(lat2);
        double df = toRadians(lat2 - lat1);
        double dl = toRadians(lon2 - lon1);

        double a = Math.sin(df / 2) * Math.sin(df / 2) + Math.cos(f1) * Math.cos(f2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        double c = 2 * atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c;

        return (float) d;
    }

    final static public double SQRT3 = 1.732050807568877294;

    static public double atan(double x) {
        boolean signChange = false;
        boolean Invert = false;
        int sp = 0;
        double x2, a;
        // check up the sign change
        if (x < 0.) {
            x = -x;
            signChange = true;
        }
        // check up the invertation
        if (x > 1.) {
            x = 1 / x;
            Invert = true;
        }
        // process shrinking the domain until x<PI/12
        while (x > Math.PI / 12) {
            sp++;
            a = x + SQRT3;
            a = 1 / a;
            x = x * SQRT3;
            x = x - 1;
            x = x * a;
        }
        // calculation core
        x2 = x * x;
        a = x2 + 1.4087812;
        a = 0.55913709 / a;
        a = a + 0.60310579;
        a = a - (x2 * 0.05160454);
        a = a * x;
        // process until sp=0
        while (sp > 0) {
            a = a + Math.PI / 6;
            sp--;
        }
        // invertation took place
        if (Invert)
            a = Math.PI / 2 - a;
        // sign change took place
        if (signChange)
            a = -a;
        //
        return a;
    }

    static public double atan2(double y, double x) {
        // if x=y=0
        if (y == 0. && x == 0.)
            return 0.;
        // if x>0 atan(y/x)
        if (x > 0.)
            return atan(y / x);
        // if x<0 sign(y)*(pi - atan(|y/x|))
        if (x < 0.) {
            if (y < 0.)
                return -(Math.PI - atan(y / x));
            else
                return Math.PI - atan(-y / x);
        }
        // if x=0 y!=0 sign(y)*pi/2
        if (y < 0.)
            return -Math.PI / 2.;
        else
            return Math.PI / 2.;
    }

}
