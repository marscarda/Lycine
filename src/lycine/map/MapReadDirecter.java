package lycine.map;
//************************************************************************
import threonine.midlayer.MapRecordDraw;
import histidine.AurigaObject;
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import threonine.midlayer.MapGraphicGetParam;
import threonine.midlayer.MapReaderGraphic;
import tryptophan.trial.StatNode;
//************************************************************************
/**
 *
 * @author marianoscardaccione
 */
public class MapReadDirecter {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public MapRecordDraw[] getMapRecords (MapGraphicGetParam param) throws AppException, Exception {
        //==========================================================
        MapReaderGraphic mapreader = new MapReaderGraphic();
        mapreader.setUniverserLambda(auriga.getUniverseAtlas());
        mapreader.setMapsLambda(auriga.getMapsLambda());
        //==========================================================
        if (param.folderid != 0) 
            return byFolder(param.folderid);
        //==========================================================
        if (param.universeid != 0)
            return bySubset(param.universeid, param.subsetid);
        //==========================================================
        if (param.trialid != 0)
            return byTrial(param.trialid, param.nodecode);
        //==========================================================
        //No valid param received. Return an empty array.
        return new MapRecordDraw[0];
        //==========================================================
    }
    //********************************************************************
    private MapRecordDraw[] byFolder (long folderid) throws AppException, Exception {
        MapReaderGraphic reader = new MapReaderGraphic();
        reader.setMapsLambda(auriga.getMapsLambda());
        return reader.recordsByFolder(folderid);
    }
    //********************************************************************
    private MapRecordDraw[] bySubset (long universeid, long subsetid) throws AppException, Exception {
        MapReaderGraphic reader = new MapReaderGraphic();
        reader.setUniverserLambda(auriga.getUniverseAtlas());
        return reader.recordsBySubset(universeid, subsetid);
    }
    //********************************************************************
    /**
     * Returns an array of maprecords given the trial and parent node.
     * @param trialid
     * @param parentnode
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    private MapRecordDraw[] byTrial (long trialid, long parentnode) throws AppException, Exception {
        
        //Trial trial = auriga.getTrialAtlas().getTrial(trialid);
        
        MapReaderGraphic reader = new MapReaderGraphic();
        reader.setUniverserLambda(auriga.getUniverseAtlas());
        StatNode[] nodes = auriga.getNewAtlas().getStatNodes(trialid, parentnode);
        List<MapRecordDraw> maprecords = new ArrayList<>();
        MapRecordDraw maprecord;
        for (StatNode node : nodes) {
            maprecord = reader.subsetGetRecord(node.subsetID());
            maprecord.reWriteId(node.nodeID());
            maprecords.add(maprecord);
        }
        return maprecords.toArray(new MapRecordDraw[0]);
    }
    //********************************************************************
}
//************************************************************************
