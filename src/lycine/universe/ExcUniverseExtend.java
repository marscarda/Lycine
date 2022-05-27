package lycine.universe;
//**************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.auth.Session;
import threonine.universe.SubSet;
import threonine.universe.Universe;
//**************************************************************************
public class ExcUniverseExtend extends ExcUniverse {
    //**********************************************************************
    boolean loadsamples = false;
    public void loadSamples () { loadsamples = true; }
    //**********************************************************************
    /**
     * Returns an array of subset given a parent subset
     * @param universeid
     * @param parentid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubsetLoad[] getSubsetLoads (long universeid, long parentid, Session session) throws AppException, Exception {
        Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
        return getSubsetLoads(universe, parentid, session);
    }    
    //======================================================================
    /**
     * 
     * @param universe
     * @param parentid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubsetLoad[] getSubsetLoads (Universe universe, long parentid, Session session) throws AppException, Exception {
        //==================================================================
        //We check the user has write acces to the project where the universe belongs
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 1);
        //==================================================================
        SubSet[] subsets = auriga.getUniverseAtlas().getSubsets(universe.universeID(), parentid);
        int count = subsets.length;
        //==================================================================
        mapstatus = false;
        for (SubSet s : subsets)
            if (s.mapStatus()) { mapstatus = true; break; }
        //==================================================================
        SubsetLoad[] loads = new SubsetLoad[count];
        for (int n = 0; n < count; n++) {
            loads[n] = new SubsetLoad();
            loads[n].subset = subsets[n];
        }
        //==================================================================
        return loads;
        //==================================================================
    }
    //**********************************************************************
}
//**************************************************************************