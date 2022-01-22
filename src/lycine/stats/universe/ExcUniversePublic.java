package lycine.stats.universe;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import methionine.auth.User;
import methionine.project.Project;
import methionine.project.ProjectErrorCodes;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseErrorCodes;
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
        Universe[] universes = auriga.getUniverseAtlas().getPublicUniverseList(search);
        Project project;
        User user;
        for (Universe universe : universes) {
            try { 
                project = auriga.projectAtlas().getProject(universe.projectID()); 
                user = auriga.getAuthLambda().getUser(project.getOwner());
                universe.setUserID(user.userID());
                universe.setUserName(user.loginName());
            }
            catch (AppException e) {
                switch (e.getErrorCode()) {
                    case AuthErrorCodes.USERNOTFOUND:
                    case ProjectErrorCodes.PROJECTNOTFOUND:
                        continue;
                    default: throw e;
                }
            }
        }
        return universes;
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
        //=================================================================
        //We get the universe and check it is public.
        Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
        if (!universe.isPublic())
            throw new AppException("Universe not public", UniverseErrorCodes.UNIVERSENOTPUBLIC);
        //=================================================================
        //We do what we have to to set the user name to the universe.
        Project project = auriga.projectAtlas().getProject(universe.projectID());
        User user = auriga.getAuthLambda().getUser(project.getOwner());
        universe.setUserID(user.userID());
        universe.setUserName(user.loginName());
        //=================================================================
        //We send back the universe.
        return universe;
        //=================================================================
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
        //==================================================================
        //We get the universe and check it is public.
        Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
        if (!universe.isPublic())
            throw new AppException("Universe not public", UniverseErrorCodes.UNIVERSENOTPUBLIC);
        //==================================================================
        SubSet subset;
        if (subsetid == 0) {
            subset = new SubSet();
            subset.setUniverseID(universeid);
            subset.setValid();
            subset.setROOT();
        }
        else subset = auriga.getUniverseAtlas().getSubset(universeid, subsetid);
        return subset;
        //==================================================================
    }
    //**********************************************************************
    public SubSet[] getSubsets (long universeid, long parentid) throws AppException, Exception {
        //==================================================================
        //We get the universe and check it is public.
        Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
        if (!universe.isPublic())
            throw new AppException("Universe not public", UniverseErrorCodes.UNIVERSENOTPUBLIC);
        //==================================================================
        SubSet[] subsets = auriga.getUniverseAtlas().getSubsets(universeid, parentid);
        return subsets;
        //------------------------------------------------------------------
    }
    //**********************************************************************
    public void aquireUniverse (long universeid, Session session) throws AppException, Exception {
        
        
        
        
        
        
        
        
    }
    //**********************************************************************
}
//**************************************************************************
