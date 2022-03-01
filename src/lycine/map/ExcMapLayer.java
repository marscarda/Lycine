package lycine.map;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.Session;
import threonine.map.MapErrorCodes;
import threonine.map.MapLayer;
import threonine.map.MappingAttlas;
//************************************************************************
public class ExcMapLayer {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    /**
     * Creates The layer.
     * @param mapLayer
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createMapLayer (MapLayer mapLayer, Session session) throws AppException, Exception {
        //****************************************************************
        if (mapLayer.layerName().length() == 0)
            throw new AppException("Map Layer name is empty", MapErrorCodes.MAPLAYEREMPTY);
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(mapLayer.projectID(), session, 2);
        //****************************************************************
        //We Create the layer
        auriga.getMapsLambda().createLayer(mapLayer);
        //****************************************************************
    }
    //********************************************************************
    /**
     * Returns a layer by ID.
     * @param layerid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapLayer getLayer (long layerid, Session session) throws AppException, Exception {
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.usesrvFullNearSrv();
        //****************************************************************
        //We recover the layer where the record is going.
        MapLayer layer = atlas.getLayer(layerid);
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(layer.projectID(), session, 2);
        //****************************************************************
        return layer;
        //****************************************************************
    }
    //********************************************************************
    public MapLayer[] getLayersByProject (long projectid, Session session) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(projectid, session, 1);
        //****************************************************************
        //Returns the folders
        return auriga.getMapsLambda().getLayersByProject(projectid);
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
