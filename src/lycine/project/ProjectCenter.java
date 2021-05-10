package lycine.project;
//************************************************************************
import java.util.Calendar;
import java.util.TimeZone;
import methinine.billing.BillingLambda;
import methinine.billing.Expenditure;
import methinine.billing.TimeBill;
import methionine.AppException;
import methionine.Celaeno;
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
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    PublicViewLambda publicviewlambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setWorkTeamLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setPublicViewLambda (PublicViewLambda publicviewlambda) { this.publicviewlambda = publicviewlambda; }
    //********************************************************************
    /**
     * 
     * @param project
     * @throws AppException
     * @throws Exception 
     */
    public void createProject (Project project) throws AppException, Exception {
        Expenditure expnd = new Expenditure();
        expnd.setDescription("Prject: " + project.getName() + " created");
        expnd.setSize(Expenditure.PROJECTCREATE);
        expnd.setUserID(project.getOwner());
        projectlambda.startTransaction();
        billinglambda.addExpenditure(expnd, true);
        projectlambda.createProject(project);
        //------------------------------------------------
        TimeBill timebill = new TimeBill();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        timebill.setDateStart(Celaeno.getDateString(calendar, true));
        timebill.setItemType(TimeBill.PROJECT);
        timebill.setItemID(project.workTeamID());
        timebill.setSize(1);
        timebill.setUserID(project.getOwner());
        billinglambda.addTimeBill(timebill);
        //------------------------------------------------
        projectlambda.commitTransaction();
    }
    //********************************************************************
    public Project[] getWorkTeamsForUser (long userid) throws AppException, Exception {
        //============================================================
        Project[] ownedworkteams = projectlambda.getWorkTeamByOwner(userid, 0);
        int ownedcount = ownedworkteams.length;
        for (Project team : ownedworkteams) {
            team.setOwnerStatus();
            team.setAccessLevel(3);
        }
        //============================================================
        ProjectAccess[] accesslist = projectlambda.getAccessList(0, userid);
        int accscount = accesslist.length;
        Project[] accecedteams = new Project[accscount];
        //============================================================
        User user;
        for (int n = 0; n < accscount; n++) {
            try {
                accecedteams[n] = projectlambda.getWorkTeam(accesslist[n].workTeamID(), 0);
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
        projectlambda.createAccess(workteamaccess, behalfusrid);
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
        Project project = projectlambda.getWorkTeam(projectid, 0);
        if (!user.isAdmin() && owner != project.getOwner())
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //================================================================
        ProjectAccess[] accesslist = projectlambda.getAccessList(projectid, 0);
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
        Project project = projectlambda.getWorkTeam(projectid, 0);
        if (project.getOwner() != userid) 
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //-----------------------------------------------------------
        projectlambda.startTransaction();
        try {
            //-----------------------------------------------
            publicviewlambda.destroyCandidate(0, projectid);
            //-----------------------------------------------
            projectlambda.deleteProject(projectid);
            projectlambda.deleteAccessesForProject(projectid); ;
            //-----------------------------------------------
        }
        catch (Exception e) {
            projectlambda.rollbackTransaction();
            throw e;
        }
        projectlambda.commitTransaction();
    }
    //********************************************************************
}
//************************************************************************
