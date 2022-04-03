package lycine.universe;
//**************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import histidine.universe.TemplateToUniverse;
import methionine.AppException;
import methionine.auth.Session;
import threonine.universe.SubSet;
import threonine.universe.Universe;
//**************************************************************************
public class ExcUniverseTemplate {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //======================================================================
    //When get children subsets. We set the map status of current subset.
    boolean mapstatus = false;
    public boolean mapStatus () { return mapstatus; }
    //**********************************************************************
    /**
     * Returns a universe given its ID
     * @param universeid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe getTemplate (long universeid) throws AppException, Exception {
        //==================================================================
        Universe universe = auriga.templateUniverseAtlas().getTemplate(universeid);
        return universe;
        //==================================================================
    }    
    //**********************************************************************
    /**
     * Returns a Subset by ID. if we already have the universe this is the method.
     * Checks user authorization to fetch.
     * @param universe
     * @param subsetid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubSet getSubset (Universe universe, long subsetid) throws AppException, Exception {
        SubSet subset;
        if (subsetid == 0) {
            subset = new SubSet();
            subset.setUniverseID(universe.universeID());
            subset.setValid();
            subset.setROOT();
        }
        else subset = auriga.templateUniverseAtlas().getSubset(universe.universeID(), subsetid);
        return subset;
        //------------------------------------------------------------------
    }    
    //**********************************************************************
    /**
     * 
     * @param universe
     * @param parentid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubSet[] getSubsets (Universe universe, long parentid) throws AppException, Exception {
        SubSet[] subsets = auriga.templateUniverseAtlas().getSubsets(universe.universeID(), parentid);
        //==================================================================
        mapstatus = false;
        for (SubSet s : subsets)
            if (s.mapStatus()) { mapstatus = true; break; }
        //==================================================================
        return subsets;
        //==================================================================
    }    
    //**********************************************************************
    //ACQUIRE AS UNIVERSE.
    //**********************************************************************
    public void aquireUniverse (long templateid, Session session) throws AppException, Exception {
        //******************************************************************
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(session.getCurrentProject(), session, 2);
        //******************************************************************
        TemplateToUniverse touniverse = new TemplateToUniverse();
        touniverse.setTemplateId(templateid);
        touniverse.setProjectId(session.getCurrentProject());
        touniverse.start();
        //******************************************************************
    }
    //**********************************************************************
}
//**************************************************************************
