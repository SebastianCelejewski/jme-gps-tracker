package pl.sebcel.gpstracker.storage;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import pl.sebcel.gpstracker.utils.Logger;

public class Storage {

    private Logger log = Logger.getLogger();

    private Hashtable data = new Hashtable();
    private Vector recordIds;

    public void load() {
        try {
            log.debug("[Storage] Loading data from storage");
            data.clear();

            RecordStore recordStore = RecordStore.openRecordStore("jme-gps-tracker", true);
            recordIds = new Vector();
            RecordEnumeration recordEnumeration = recordStore.enumerateRecords(null, null, false);
            System.out.println("Found " + recordStore.getNumRecords() + " records");
            while (recordEnumeration.hasNextElement()) {
                int recordId = recordEnumeration.nextRecordId();
                System.out.println("Loading record " + recordId);
                recordIds.addElement(new Integer(recordId));
                try {
                    byte[] recordBytes = recordStore.getRecord(recordId);
                    String recordData = new String(recordBytes);
                    System.out.println("Record data: " + recordData);
                    int splitPosition = recordData.indexOf("=");
                    if (splitPosition != -1) {
                        String key = recordData.substring(0, splitPosition);
                        String value = recordData.substring(splitPosition + 1);
                        data.put(key, value);
                    }
                } catch (Exception ex) {
                    log.debug("[Storage] Failed to load record " + recordId + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            recordEnumeration.destroy();
            log.debug("[Storage] Successfully loaded data from storage");
        } catch (Exception ex) {
            log.debug("[Storage] Failed to load data from storage: " + ex);
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            log.debug("[Storage] Saving data to storage");
            clear();
            recordIds = new Vector();
            RecordStore recordStore = RecordStore.openRecordStore("jme-gps-tracker", true);
            Enumeration keys = data.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) data.get(key);
                String recordData = key + "=" + value;
                System.out.println("Key: " + key + ", value:" + value);
                System.out.println("Record data: " + recordData);
                byte[] recordBytes = recordData.getBytes();
                recordIds.addElement(new Integer(recordStore.getNextRecordID()));
                recordStore.addRecord(recordBytes, 0, recordBytes.length);
            }

            log.debug("[Storage] Successfully saved data to storage");
        } catch (Exception ex) {
            log.debug("[Storage] Failed to save data to storage: " + ex);
            ex.printStackTrace();
        }
    }

    public String getValue(String key) {
        if (data.containsKey(key)) {
            return (String) data.get(key);
        } else {
            return "";
        }
    }

    public void setValue(String key, String value) {
        if (value != null) {
            data.put(key, value);
        } else {
            data.remove(key);
        }
    }

    private void clear() throws Exception {
        RecordStore recordStore = RecordStore.openRecordStore("jme-gps-tracker", true);
        for (int i = 0; i < recordIds.size(); i++) {
            int recordId = ((Integer) recordIds.elementAt(i)).intValue();
            try {
                recordStore.deleteRecord(recordId);
            } catch (Exception ex) {
                log.debug("[Storage] Failed to delete record " + recordId + ": " + ex.getMessage());
            }
        }
    }
}