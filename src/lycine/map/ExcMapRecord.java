package lycine.map;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.Session;
import threonine.map.MapErrorCodes;
import threonine.map.MapLayer;
import threonine.map.MapRecord;
import threonine.map.MappingAttlas;
//************************************************************************
public class ExcMapRecord {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    
    //********************************************************************
    /**
     * Creates a map record.
     * @param record
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createMapRecord (MapRecord record, Session session) throws AppException, Exception {
        //****************************************************************
        if (record.getName().length() == 0)
            throw new AppException("Record name cannot be empty", MapErrorCodes.MAPRECORDNAMEEMPTY);
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.usesrvFullNearSrv();
        //****************************************************************
        //We recover the layer where the record is going.
        MapLayer layer = atlas.getLayer(record.layerID());
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(layer.projectID(), session, 2);
        //****************************************************************
        //We create the record.
        atlas.createMapRecord(record);
        //****************************************************************
    }
    //********************************************************************
    public MapRecord[] getRecords (MapLayer layer) throws AppException, Exception { 
        return auriga.getMapsLambda().getMapRecords(layer.layerID()); 
    }
    //====================================================================
    public MapRecord[] getRecords (long layerid, Session session) throws AppException, Exception {
        MapLayer layer = auriga.getMapsLambda().getLayer(layerid);
        return getRecords(layer, session);
    }
    //====================================================================
    public MapRecord[] getRecords (MapLayer layer, Session session) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(layer.projectID(), session, 1);
        //****************************************************************
        //Returns the folders
        return auriga.getMapsLambda().getMapRecords(layer.layerID());
        //****************************************************************
    }    
    //********************************************************************
    
    //********************************************************************
}
//************************************************************************
