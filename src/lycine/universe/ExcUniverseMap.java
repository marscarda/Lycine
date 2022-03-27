package lycine.universe;
//**************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import methionine.finance.FinanceAtlas;
import methionine.project.ProjectLambda;
import threonine.mapping.LayerUse;
import threonine.mapping.MapErrorCodes;
import threonine.mapping.MapRecord;
import threonine.mapping.MappingAttlas;
import threonine.midlayer.MapObjectGraphic;
import threonine.midlayer.MapReaderGraphic;
import threonine.midlayer.MapRecordDraw;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
//**************************************************************************
/**
 *
 * @author marianoscardaccione
 */
public class ExcUniverseMap {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //======================================================================
    //When get children subsets. We set the map status of current subset.
//    boolean mapstatus = false;
//    public boolean mapStatus () { return mapstatus; }
    //**********************************************************************
    public void setMapRecordToSubset (long subsetid, long recordid, Session session) throws AppException, Exception {
        //******************************************************************
        UniverseAtlas uatlas = auriga.getUniverseAtlas();
        MappingAttlas matlas = auriga.getMapsLambda();
        ProjectLambda patlas = auriga.projectAtlas();
        FinanceAtlas fatlas = auriga.getBillingLambda();
        //******************************************************************
        //We use the main server
        uatlas.usesrvFullMainSrv();
        matlas.usesrvFullMainSrv();
        patlas.usesrvFullMainSrv();
        fatlas.usesrvFullMainSrv();
        //******************************************************************
        //We get the subset and the universe
        SubSet subset = uatlas.getSubset(0, subsetid);
        Universe universe = uatlas.getUniverse(subset.getUniverseID());
        //******************************************************************
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 2);
        //******************************************************************
        MapRecord record = auriga.getMapsLambda().getMapRecord(recordid);
        LayerUse use;
        try { use = matlas.getLayerUse(); }
        catch (AppException e) {
            if (e.getErrorCode() == MapErrorCodes.LAYERUSENOTFOUND)
                throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
            throw e;
        }
        //******************************************************************
        //We fetch the map features from the record we want to set.
        MapReaderGraphic mapreader = new MapReaderGraphic();
        mapreader.setMapsLambda(auriga.getMapsLambda());
        MapRecordDraw recordg = mapreader.getRecord(record);
        MapObjectGraphic[] features = recordg.getMapObjects();
        //------------------------------------------------------------------
        //If there is no map object in the record. We throw an exception
        if (features.length == 0)
            throw new AppException("The record " + record.getName() + " has no map object", AppException.NOMAPOBJECTINRECORD);
        //******************************************************************
        //We clear the existent map objects the subset could have
        uatlas.clearMapObject(subset.getSubsetID());
        //------------------------------------------------------------------
        //We Add the objects to the subset.
        for (MapObjectGraphic feature : features)
            uatlas.addMapFeature(subset.getSubsetID(), feature.getPoints());
        uatlas.setMapStatus(subsetid, 1);
        //******************************************************************
    }    
    //**********************************************************************
}
//**************************************************************************
