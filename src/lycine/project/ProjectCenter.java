package lycine.project;
//************************************************************************
import histidine.AurigaObject;
import java.util.Calendar;
import java.util.TimeZone;
import methionine.finance.UsageCost;
import methionine.finance.AlterUsage;
import methionine.finance.UsagePeriod;
import methionine.AppException;
import methionine.Celaeno;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.auth.User;
import methionine.project.Project;
import methionine.project.ProjectAccess;
//************************************************************************
public class ProjectCenter {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
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
        auriga.projectAtlas().setAutoCommit(0);
        auriga.projectAtlas().createProject(project);
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
        auriga.projectAtlas().commit();
        //------------------------------------------------
    }
    //********************************************************************
    public Project[] getWorkTeamsForUser (long userid) throws AppException, Exception {
        //============================================================
        Project[] ownedworkteams = auriga.projectAtlas().getWorkTeamByOwner(userid);
        int ownedcount = ownedworkteams.length;
        for (Project team : ownedworkteams) {
            team.setOwnerStatus();
            team.setAccessLevel(3);
        }
        //============================================================
        ProjectAccess[] accesslist = auriga.projectAtlas().getAccessList(0, userid);
        int accscount = accesslist.length;
        Project[] accecedteams = new Project[accscount];
        //============================================================
        User user;
        for (int n = 0; n < accscount; n++) {
            try {
                accecedteams[n] = auriga.projectAtlas().getProject(accesslist[n].projectID(), 0);
                user = auriga.getAuthLambda().getUser(accecedteams[n].getOwner(), false);
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
        Project project = auriga.projectAtlas().getProject(access.projectID(), userid);
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
        auriga.projectAtlas().AddLockUserAccess(tablist);
        auriga.projectAtlas().lockTables(tablist);
        auriga.projectAtlas().setAutoCommit(0);
        auriga.projectAtlas().createAccess(access, userid);
        auriga.getBillingLambda().alterUsage(alter);
        //---------------------------------------------
        //We are done.
        auriga.projectAtlas().commit();
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
        ProjectAccess access = auriga.projectAtlas().getAccess(projectid, userid);
        auriga.getBillingLambda().setAutoCommit(0);
        auriga.projectAtlas().revokeProjectAccess(projectid, userid, owner);
        Project project = auriga.projectAtlas().getProject(projectid, 0);
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
        ProjectAccess access = auriga.projectAtlas().getAccess(projectid, userid);
        auriga.projectAtlas().setAutoCommit(0);
        auriga.projectAtlas().leaveProject(projectid, userid);
        Project project = auriga.projectAtlas().getProject(projectid, 0);
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
        Project project = auriga.projectAtlas().getProject(projectid, 0);
        if (!user.isAdmin() && owner != project.getOwner())
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //================================================================
        ProjectAccess[] accesslist = auriga.projectAtlas().getAccessList(projectid, 0);
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
        Project project = auriga.projectAtlas().getProject(projectid, 0);
        if (project.getOwner() != userid) 
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //-----------------------------------------------------------
        //Lock All Tables
        TabList tabs = new TabList();
        auriga.projectAtlas().lockSetDestroyed(tabs);
        auriga.getBillingLambda().AddLockAlterUsage(tabs);
        auriga.projectAtlas().setAutoCommit(0);
        auriga.projectAtlas().lockTables(tabs);
        //-----------------------------------------------------------
        auriga.projectAtlas().setProjectDestroyed(projectid);
        auriga.getBillingLambda().endUsage(projectid);
        //-----------------------------------------------------------
        //We are all done
        auriga.projectAtlas().commit();
        //-----------------------------------------------------------
    }
    //********************************************************************
}
//************************************************************************
