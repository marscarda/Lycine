package lycine.mapping;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import threonine.mapping.MapLayer;
import threonine.mapping.MappingAttlas;
//************************************************************************
public class ExcMapLayerAdmin {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    /**
     * 
     * @param layerid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public MapLayer getLayer (long layerid, Session session) throws AppException, Exception {
        //****************************************************************
        //We check privileges to do this.
        boolean allow = false;
        if (session.isAdmin()) allow = true;
        //----------------------------------------------------------------
        if (!allow)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.usesrvFullNearSrv();
        //****************************************************************
        return atlas.getLayer(layerid);
        //****************************************************************
    }
    //********************************************************************
    /**
     * Sets a layer public
     * @param layerid
     * @param name
     * @param description
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void setLayerPublic (long layerid, String name, String description, Session session) throws AppException, Exception {
        //****************************************************************
        //We check privileges to do this.
        boolean allow = false;
        if (session.isAdmin()) allow = true;
        //----------------------------------------------------------------
        if (!allow)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.usesrvFullMainSrv();
        //****************************************************************
        atlas.setPublic(layerid, name, description);
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
