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
import tryptophan.design.CustomLabel;
import tryptophan.design.Variable;
import tryptophan.design.DesignLambda;
import tryptophan.design.Form;
import tryptophan.design.FormQuestion;
//************************************************************************
public class DesignCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    DesignLambda designlambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setVariableLambda (DesignLambda designlambda) { this.designlambda = designlambda; }
    //********************************************************************
    /**
     * 
     * @param variable
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
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
        designlambda.addCreateVariableLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        designlambda.setAutoCommit(0);
        designlambda.lockTables(tabs);
        //------------------------------------------------------------------
        designlambda.createVariable(variable);
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
        designlambda.commit();
        designlambda.unLockTables();
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
        return designlambda.getVariableCategories(projectid, type);
        //******************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param type
     * @param category
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Variable[] getVariables (long projectid, int type, String category, long userid) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        projectlambda.checkAccess(projectid, userid, 1);
        //------------------------------------------------------------------
        if (category == null) category = "";
        //******************************************************************
        return designlambda.getVariables(projectid, type, category);
        //******************************************************************
    }
    //********************************************************************
    /**
     * Creates a new questionnaire.
     * @param questionary
     * @param userid The user that is creating it.
     * @throws AppException
     * @throws Exception 
     */
    public void createForm (Form questionary, long userid) throws AppException, Exception {
        if (questionary.getName().length() == 0)
            throw new AppException("Form Name cannot be empty", AppException.INVALIDDATASUBMITED);
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
        designlambda.addCreateQuestionaryLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        designlambda.setAutoCommit(0);
        designlambda.lockTables(tabs);
        //------------------------------------------------------------------
        designlambda.createForm(questionary);
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
        designlambda.commit();
        designlambda.unLockTables();
        //------------------------------------------------------------------
    }
    //********************************************************************
    /**
     * Returns a questionary given its ID
     * @param questionnaireid
     * @param userid The user in behalf of the quest is being requested.
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Form getQuestionnaire (long questionnaireid, long userid) throws AppException, Exception {
        //****************************************************************
        //We recover the quest.
        Form questionnaire = designlambda.getQuestionnaire(questionnaireid);
        //****************************************************************
        //We check the user has read acces to the project
        projectlambda.checkAccess(questionnaire.projectID(), userid, 1);
        //----------------------------------------------------------------
        return questionnaire;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param projectid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Form[] getQuestionaries (long projectid, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has read acces to the project
        projectlambda.checkAccess(projectid, userid, 1);
        //----------------------------------------------------------------
        return designlambda.getQuestionaries(projectid);
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param questionnaireid
     * @param questionnaire
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void updateQuestionnaire (long questionnaireid, Form questionnaire, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has read acces to the project
        Form quest = designlambda.getQuestionnaire(questionnaireid);
        projectlambda.checkAccess(quest.projectID(), userid, 2);
        //----------------------------------------------------------------
        designlambda.updateQuestionnaire(questionnaireid, questionnaire);
        //****************************************************************
    }
    //********************************************************************
    /**
     * Deletes a Form.
     * @param formid
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void destroyForm (long formid, long userid) throws AppException, Exception {
        //****************************************************************
        //We recover the form and check the user has delete acces to the project
        Form form = designlambda.getQuestionnaire(formid);
        projectlambda.checkAccess(form.projectID(), userid, 3);
        //----------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(form.projectID(), 0);
        //------------------------------------------------------------------
        TabList tabs = new TabList();
        designlambda.addDestroyFormLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        designlambda.setAutoCommit(0);
        designlambda.lockTables(tabs);
        //------------------------------------------------------------------
        designlambda.destroyForm(formid);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(form.cost);
        alter.setStartingEvent("Form '" + form.getName() + "' Destroyed");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        designlambda.commit();
        designlambda.unLockTables();
        //----------------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param question
     * @param userid 
     * @throws methionine.AppException 
     */
    public void addVariableToQuestionnaire (FormQuestion question, long userid) throws AppException, Exception {
        //****************************************************************
        // We fetch the variable and questionnaire.
        Variable variable = designlambda.getVariable(question.variableID());
        Form form = designlambda.getQuestionnaire(question.formID());
        //****************************************************************
        //We check the variable and questionnarie belongs to the same project.
        if (variable.projectID() != form.projectID())
            throw new AppException("Form and Variable are from different projects", AppException.NOTTHESAMEPROJECT);
        //****************************************************************
        //We check the performing user has access to the project.
        projectlambda.checkAccess(variable.projectID(), userid, 2);
        //****************************************************************
        // Writing part
        //****************************************************************
        TabList tabs = new TabList();
        designlambda.addAddToQuestionnaire(tabs);
        designlambda.setAutoCommit(0);
        designlambda.lockTables(tabs);
        //----------------------------------------------------------------
        //We Add The variable to questionnaire
        designlambda.addToQuestionnaire(question);
        //----------------------------------------------------------------
        //We are done.
        designlambda.commit();
        designlambda.unLockTables();
        //****************************************************************
        //Last we add the variable to the question for display purpose.
        question.fillVariable(variable);
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param formid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public FormQuestion[] getFormQuestions (long formid, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has read acces to the project
        Form quest = designlambda.getQuestionnaire(formid);
        projectlambda.checkAccess(quest.projectID(), userid, 1);
        //----------------------------------------------------------------
        return designlambda.getFormQuestions(formid);
        //****************************************************************
    }
    //********************************************************************
    public void setCustomLabels (CustomLabel[] labels, long projectid, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has write acces to the project
        projectlambda.checkAccess(projectid, userid, 2);
        //----------------------------------------------------------------
        for (CustomLabel label : labels)
            label.setProjectId(projectid);
        //----------------------------------------------------------------
        designlambda.setCustomLabels(labels);
        //****************************************************************
    }
    //====================================================================
    /**
     * 
     * @param projectid
     * @param groupcode
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public CustomLabel[] getCustomLabels (long projectid, int groupcode, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has read acces to the project
        projectlambda.checkAccess(projectid, userid, 1);
        return designlambda.getCustomLabels(projectid, groupcode);
    }
    //********************************************************************
}
//************************************************************************
