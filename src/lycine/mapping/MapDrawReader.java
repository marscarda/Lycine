package lycine.mapping;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.auth.Session;
import threonine.map.MapLayer;
import threonine.map.MapRecord;
import threonine.map.MappingAttlas;
import threonine.midlayer.MapRecordDraw;
//**************************************************************************
public class MapDrawReader {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //**********************************************************************
    public MapRecordDraw[] getDrawingLayer (long layerid, Session session) throws AppException, Exception {
        //******************************************************************
        MappingAttlas mapatlas = auriga.getMapsLambda();
        //******************************************************************
        //We first get the layer and if we have to we check auth
        MapLayer layer = mapatlas.getLayer(layerid);
        if (layer.projectID() != 0) {
            //Put some security here.
        }
        //******************************************************************
        MapRecord[] records = mapatlas.getMapRecords(layerid);
        if (records.length == 0) return new MapRecordDraw[0];
        int rcount = records.length;
        MapRecordDraw[] recordsdraw = new MapRecordDraw[rcount];
        //------------------------------------------------------------------
        for (int n = 0; n < rcount; n++) {
            recordsdraw[n] = new MapRecordDraw();
            recordsdraw[n].setMatch(records[n].getID());
            recordsdraw[n].setObjects(mapatlas.getObjectsByRecord(recordsdraw[n].matchID(), true));
        }
        //------------------------------------------------------------------
        return recordsdraw;
    }
    //**********************************************************************
}
//**************************************************************************
