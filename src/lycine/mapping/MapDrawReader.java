package lycine.mapping;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.auth.Session;
import threonine.mapping.MapLayer;
import threonine.mapping.MapRecord;
import threonine.mapping.MappingAttlas;
import threonine.midlayer.MapRecordDraw;
import threonine.universe.SubSet;
import threonine.universe.TemplateAtlas;
import threonine.universe.UniverseAtlas;
//**************************************************************************
public class MapDrawReader {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //**********************************************************************
    /**
     * 
     * @param layerid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
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
    /**
     * 
     * @param universeid
     * @param subsetid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapRecordDraw[] getDrawingSubset (long universeid, long subsetid, Session session) throws AppException, Exception {
        UniverseAtlas uatlas = auriga.getUniverseAtlas();
        SubSet[] subsets = uatlas.getSubsets(universeid, subsetid);
        if (subsets.length == 0) return new MapRecordDraw[0];
        int scount = subsets.length;
        MapRecordDraw[] recordsdraw = new MapRecordDraw[scount];
        for (int n = 0; n < scount; n++) {
            recordsdraw[n] = new MapRecordDraw();
            recordsdraw[n].relationid = subsets[n].getSubsetID();
            recordsdraw[n].setObjects(uatlas.getObjectsBySubset(recordsdraw[n].relationid, true));
        }
        return recordsdraw;
    }
    //**********************************************************************
    /**
     * 
     * @param universeid
     * @param subsetid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapRecordDraw[] getDrawingSubsetTemplate (long universeid, long subsetid) throws AppException, Exception {
        TemplateAtlas tatlas = auriga.templateUniverseAtlas();
        SubSet[] subsets = tatlas.getSubsets(universeid, subsetid);
        if (subsets.length == 0) return new MapRecordDraw[0];
        int scount = subsets.length;
        MapRecordDraw[] recordsdraw = new MapRecordDraw[scount];
        for (int n = 0; n < scount; n++) {
            recordsdraw[n] = new MapRecordDraw();
            recordsdraw[n].relationid = subsets[n].getSubsetID();
            recordsdraw[n].setObjects(tatlas.getFeaturesBySubset(recordsdraw[n].relationid, true));
        }
        return recordsdraw;
    }
    //**********************************************************************
}
//**************************************************************************
