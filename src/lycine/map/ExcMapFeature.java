package lycine.map;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import methionine.finance.FinanceRules;
import threonine.map.MapLayer;
import threonine.map.MapRecord;
import threonine.map.MappingAttlas;
import threonine.map.PointLocation;
import threonine.midlayer.MapValidationAndMath;
//************************************************************************
public class ExcMapFeature {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public void addFeature (long recordid, String pointstable, Session session) throws AppException, Exception {
        //****************************************************************
        MappingAttlas atlas = auriga.getMapsLambda();
        atlas.usesrvFullNearSrv();
        //****************************************************************
        MapRecord record = atlas.getMapRecord(recordid);
        MapLayer layer = atlas.getLayer(record.layerID());
        //****************************************************************
        if (layer.projectID() == 0)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //****************************************************************
        //We check the performing user has access to the project.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(layer.projectID(), session, 2);
        //****************************************************************
        //Create points and check validity
        PointLocation[] points = MapValidationAndMath.createPoints(pointstable);
        MapValidationAndMath.checkValid(points);
        //****************************************************************
        //We calculate the cost of the object.
        int billpts = points.length;
        float cost = 0;
        while (billpts > 0) {
            billpts -= 100;
            cost += FinanceRules.MAP100VERTICES;
        }
        //****************************************************************
        
        //****************************************************************
        atlas.createMapObject(recordid, points, cost);
        //****************************************************************
        /*
        for (PointLocation p : points)
            System.out.println(p.latitude + " " + p.longitude);
        */
        //****************************************************************
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
