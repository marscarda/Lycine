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
import tryptophan.mixqwerty.Environment;
import tryptophan.mixqwerty.EnvironmentLambda;
//************************************************************************
public class EnvironmentCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    EnvironmentLambda environmentlambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setEnvironmentLambda (EnvironmentLambda environmentlambda) { this.environmentlambda = environmentlambda; }
    //********************************************************************
    public void createEnvirnment (Environment environment, long userid) throws AppException, Exception {
        //****************************************************************
        if (environment.getName().length() == 0)
            throw new AppException("Environment Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        
        
        //****************************************************************
        //Reading Part
        //****************************************************************
        //We check the user has write acces to the project
        projectlambda.checkAccess(environment.projectID(), userid, 2);
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
    public Environment[] getEnvironments (long projectid, long userid, boolean fillextras) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        projectlambda.checkAccess(projectid, userid, 1);
        //****************************************************************
        Environment[] environments = environmentlambda.getEnviromentsByProject(projectid);
        if (!fillextras) return environments;
        //----------------------------------------------------------------

        //----------------------------------------------------------------
        return environments;
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
