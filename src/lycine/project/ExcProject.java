package lycine.project;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.auth.AuthLamda;
import methionine.auth.Session;
import methionine.auth.User;
import methionine.finance.AlterUsage;
import methionine.finance.BalanceInfo;
import methionine.finance.FinanceAtlas;
import methionine.finance.FinanceRules;
import methionine.finance.UsagePeriod;
import methionine.project.Project;
import methionine.project.ProjectAccess;
import methionine.project.ProjectErrorCodes;
import methionine.project.ProjectLambda;
//************************************************************************
public class ExcProject {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    /**
     * Creates a project for a user.
     * @param project
     * @throws AppException
     * @throws Exception 
     */
    public void createProject (Project project) throws AppException, Exception {
        //*****************************************************
        if (project.getName().length() == 0) 
            throw new AppException("Project Name cannot be empty", ProjectErrorCodes.EMPTYPROJECTNAME);
        //-----------------------------------------------------
        if (project.getName().length() > 40)
            throw new AppException("Project Name too long", ProjectErrorCodes.TOOLONGPROJECTNAME);
        //*****************************************************
        //We check the user has confirmed their email
        User user = auriga.getAuthLambda().getUser(project.getOwner());
        if (!user.emailConfirmed()) 
            throw new AppException("Email address not confirmed", AuthErrorCodes.EMAILNOTCONFIRMED);
        //*****************************************************
        //We check the user is able to spend.
        BalanceInfo balance = auriga.getBillingLambda().getTotalBalance(project.getOwner());
        FinanceRules.spendOk(balance.getTotalBalance());
        //*****************************************************
        project.setDayCost(FinanceRules.PROJECT);
        ProjectLambda projatlas = auriga.projectAtlas();
        FinanceAtlas fincatlas = auriga.getBillingLambda();
        AuthLamda authatlas = auriga.getAuthLambda();
        authatlas.useMaster();
        //*****************************************************
        TabList tablist = new TabList();
        projatlas.setAutoCommit(0);
        authatlas.lockUsers(tablist);
        projatlas.lockProjects(tablist);
        fincatlas.lockUsagePeriods(tablist);
        projatlas.lockTables(tablist);
        //*****************************************************
        authatlas.checkUserExists(project.getOwner());
        auriga.projectAtlas().createProject(project);
        //------------------------------------------------
        UsagePeriod period = new UsagePeriod();
        period.setUserID(project.getOwner());
        period.setProjectID(project.projectID());
        period.setCostPerDay(FinanceRules.PROJECT);
        period.setProjectName(project.getName());
        period.setStartingEvent("Project created");
        //------------------------------------------------
        auriga.getBillingLambda().startUsage(period);
        //*****************************************************
        //We are all done.
        auriga.projectAtlas().commit();
        //*****************************************************
        //We assume the creator is the owner :)
        project.setOwnerStatus();
        //*****************************************************
    }
    //********************************************************************
    public Project getProject (long projectid, Session session) throws AppException, Exception {
        //****************************************************************
        ProjectLambda atlas = auriga.projectAtlas();
        Project project = atlas.getProject(projectid);
        //****************************************************************
        
        //SECURITY CHECKS
        
        //****************************************************************
        return project;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param access
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createProjectAccess (ProjectAccess access, Session session) throws AppException, Exception {
        //****************************************************************
        //The owner of the project can perform this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(access.projectID(), session);
        //****************************************************************
        //We get the Atlas
        ProjectLambda patlas = auriga.projectAtlas();
        FinanceAtlas fatlas = auriga.getBillingLambda();
        AuthLamda aatlas = auriga.getAuthLambda();
        //****************************************************************
        //We use the main server
        patlas.usesrvFullMainSrv();
        fatlas.usesrvFullMainSrv();
        aatlas.usesrvFullMainSrv();
        //****************************************************************
        //We lock all the tables we will use.
        TabList tablist = new TabList();
        patlas.lockUserAccess(tablist);
        patlas.lockProjects(tablist);
        aatlas.lockUsers(tablist);
        fatlas.lockAlterUsage(tablist);
        patlas.lockTables(tablist);
        patlas.setAutoCommit(0);
        //****************************************************************
        //We start doing
        //================================================================
        //We get the project and the user id we want to add.
        Project project = patlas.getProject(access.projectID());
        long grantuserid = auriga.getAuthLambda().getUserId(access.getUserName());
        //================================================================
        //We check the user isn't the owner
        if (project.getOwner() == grantuserid)
            throw new AppException("The user is already a member", ProjectErrorCodes.USERALREADYMEMBER);
        //================================================================
        //We create the access.
        access.setDayCost(FinanceRules.PROJECTUSER);
        access.setUserID(grantuserid);
        patlas.createAccess(access);
        //================================================================
        //We create the usage alteration before lockin tables. Performance.
        AlterUsage alter = new AlterUsage();
        alter.setIncrease(FinanceRules.PROJECTUSER);
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Added to project");
        fatlas.alterUsage(alter);
        //****************************************************************
        //We are all done
        auriga.projectAtlas().commit();
        //****************************************************************
    }
    //********************************************************************
    /**
     * Revokes an access to a project.
     * @param projectid
     * @param userid
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void revokeAccess (long projectid, long userid, Session session) throws AppException, Exception {
        //****************************************************************
        //The owner of the project can perform this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(projectid, session);
        //****************************************************************
        //We get the Atlas
        ProjectLambda patlas = auriga.projectAtlas();
        FinanceAtlas fatlas = auriga.getBillingLambda();
        //We use the main server
        patlas.usesrvFullMainSrv();
        fatlas.usesrvFullMainSrv();
        //****************************************************************
        //We lock all the tables we will use.
        TabList tablist = new TabList();
        patlas.lockUserAccess(tablist);
        patlas.lockProjects(tablist);
        fatlas.lockAlterUsage(tablist);
        patlas.lockTables(tablist);
        patlas.setAutoCommit(0);
        //****************************************************************
        //We get the project and the access we want to revoke.
        Project project = patlas.getProject(projectid);
        ProjectAccess access = patlas.getAccess(projectid, userid);
        //================================================================
        patlas.revokeProjectAccess(projectid, userid);
        //****************************************************************
        //We create the usage alteration. Performance.
        AlterUsage alter = new AlterUsage();
        alter.setDecrease(access.dayCost());
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setStartingEvent("User Removed From Project");
        fatlas.alterUsage(alter);        
        //****************************************************************
        //We are all done
        patlas.commit();
        //****************************************************************
    }
    //********************************************************************
    /**
     * Creates an access for a project. Only the owner is allowed to do this.
     * @param projectid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public ProjectAccess[] getAccessList (long projectid, Session session) throws AppException, Exception {
        //================================================================
        //The owner of the project or an admin can perform this.
        ProjectLambda atlas = auriga.projectAtlas();
        Project project = atlas.getProject(projectid);
        boolean allowed = false;
        if (session.isAdmin()) allowed = true;
        if (project.getOwner() == session.getUserId()) allowed = true;
        if (!allowed)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //================================================================
        User user;
        ProjectAccess[] accesslist = auriga.projectAtlas().getAccessList(projectid, 0);
        for (ProjectAccess access : accesslist) {
            try {
                user = auriga.getAuthLambda().getUser(access.userID());
                access.setUserName(user.loginName());
            }
            catch (Exception e) {}
        }
        //================================================================
        return accesslist;
        //================================================================        
    }
    //********************************************************************
}
//************************************************************************
