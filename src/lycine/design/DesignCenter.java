package lycine.design;
//************************************************************************
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthLamda;
import methionine.billing.AlterUsage;
import methionine.billing.BillingLambda;
import methionine.billing.UsageCost;
import methionine.project.Project;
import methionine.project.ProjectLambda;
import newfactor.survey.design.Variable;
import newfactor.survey.design.DesignLambda;
import newfactor.survey.design.Questionary;
//************************************************************************
public class DesignCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    DesignLambda variablelambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setVariableLambda (DesignLambda variablelambda) { this.variablelambda = variablelambda; }
    //********************************************************************
    public void createVariable (Variable variable, long userid) throws AppException, Exception {
        //----------------------------------------------------------------
        if (variable.getName().length() == 0)
            throw new AppException("Variable Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //----------------------------------------------------------------
        if (variable.getLabel().length() == 0)
            throw new AppException("Label cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We check the user has write acces to the project
        projectlambda.checkAccess(variable.projectID(), userid, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(variable.projectID(), 0);
        //------------------------------------------------------------------
        //We persist the cost of this particular variable.
        variable.setCost(UsageCost.VARIABLE);
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        variablelambda.addCreateVariableLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        variablelambda.setAutoCommit(0);
        variablelambda.lockTables(tabs);
        //------------------------------------------------------------------
        variablelambda.createVariable(variable);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(UsageCost.VARIABLE);
        alter.setStartingEvent("Variable '" + variable.getName() + "' Created");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        variablelambda.commit();
        variablelambda.unLockTables();
        //------------------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param type
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public String[] getCategories (long projectid, int type, long userid) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        projectlambda.checkAccess(projectid, userid, 1);
        //------------------------------------------------------------------
        
        
        //******************************************************************
        return variablelambda.getVariableCategories(projectid, type);
        //******************************************************************
    }
    //********************************************************************
    public Variable[] getVariables (long projectid, int type, String category, long userid) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        projectlambda.checkAccess(projectid, userid, 1);
        //------------------------------------------------------------------
        if (category == null) category = "";
        //******************************************************************
        return variablelambda.getVariables(projectid, type, category);
        //******************************************************************
    }
    //********************************************************************
    public void createQuestionary (Questionary questionary, long userid) throws AppException, Exception {
        if (questionary.getName().length() == 0)
            throw new AppException("Variable Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We check the user has write acces to the project
        projectlambda.checkAccess(questionary.projectID(), userid, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(questionary.projectID(), 0);
        //------------------------------------------------------------------
        //We persist the cost of this particular variable.
        questionary.cost = UsageCost.QUESTIONARY;
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        variablelambda.addCreateQuestionaryLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        variablelambda.setAutoCommit(0);
        variablelambda.lockTables(tabs);
        //------------------------------------------------------------------
        variablelambda.createVariableReader(questionary);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(UsageCost.QUESTIONARY);
        alter.setStartingEvent("Questionary '" + questionary.getName() + "' Created");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        variablelambda.commit();
        variablelambda.unLockTables();
        //------------------------------------------------------------------
    }
    //********************************************************************
    public Questionary[] getQuestionaries (long projectid, long userid) throws AppException, Exception {



        return variablelambda.getQuestionaries(projectid);
    }
    //********************************************************************
}
//************************************************************************
