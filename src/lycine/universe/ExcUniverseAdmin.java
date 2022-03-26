package lycine.universe;
//**************************************************************************
import histidine.AurigaObject;
import histidine.universe.UniverseToTemplate;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
//**************************************************************************
public class ExcUniverseAdmin {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //======================================================================
    //**********************************************************************
    public void createTemplateStart (long universeid, String name, String description, Session session) throws AppException, Exception {
        //****************************************************************
        //We check privileges to do this.
        boolean allow = false;
        if (session.isAdmin()) allow = true;
        //----------------------------------------------------------------
        if (!allow)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //****************************************************************
        UniverseAtlas uatlas = auriga.getUniverseAtlas();
        Universe universe = uatlas.getUniverse(universeid);
        //****************************************************************
        if (name != null)
            if (name.length() != 0)
                universe.setName(name);
        if (description != null)
            if (description.length() != 0)
                universe.setDescription(description);
        //****************************************************************
        UniverseToTemplate totemplate = new UniverseToTemplate();
        totemplate.setUniverse(universe);
        totemplate.start();
        //****************************************************************
    }
    //**********************************************************************
}
//**************************************************************************
