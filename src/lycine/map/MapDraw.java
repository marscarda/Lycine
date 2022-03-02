package lycine.map;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.auth.Session;
import threonine.map.MapLayer;
import threonine.map.MapRecord;
import threonine.midlayer.MapRecordDraw;
//**************************************************************************
public class MapDraw {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //**********************************************************************
    public MapRecordDraw[] getDrawingLayer (long layerid, Session session) throws AppException, Exception {
        //******************************************************************
        //We first get the layer and if we have to we check auth
        MapLayer layer = auriga.getMapsLambda().getLayer(layerid);
        if (layer.projectID() != 0) {
            //Put some security here.
        }
        //******************************************************************
        MapRecord[] records = auriga.getMapsLambda().getMapRecords(layerid);
        if (records.length == 0) return new MapRecordDraw[0];
        int rcount = records.length;
        
        MapRecordDraw[] recordsg = new MapRecordDraw[rcount];
        
        
        
        return new MapRecordDraw[0];
    }
    //**********************************************************************
}
//**************************************************************************
