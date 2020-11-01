package lycine.project;
//************************************************************************
import methionine.AppException;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import methionine.project.Project;
import methionine.project.ProjectAccess;
import methionine.project.ProjectLambda;
import tryptophan.survey.publicview.PublicViewLambda;
//************************************************************************
public class ProjectCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda workteamlambda = null;
    PublicViewLambda publicviewlambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setWorkTeamLambda (ProjectLambda workteamlambda) { this.workteamlambda = workteamlambda; }
    public void setPublicViewLambda (PublicViewLambda publicviewlambda) { this.publicviewlambda = publicviewlambda; }
    //********************************************************************
    public Project[] getWorkTeamsForUser (long userid) throws AppException, Exception {
        //============================================================
        Project[] ownedworkteams = workteamlambda.getWorkTeamByOwner(userid, 0);
        int ownedcount = ownedworkteams.length;
        for (Project team : ownedworkteams) {
            team.setOwnerStatus();
            team.setAccessLevel(3);
        }
        //============================================================
        ProjectAccess[] accesslist = workteamlambda.getAccessList(0, userid);
        int accscount = accesslist.length;
        Project[] accecedteams = new Project[accscount];
        //============================================================
        User user;
        for (int n = 0; n < accscount; n++) {
            try {
                accecedteams[n] = workteamlambda.getWorkTeam(accesslist[n].workTeamID(), 0);
                user = authlambda.getUser(accecedteams[n].getOwner(), false);
                accecedteams[n].setOwnerName(user.loginName());
                accecedteams[n].setAccessLevel(accesslist[n].accessLevel());
            }
            catch (AppException e) { }
        }
        //============================================================
        int totcount = accscount + ownedcount;
        Project[] finalteams = new Project[totcount];
        System.arraycopy(ownedworkteams, 0, finalteams, 0, ownedcount);
        System.arraycopy(accecedteams, 0, finalteams, ownedcount, accscount);
        //============================================================
        return finalteams;
        //============================================================
    }
    //********************************************************************
    /**
     * 
     * @param workteamaccess
     * @param behalfusrid
     * @throws AppException WORKTEAMNOTFOUND UNAUTHORIZED USERNOTFOUND
     * @throws Exception 
     */
    public void createWorkteamAccess (ProjectAccess workteamaccess, long behalfusrid) throws AppException, Exception {
        long userid = authlambda.getUserIdByIdentifier(workteamaccess.getUserName());
        workteamaccess.setUserID(userid);
        workteamlambda.createAccess(workteamaccess, behalfusrid);
    }
    //********************************************************************
    /**
     * Returns an array of Project Access given a project ID
     * @param projectid
     * @param owner
     * @return 
     * @throws AppException UNAUTHORIZED WORKTEAMNOTFOUND
     * @throws Exception 
     */
    public ProjectAccess[] getAccessList (long projectid, long owner) throws AppException, Exception {
        //================================================================
        User user = authlambda.getUser(owner, false);
        Project project = workteamlambda.getWorkTeam(projectid, 0);
        if (!user.isAdmin() && owner != project.getOwner())
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //================================================================
        ProjectAccess[] accesslist = workteamlambda.getAccessList(projectid, 0);
        for (ProjectAccess access : accesslist) {
            try {
                user = authlambda.getUser(access.userID(), false);
                access.setUserName(user.loginName());
            }
            catch (Exception e) {}
        }
        //================================================================
        return accesslist;
        //================================================================
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param userid 
     * @throws AppException PROJECTNOTFOUND UNAUTHORIZED
     */
    public void destroyProject (long projectid, long userid) throws AppException, Exception {
        //-----------------------------------------------------------
        Project project = workteamlambda.getWorkTeam(projectid, 0);
        if (project.getOwner() != userid) 
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //-----------------------------------------------------------
        workteamlambda.startTransaction();
        try {
            //-----------------------------------------------
            publicviewlambda.destroyCandidate(0, projectid);
            //-----------------------------------------------
            workteamlambda.deleteProject(projectid);
            workteamlambda.deleteAccessesForProject(projectid); ;
            //-----------------------------------------------
        }
        catch (Exception e) {
            workteamlambda.rollbackTransaction();
            throw e;
        }
        workteamlambda.commitTransaction();
    }
    //********************************************************************
}
//************************************************************************
