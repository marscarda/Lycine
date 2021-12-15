package lycine.map;
//************************************************************************
import threonine.midlayer.MapRecordGraphic;
import histidine.AurigaObject;
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import threonine.map.MapFolder;
import threonine.midlayer.MapGraphicGetParam;
import threonine.midlayer.MapReaderGraphic;
import threonine.universe.Universe;
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
    public MapRecordGraphic[] getMapRecords (MapGraphicGetParam param, long userid) throws AppException, Exception {
        //==========================================================
        MapReaderGraphic mapreader = new MapReaderGraphic();
        mapreader.setUniverserLambda(auriga.getUniverseAtlas());
        mapreader.setMapsLambda(auriga.getMapsLambda());
        //==========================================================
        if (param.folderid != 0) 
            return byFolder(param.folderid, userid);
        //==========================================================
        if (param.universeid != 0)
            return bySubset(param.universeid, param.subsetid, userid);
        //==========================================================
        if (param.trialid != 0)
            return byTrial(param.trialid, param.nodecode, userid);
        //==========================================================
        //No valid param received. Return an empty array.
        return new MapRecordGraphic[0];
        //==========================================================
    }
    //********************************************************************
    private MapRecordGraphic[] byFolder (long folderid, long userid) throws AppException, Exception {
        if (userid != 0) {
            MapFolder folder = auriga.getMapsLambda().getMapFolder(folderid);
            //auriga.projectAtlas().checkAccess(folder.projectID(), userid, 1);
        }
        MapReaderGraphic reader = new MapReaderGraphic();
        reader.setMapsLambda(auriga.getMapsLambda());
        return reader.recordsByFolder(folderid, userid);
    }
    //********************************************************************
    private MapRecordGraphic[] bySubset (long universeid, long subsetid, long userid) throws AppException, Exception {
        if (userid != 0) {
            Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
            auriga.projectAtlas().checkAccess(universe.projectID(), userid, 1);
        }
        MapReaderGraphic reader = new MapReaderGraphic();
        reader.setUniverserLambda(auriga.getUniverseAtlas());
        return reader.recordsBySubset(universeid, subsetid, userid);
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
    private MapRecordGraphic[] byTrial (long trialid, long parentnode, long userid) throws AppException, Exception {
        
        //Trial trial = auriga.getTrialAtlas().getTrial(trialid);
        
        MapReaderGraphic reader = new MapReaderGraphic();
        reader.setUniverserLambda(auriga.getUniverseAtlas());
        StatNode[] nodes = auriga.getNewAtlas().getStatNodes(trialid, parentnode);
        List<MapRecordGraphic> maprecords = new ArrayList<>();
        MapRecordGraphic maprecord;
        for (StatNode node : nodes) {
            maprecord = reader.subsetGetRecord(node.subsetID());
            maprecord.reWriteId(node.nodeID());
            maprecords.add(maprecord);
        }
        return maprecords.toArray(new MapRecordGraphic[0]);
    }
    //********************************************************************
}
//************************************************************************
