package lycine.map;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.Session;
import threonine.map.LayerUse;
import threonine.map.MapLayer;
import threonine.map.MappingAttlas;
//************************************************************************
public class ExcMapLayerUse {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public MapLayer createUse (LayerUse use, Session session) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(use.projectID(), session, 2);
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.createLayerUse(use);
        //****************************************************************
        return atlas.getLayer(use.layerID());
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
