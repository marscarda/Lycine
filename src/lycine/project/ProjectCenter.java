package lycine.project;
//************************************************************************
import histidine.AurigaObject;
import java.util.Calendar;
import java.util.TimeZone;
import methionine.billing.UsageCost;
import methionine.billing.AlterUsage;
import methionine.billing.BillingLambda;
import methionine.billing.UsagePeriod;
import methionine.AppException;
import methionine.Celaeno;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import methionine.project.Project;
import methionine.project.ProjectAccess;
import methionine.project.ProjectLambda;
//************************************************************************
public class ProjectCenter {
    //********************************************************************
    AurigaObject auriga = null;
    
    /*
    
    Use the auriga
    
    */
    
    @Deprecated
    AuthLamda authlambda = null;
    @Deprecated
    ProjectLambda projectlambda = null;
    @Deprecated
    BillingLambda billinglambda = null;
    //PublicViewLambda publicviewlambda = null;
    //====================================================================
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    
    /*
    All this deprecation... Use The Auriga.
    */
    
    @Deprecated
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    @Deprecated
    public void setWorkTeamLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    @Deprecated
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
//    public void setPublicViewLambda (PublicViewLambda publicviewlambda) { this.publicviewlambda = publicviewlambda; }
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
        auriga.getProjectLambda().setAutoCommit(0);
        auriga.getProjectLambda().createProject(project);
        //------------------------------------------------
        UsagePeriod period = new UsagePeriod();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        period.setUserID(project.getOwner());
        period.setProjectID(project.projectID());
        period.setDateStart(Celaeno.getDateString(calendar, true));
        
        period.setCostPerDay(UsageCost.PROJECT);
        
        period.setProjectName(project.getName());
        period.setStartingEvent("Project created");
        //------------------------------------------------
        auriga.getBillingLambda().startUsage(period);
        //------------------------------------------------
        auriga.getProjectLambda().commit();
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
        long grantuserid = auriga.getAuthLambda().getUserIdByIdentifier(access.getUserName());
        //---------------------------------------------
        //The access itself
        access.setDayCost(UsageCost.PROJECTUSER);
        Project project = auriga.getProjectLambda().getProject(access.projectID(), userid);
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
        auriga.getBillingLambda().AddLockAlterUsage(tablist);
        auriga.getProjectLambda().AddLockUserAccess(tablist);
        auriga.getProjectLambda().lockTables(tablist);
        auriga.getProjectLambda().setAutoCommit(0);
        auriga.getProjectLambda().createAccess(access, userid);
        auriga.getBillingLambda().alterUsage(alter);
        //---------------------------------------------
        //We are done.
        auriga.getProjectLambda().commit();
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
        ProjectAccess access = auriga.getProjectLambda().getAccess(projectid, userid);
        auriga.getBillingLambda().setAutoCommit(0);
        auriga.getProjectLambda().revokeProjectAccess(projectid, userid, owner);
        Project project = auriga.getProjectLambda().getProject(projectid, 0);
        AlterUsage alter = new AlterUsage();
        alter.setDecrease(access.dayCost());
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Removed From Project");
        auriga.getBillingLambda().alterUsage(alter);
        auriga.getBillingLambda().commit();
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
        ProjectAccess access = auriga.getProjectLambda().getAccess(projectid, userid);
        auriga.getProjectLambda().setAutoCommit(0);
        auriga.getProjectLambda().leaveProject(projectid, userid);
        Project project = auriga.getProjectLambda().getProject(projectid, 0);
        AlterUsage alter = new AlterUsage();
        alter.setDecrease(access.dayCost());
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Left Project");
        auriga.getBillingLambda().alterUsage(alter);
        auriga.getBillingLambda().commit();
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
        User user = auriga.getAuthLambda().getUser(owner, false);
        Project project = auriga.getProjectLambda().getProject(projectid, 0);
        if (!user.isAdmin() && owner != project.getOwner())
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //================================================================
        ProjectAccess[] accesslist = auriga.getProjectLambda().getAccessList(projectid, 0);
        for (ProjectAccess access : accesslist) {
            try {
                user = auriga.getAuthLambda().getUser(access.userID(), false);
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
     * Marks a project as destroyed and ends the usage.
     * @param projectid
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void setDestroyed (long projectid, long userid) throws AppException, Exception {
        //-----------------------------------------------------------
        Project project = auriga.getProjectLambda().getProject(projectid, 0);
        if (project.getOwner() != userid) 
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //-----------------------------------------------------------
        //Lock All Tables
        TabList tabs = new TabList();
        auriga.getProjectLambda().lockSetDestroyed(tabs);
        auriga.getBillingLambda().AddLockAlterUsage(tabs);
        auriga.getProjectLambda().setAutoCommit(0);
        auriga.getProjectLambda().lockTables(tabs);
        //-----------------------------------------------------------
        auriga.getProjectLambda().setProjectDestroyed(projectid);
        auriga.getBillingLambda().endUsage(projectid);
        //-----------------------------------------------------------
        //We are all done
        auriga.getProjectLambda().commit();
        //-----------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param userid 
     * @throws AppException PROJECTNOTFOUND UNAUTHORIZED
     */
    @Deprecated
    public void destroyProject (long projectid, long userid) throws AppException, Exception {
        //-----------------------------------------------------------
        Project project = auriga.getProjectLambda().getProject(projectid, 0);
        if (project.getOwner() != userid) 
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //-----------------------------------------------------------
        projectlambda.startTransaction();
        try {
            projectlambda.startTransaction();
            //-----------------------------------------------
            //publicviewlambda.destroyCandidate(0, projectid);
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
