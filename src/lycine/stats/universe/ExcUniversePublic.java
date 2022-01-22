package lycine.stats.universe;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import threonine.universe.SubSet;
import threonine.universe.Universe;
//**************************************************************************
public class ExcUniversePublic {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //**********************************************************************
    /**
     * 
     * @param search
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe[] getUniverses (String search) throws AppException, Exception {
        return auriga.getUniverseAtlas().getPublicUniverseList(search);
    }
    //**********************************************************************
    /**
     * 
     * @param universeid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe getUniverse (long universeid) throws AppException, Exception {
        return auriga.getUniverseAtlas().getUniverse(universeid);
    }
    //**********************************************************************
    /**
     * 
     * @param universeid
     * @param subsetid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubSet getSubset (long universeid, long subsetid) throws AppException, Exception {
        //------------------------------------------------------------------
        //We check the user has access to the project.
//        if (userid != 0) {
//            Universe universe = universelambda.getUniverse(universeid);
//            projectlambda.checkAccess(universe.projectID(), userid, 1);
//        }
        //------------------------------------------------------------------
        SubSet subset;
        if (subsetid == 0) {
            subset = new SubSet();
            subset.setUniverseID(universeid);
            subset.setValid();
            subset.setROOT();
        }
        else subset = auriga.getUniverseAtlas().getSubset(universeid, subsetid);
        return subset;
        //------------------------------------------------------------------
    }
    //**********************************************************************    
    
}
//**************************************************************************
