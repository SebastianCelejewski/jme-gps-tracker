package pl.sebcel.location;

import javax.microedition.location.QualifiedCoordinates;

public interface LocationManagerGpsListener {

    public void locationUpdated(QualifiedCoordinates coordinates);

}