package lycine.project;
//************************************************************************
import java.util.Calendar;
import java.util.TimeZone;
import methionine.billing.UsageCost;
import methionine.billing.AlterUsage;
import methionine.billing.BillingLambda;
import methionine.billing.UsagePeriod;
import methionine.AppException;
import methionine.Celaeno;
import methionine.TabList;
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
        //------------------------------------------------
        project.setDayCost(UsageCost.PROJECT);
        //------------------------------------------------
        projectlambda.setAutoCommit(0);
        projectlambda.createProject(project);
        //------------------------------------------------
        UsagePeriod period = new UsagePeriod();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        period.setUserID(project.getOwner());
        period.setProjectID(project.workTeamID());
        period.setDateStart(Celaeno.getDateString(calendar, true));
        period.setCostPerDay(UsageCost.PROJECT);
        period.setProjectName(project.getName());
        period.setStartingEvent("Project created");
        //------------------------------------------------
        billinglambda.startUsage(period);
        //------------------------------------------------
        projectlambda.commit();
        //------------------------------------------------
    }
    //********************************************************************
    public Project[] getWorkTeamsForUser (long userid) throws AppException, Exception {
        //============================================================
        Project[] ownedworkteams = projectlambda.getWorkTeamByOwner(userid);
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
                accecedteams[n] = projectlambda.getProject(accesslist[n].projectID(), 0);
                user = authlambda.getUser(accecedteams[n].getOwner(), false);
                accecedteams[n].setOwnerName(user.loginName());
                accecedteams[n].setAccessLevel(accesslist[n].accessLevel());
            }
            catch (AppException e) { 
                accecedteams[n] = new Project();
            }
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
     * @param access
     * @param userid //The user who is trying to perform this. Not the user to grant
     * @throws AppException WORKTEAMNOTFOUND UNAUTHORIZED USERNOTFOUND
     * @throws Exception 
     */
    public void createProjectAccess (ProjectAccess access, long userid) throws AppException, Exception {
        //=============================================
        //The user authorization to perform this is made 
        //in projectlambda.createAccess(..)
        //=============================================
        //We recover the user we want to grant access.
        long grantuserid = authlambda.getUserIdByIdentifier(access.getUserName());
        //---------------------------------------------
        //The access itself
        access.setDayCost(UsageCost.PROJECTUSER);
        Project project = projectlambda.getProject(access.projectID(), userid);
        access.setUserID(grantuserid);
        //---------------------------------------------
        //Create the access. And alter the billing cost
        //of the project
        //---------------------------------------------
        //We create the usage alteration before lockin tables. Performance.
        AlterUsage alter = new AlterUsage();
        alter.setIncrease(UsageCost.PROJECTUSER);
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Added to project");
        //---------------------------------------------
        //We lock the tables
        TabList tablist = new TabList();
        billinglambda.AddLockAlterUsage(tablist);
        projectlambda.AddLockUserAccess(tablist);
        projectlambda.lockTables(tablist);
        projectlambda.setAutoCommit(0);
        projectlambda.createAccess(access, userid);
        billinglambda.alterUsage(alter);
        //---------------------------------------------
        //We are done.
        projectlambda.commit();
        //---------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param userid
     * @param owner
     * @throws AppException
     * @throws Exception 
     */
    public void revokeProjectAccess (long projectid, long userid, long owner) throws AppException, Exception {
        ProjectAccess access = projectlambda.getAccess(projectid, userid);
        billinglambda.startTransaction();
        projectlambda.revokeProjectAccess(projectid, userid, owner);
        Project project = projectlambda.getProject(projectid, 0);
        AlterUsage alter = new AlterUsage();
        alter.setDecrease(access.dayCost());
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Removed From Project");
        billinglambda.alterUsage(alter);
        billinglambda.commitTransaction();
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void leaveProject (long projectid, long userid) throws AppException, Exception {
        ProjectAccess access = projectlambda.getAccess(projectid, userid);
        billinglambda.startTransaction();
        projectlambda.leaveProject(projectid, userid);
        Project project = projectlambda.getProject(projectid, 0);
        AlterUsage alter = new AlterUsage();
        alter.setDecrease(access.dayCost());
        alter.setProjectId(project.workTeamID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Left Project");
        billinglambda.alterUsage(alter);
        billinglambda.commitTransaction();
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
        Project project = projectlambda.getProject(projectid, 0);
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
        Project project = projectlambda.getProject(projectid, 0);
        if (project.getOwner() != userid) 
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //-----------------------------------------------------------
        projectlambda.startTransaction();
        try {
            projectlambda.startTransaction();
            //-----------------------------------------------
            publicviewlambda.destroyCandidate(0, projectid);
            //-----------------------------------------------
            projectlambda.deleteProject(projectid);
            projectlambda.deleteAccessesForProject(projectid); ;
            
            //billinglambda.endBillingPeriod(BillingPeriod.PROJECT, project.workTeamID());
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
