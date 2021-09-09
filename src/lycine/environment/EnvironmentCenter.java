package lycine.environment;
//************************************************************************
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthLamda;
import methionine.billing.AlterUsage;
import methionine.billing.BillingLambda;
import methionine.billing.UsageCost;
import methionine.project.Project;
import methionine.project.ProjectLambda;
import threonine.universe.Universe;
import threonine.universe.UniverseLambda;
import tryptophan.mixqwerty.TrialSpace;
import tryptophan.mixqwerty.TrialLambda;
//************************************************************************
public class EnvironmentCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    TrialLambda environmentlambda = null;
    UniverseLambda universelambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setEnvironmentLambda (TrialLambda environmentlambda) { this.environmentlambda = environmentlambda; }
    public void setUniverseLambda (UniverseLambda universelambda) { this.universelambda = universelambda; }
    //********************************************************************
    public void createEnvirnment (TrialSpace environment, long userid) throws AppException, Exception {
        //****************************************************************
        if (environment.getName().length() == 0)
            throw new AppException("Environment Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //----------------------------------------------------------------
        if (environment.universeID() == 0)
            throw new AppException("A universe must be selected", AppException.INVALIDDATASUBMITED);
        //****************************************************************
        //Reading Part
        //****************************************************************
        //We check the user has write acces to the project
        projectlambda.checkAccess(environment.projectID(), userid, 2);
        //----------------------------------------------------------------
        //We recover the universe. We check it exists and add name to environment.
        Universe universe = universelambda.getUniverse(environment.universeID());
        environment.setUniverseName(universe.getName());
        //----------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(environment.projectID(), 0);
        //----------------------------------------------------------------
        //We persist the cost of this particular variable.
        environment.cost = UsageCost.ENVIRONMENT;
        //****************************************************************
        //Writing Part
        //****************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        environmentlambda.addCreateEnvironmentLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        environmentlambda.setAutoCommit(0);
        environmentlambda.lockTables(tabs);
        //----------------------------------------------------------------
        environmentlambda.createEnvironment(environment);
        //----------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(environment.cost);
        alter.setStartingEvent("Environment '" + environment.getName() + "' Created");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        environmentlambda.commit();
        environmentlambda.unLockTables();
        //------------------------------------------------------------------
    }
    //********************************************************************
    /**
     * Returns an array of environments given a project id
     * @param projectid
     * @param userid
     * @param fillextras
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public TrialSpace[] getEnvironments (long projectid, long userid, boolean fillextras) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        projectlambda.checkAccess(projectid, userid, 1);
        //****************************************************************
        TrialSpace[] environments = environmentlambda.getEnviromentsByProject(projectid);
        if (!fillextras) return environments;
        //----------------------------------------------------------------
        Universe universe;
        for (TrialSpace environment : environments) {
            //----------------------------------------------
            try {
                universe = universelambda.getUniverse(environment.universeID());
                environment.setUniverseName(universe.getName());
            }
            catch (AppException e) {}
            //----------------------------------------------
        }
        //----------------------------------------------------------------
        return environments;
        //****************************************************************
    }
    //********************************************************************
    /**
     * Destroys an environment.
     * @param environmentid
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void destroyEnvironments (long environmentid, long userid) throws AppException, Exception {
        //****************************************************************
        //We fetch the environment and check the performing user has access to the project.
        TrialSpace environment = environmentlambda.getEnvironment(environmentid);
        projectlambda.checkAccess(environment.projectID(), userid, 3);
        //----------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(environment.projectID(), 0);
        //****************************************************************
        TabList tabs = new TabList();
        environmentlambda.addDestroyEnvironment(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        environmentlambda.setAutoCommit(0);
        environmentlambda.lockTables(tabs);
        //------------------------------------------------------------------
        environmentlambda.destroyEnvironment(environmentid);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(environment.cost);
        alter.setStartingEvent("Environment '" + environment.getName() + "' Destroyed");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        environmentlambda.commit();
        environmentlambda.unLockTables();
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
