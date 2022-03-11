package lycine.mapping;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import threonine.mapping.LayerUse;
import threonine.mapping.MapErrorCodes;
import threonine.mapping.MapLayer;
import threonine.mapping.MappingAttlas;
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
        MappingAttlas atlas = auriga.getMapsLambda();
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(mapLayer.projectID(), session, 2);
        //****************************************************************
        //We do a transaction.
        atlas.setAutoCommit(0);
        //****************************************************************
        //We Create the layer
        atlas.createLayer(mapLayer);
        //----------------------------------------------------------------
        //We create the use.
        LayerUse layeruse = new LayerUse();
        layeruse.setProjectId(mapLayer.projectID());
        layeruse.setLayerId(mapLayer.layerID());
        atlas.createLayerUse(layeruse);
        //****************************************************************
        //We are all done.
        atlas.commit();
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
        pauth.checkAccess(layer.projectID(), session, 1);
        //****************************************************************
        return layer;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param layerid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapLayer getLayer (long layerid) throws AppException, Exception {
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.usesrvFullNearSrv();
        //****************************************************************
        return atlas.getLayer(layerid);
        //****************************************************************
    }
    //********************************************************************
    /**
     * Returns a list of layers for a project.
     * @param projectid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
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
    /**
     * 
     * @param projectid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapLayer[] getUsedLayers (long projectid, Session session) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(projectid, session, 1);
        //****************************************************************
        //Returns the folders
        return auriga.getMapsLambda().getUsedLayers(projectid);
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param searchkey
     * @param session
     * @return
     * @throws Exception 
     */
    public MapLayer[] searchLayers (long projectid, String searchkey, Session session) throws Exception {
        //****************************************************************
        //Search key must not be empty. No exception
        if (searchkey == null) return new MapLayer[0];
        if (searchkey.length() == 0) return new MapLayer[0];
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(projectid, session, 1);
        //****************************************************************
        //We return it
        return auriga.getMapsLambda().searchLayers(projectid, searchkey);
        //****************************************************************
    }
    //********************************************************************
    public void setForPublish (long layerid, Session session) throws AppException, Exception {
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        //****************************************************************
        //We recover the layer where the record is going.
        MapLayer layer = atlas.getLayer(layerid);
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(layer.projectID(), session);
        //****************************************************************
        atlas.setForPub(layerid, 1);
        //****************************************************************
    }
    //********************************************************************
    /* ADMIN USE */
    //********************************************************************
    /**
     * 
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapLayer[] getForPublishLayers (Session session) throws AppException, Exception {
        if (!session.isAdmin())
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        return auriga.getMapsLambda().forPublishLayers();
    }
    //********************************************************************
}
//************************************************************************
