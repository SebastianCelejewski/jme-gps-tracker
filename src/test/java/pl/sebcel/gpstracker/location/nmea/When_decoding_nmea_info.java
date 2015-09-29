package pl.sebcel.gpstracker.location.nmea;

import junit.framework.Assert;
import junit.framework.TestCase;
import pl.sebcel.gpstracker.TestUtils;
import pl.sebcel.gpstracker.location.NmeaDecoder;
import pl.sebcel.gpstracker.location.NmeaInfo;
import pl.sebcel.gpstracker.location.NmeaInfo.SatelliteInfo;

public class When_decoding_nmea_info extends TestCase {

    private NmeaDecoder cut = new NmeaDecoder();
    private String nmeaInfoString;
    private NmeaInfo result;

    protected void setUp() {
        nmeaInfoString = TestUtils.loadFile("/nmea.txt");
        result = cut.decode(nmeaInfoString);
    }

    public void test_should_return_number_of_satellites() {
        Assert.assertEquals(4, result.getNumberOfSatellitesInView());
    }

    public void test_should_return_quality_of_gps_signal() {
        Assert.assertEquals(1, result.getGpsQuality());
    }

    public void test_should_return_SNR() {
        Assert.assertEquals(24, ((SatelliteInfo) result.getSatellitesInfo().get(new Integer(1))).getSnr().intValue());
    }
}