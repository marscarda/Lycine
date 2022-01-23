package lycine.design;
//************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.Session;
import methionine.finance.AlterUsage;
import methionine.finance.UsageCost;
import methionine.project.Project;
import tryptophan.design.CustomLabel;
import tryptophan.design.Metric;
import tryptophan.design.Form;
import tryptophan.design.FormMetricRef;
//************************************************************************
public class DesignCenter {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    /**
     * 
     * @param metric
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createMetric (Metric metric, Session session) throws AppException, Exception {
        //----------------------------------------------------------------
        if (metric.getName().length() == 0)
            throw new AppException("Variable Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //----------------------------------------------------------------
        if (metric.getLabel().length() == 0)
            throw new AppException("Label cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We check the user has write acces to the project
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(metric.projectID(), session, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = auriga.projectAtlas().getProject(metric.projectID());
        //------------------------------------------------------------------
        //We persist the cost of this particular variable.
        metric.setCost(UsageCost.METRIC);
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        auriga.getDesignLambda().addCreateVariableLock(tabs);
        auriga.getBillingLambda().lockAlterUsage(tabs);
        auriga.projectAtlas().setLock(tabs);
        auriga.getDesignLambda().setAutoCommit(0);
        auriga.getDesignLambda().lockTables(tabs);
        //==================================================================
        //We check existences.
        auriga.projectAtlas().inMasterProject(project.projectID());
        //==================================================================
        auriga.getDesignLambda().createVariable(metric);
        //==================================================================
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(UsageCost.METRIC);
        alter.setStartingEvent("Metric '" + metric.getName() + "' Created");
        auriga.getBillingLambda().alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        auriga.getDesignLambda().commit();
        auriga.getDesignLambda().unLockTables();
        //------------------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param type
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public String[] getCategories (int type, Session session) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(session.getCurrentProject(), session, 1);
        //******************************************************************        
        return auriga.getDesignLambda().getVariableCategories(session.getCurrentProject(), type);
        //******************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param type
     * @param session
     * @param category
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Metric[] getMetrics (int type, String category, Session session) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(session.getCurrentProject(), session, 1);
        //******************************************************************
        if (category == null) category = "";
        //******************************************************************
        return auriga.getDesignLambda().getVariables(session.getCurrentProject(), type, category);
        //******************************************************************
    }
    //********************************************************************
    /**
     * Creates a new questionnaire.
     * @param questionary
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createForm (Form questionary, Session session) throws AppException, Exception {
        if (questionary.getName().length() == 0)
            throw new AppException("Form Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We check the user has read acces to the project
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(session.getCurrentProject(), session, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = auriga.projectAtlas().getProject(questionary.projectID());
        //------------------------------------------------------------------
        //We persist the cost of this particular variable.
        questionary.cost = UsageCost.QUESTIONARY;
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        auriga.getDesignLambda().addCreateQuestionaryLock(tabs);
        auriga.getBillingLambda().lockAlterUsage(tabs);
        auriga.getDesignLambda().setAutoCommit(0);
        auriga.getDesignLambda().lockTables(tabs);
        //------------------------------------------------------------------
        auriga.getDesignLambda().createForm(questionary);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(UsageCost.QUESTIONARY);
        alter.setStartingEvent("Questionary '" + questionary.getName() + "' Created");
        auriga.getBillingLambda().alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        auriga.getDesignLambda().commit();
        auriga.getDesignLambda().unLockTables();
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
        Form questionnaire = auriga.getDesignLambda().getQuestionnaire(questionnaireid);
        //****************************************************************
        //We check the user has read acces to the project
        auriga.projectAtlas().checkAccess(questionnaire.projectID(), userid, 1);
        //----------------------------------------------------------------
        return questionnaire;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Form[] getQuestionaries (Session session) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(session.getCurrentProject(), session, 1);
        //******************************************************************
        return auriga.getDesignLambda().getQuestionaries(session.getCurrentProject());
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
        Form quest = auriga.getDesignLambda().getQuestionnaire(questionnaireid);
        auriga.projectAtlas().checkAccess(quest.projectID(), userid, 2);
        //----------------------------------------------------------------
        auriga.getDesignLambda().updateQuestionnaire(questionnaireid, questionnaire);
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
        Form form = auriga.getDesignLambda().getQuestionnaire(formid);
        auriga.projectAtlas().checkAccess(form.projectID(), userid, 3);
        //----------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = auriga.projectAtlas().getProject(form.projectID(), 0);
        //****************************************************************
        TabList tabs = new TabList();
        auriga.getDesignLambda().addDestroyFormLock(tabs);
        auriga.getBillingLambda().lockAlterUsage(tabs);
        auriga.projectAtlas().setLock(tabs);
        auriga.getDesignLambda().setAutoCommit(0);
        auriga.getDesignLambda().lockTables(tabs);
        //==================================================================
        //Check existence in master.
        auriga.projectAtlas().inMasterProject(project.projectID());
        auriga.getDesignLambda().inMasterProject(formid);
        //==================================================================
        auriga.getDesignLambda().destroyForm(formid);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(form.cost);
        alter.setStartingEvent("Form '" + form.getName() + "' Destroyed");
        auriga.getBillingLambda().alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        auriga.getDesignLambda().commit();
        auriga.getDesignLambda().unLockTables();
        //----------------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param formref
     * @param userid 
     * @throws methionine.AppException 
     */
    public void addFormMetricRef (FormMetricRef formref, long userid) throws AppException, Exception {
        //****************************************************************
        // We fetch the variable and questionnaire.
        Metric metric = auriga.getDesignLambda().getVariable(formref.variableID());
        Form form = auriga.getDesignLambda().getQuestionnaire(formref.formID());
        //****************************************************************
        //We check the variable and questionnarie belongs to the same project.
        if (metric.projectID() != form.projectID())
            throw new AppException("Form and Variable are from different projects", AppException.NOTTHESAMEPROJECT);
        //****************************************************************
        //We check the performing user has access to the project.
        auriga.projectAtlas().checkAccess(metric.projectID(), userid, 2);
        //================================================================
        //We set the project ID. 
        formref.setProjectId(metric.projectID());
        //****************************************************************
        // Writing part
        //****************************************************************
        TabList tabs = new TabList();
        auriga.getDesignLambda().addAddToQuestionnaire(tabs);
        auriga.projectAtlas().setLock(tabs);
        auriga.getDesignLambda().setAutoCommit(0);
        auriga.getDesignLambda().lockTables(tabs);
        //----------------------------------------------------------------
        //We check the project exists
        auriga.projectAtlas().inMasterProject(metric.projectID());
        //----------------------------------------------------------------
        //We Add The variable to questionnaire
        //Form and metric existence are checked there.
        auriga.getDesignLambda().addToQuestionnaire(formref);
        //----------------------------------------------------------------
        //We are done.
        auriga.getDesignLambda().commit();
        auriga.getDesignLambda().unLockTables();
        //****************************************************************
        //Last we add the variable to the question for display purpose.
        formref.fillVariable(metric);
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
    public FormMetricRef[] getFormQuestions (long formid, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has read acces to the project
        Form quest = auriga.getDesignLambda().getQuestionnaire(formid);
        auriga.projectAtlas().checkAccess(quest.projectID(), userid, 1);
        //----------------------------------------------------------------
        return auriga.getDesignLambda().getFormQuestions(formid);
        //****************************************************************
    }
    //********************************************************************
    public void setCustomLabels (CustomLabel[] labels, long projectid, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the user has write acces to the project
        auriga.projectAtlas().checkAccess(projectid, userid, 2);
        //----------------------------------------------------------------
        for (CustomLabel label : labels)
            label.setProjectId(projectid);
        //----------------------------------------------------------------
        auriga.getDesignLambda().setCustomLabels(labels);
        //****************************************************************
    }
    //====================================================================
    /**
     * 
     * @param session
     * @param groupcode
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public CustomLabel[] getCustomLabels (int groupcode, Session session) throws AppException, Exception {
        //******************************************************************
        //We check the user has read acces to the project
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(session.getCurrentProject(), session, 1);
        //******************************************************************
        return auriga.getDesignLambda().getCustomLabels(session.getCurrentProject(), groupcode);
        //******************************************************************
    }
    //********************************************************************
}
//************************************************************************
