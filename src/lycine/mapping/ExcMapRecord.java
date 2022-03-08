package lycine.mapping;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import java.util.ArrayList;
import java.util.List;
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
    /**
     * 
     * @param rawrecordids
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapRecord[] getMapRecord (String rawrecordids) throws AppException, Exception {
        //****************************************************************
        if (rawrecordids == null) return new MapRecord[0];
        if (rawrecordids.length() == 0) return new MapRecord[0];
        String[] txtrecordids = rawrecordids.split(",");  
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        //****************************************************************
        List<MapRecord> records = new ArrayList<>();
        long recordid;
        //================================================================
        for (String txtrecordid : txtrecordids) {
            try { 
                recordid = Long.parseLong(txtrecordid);
                records.add(atlas.getMapRecord(recordid)); 
            }
            catch (AppException e) { continue; }
        }
        //****************************************************************
        return records.toArray(new MapRecord[0]);        
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
    
    //********************************************************************
}
//************************************************************************
