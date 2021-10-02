package lycine.sample;
//************************************************************************
import tryptophan.sample.SamplePayLoad;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import methionine.billing.AlterUsage;
import methionine.billing.BillingLambda;
import methionine.billing.UsageCost;
import methionine.project.Project;
import methionine.project.ProjectLambda;
import tryptophan.design.DesignLambda;
import tryptophan.design.Form;
import tryptophan.sample.Responder;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleLambda;
//************************************************************************
public class SampleCenterPanel {
    //********************************************************************
    protected AuthLamda authlambda = null;
    protected ProjectLambda projectlambda = null;
    protected BillingLambda billinglambda = null;
    protected DesignLambda designlambda = null;
    protected SampleLambda samplelambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setVariableLambda (DesignLambda variablelambda) { this.designlambda = variablelambda; }
    public void setSampleLambda (SampleLambda samplelambda) { this.samplelambda = samplelambda; }
    //********************************************************************
    /**
     * Creates a new sample.
     * @param sample
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void createSample (Sample sample, long userid) throws AppException, Exception {
        //****************************************************************
        if (sample.getName().length() == 0)
            throw new AppException("Sample Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //----------------------------------------------------------------
        if (sample.formID() == 0)
            throw new AppException("A form must be selected", AppException.INVALIDDATASUBMITED);
        //****************************************************************
        //We check the performing user has access to the project.
        projectlambda.checkAccess(sample.projectID(), userid, 2);
        //****************************************************************
        //We check the form ID belongs to the same project.
        Form form = designlambda.getQuestionnaire(sample.formID());
        Project project = projectlambda.getProject(form.projectID(), 0);
        if (form.projectID() != sample.projectID())
            throw new AppException("Form does not belong to the project", AppException.NOTTHESAMEPROJECT);
        //****************************************************************
        //If a user name is entrusted we find the user id.
        if (sample.getUserName().length() != 0)
            sample.setUserId(authlambda.getUserIdByIdentifier(sample.getUserName()));
        //----------------------------------------------------------------
        //Set the form name in the sample for response sake
        sample.setFormName(form.getName());
        //****************************************************************
        // Writing part
        //****************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        samplelambda.addCreateSampleLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        samplelambda.setAutoCommit(0);
        samplelambda.lockTables(tabs);
        //----------------------------------------------------------------
        //We create the sample.
        sample.cost = UsageCost.SAMPLE;
        samplelambda.createSample(sample);
        //----------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(sample.cost);
        alter.setStartingEvent("Sample '" + sample.getName() + "' Created");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        samplelambda.commit();
        samplelambda.unLockTables();
        //****************************************************************
    }
    //********************************************************************
    /**
     * Returns a list of samples given a project
     * @param projectid
     * @param userid
     * @param fillextras
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Sample[] getSamples (long projectid, long userid, boolean  fillextras) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        if (userid != 0)
            projectlambda.checkAccess(projectid, userid, 1);
        //****************************************************************
        Sample[] samples = samplelambda.getSamplesByProject(projectid);
        if (!fillextras) return samples;
        //----------------------------------------------------------------
        User user;
        Form form;
        for (Sample sample : samples) {
            //------------------------------------------------------------
            //Fill the user name
            try { 
                user = authlambda.getUser(sample.userID(), false); 
                sample.setUserName(user.loginName());
            }
            catch (AppException e) {}
            //------------------------------------------------------------
            try {
                form = designlambda.getQuestionnaire(sample.formID());
                sample.setFormName(form.getName());
            }
            catch (AppException e) {}
            //------------------------------------------------------------
        }
        //----------------------------------------------------------------
        return samples;
        //****************************************************************
    }
    //********************************************************************
    /**
     * Destroys a sample.
     * @param sampleid
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void destroySample (long sampleid, long userid) throws AppException, Exception {
        //****************************************************************
        //We fetch the sample and check the performing user has access to the project.
        Sample sample = samplelambda.getSample(sampleid);
        projectlambda.checkAccess(sample.projectID(), userid, 3);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(sample.projectID(), 0);
        //****************************************************************
        TabList tabs = new TabList();
        samplelambda.addDestroySampleLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        samplelambda.setAutoCommit(0);
        samplelambda.lockTables(tabs);
        //------------------------------------------------------------------
        samplelambda.destroySample(sampleid);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(sample.cost);
        alter.setStartingEvent("Sample '" + sample.getName() + "' Destroyed");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        samplelambda.commit();
        samplelambda.unLockTables();
        //****************************************************************
    }
    //********************************************************************
    /**
     * Returns a sample payload given a sampleid.
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SamplePayLoad getSamplePayload (long sampleid, long userid) throws AppException, Exception {
        //****************************************************************
        //We recover the sample.
        Sample sample = samplelambda.getSample(sampleid);
        //----------------------------------------------------------------
        //We check (If required) if the user has access to the project.
        if (userid != 0)
            projectlambda.checkAccess(sample.projectID(), userid, 1);
        //****************************************************************
        //We create the sample payload instance and set the sampleid.
        SamplePayLoad samplepayload = new SamplePayLoad();
        samplepayload.setSample(sample);
        //****************************************************************
        //We recover the responses and add them to the payload.
        Responder[] responses = samplelambda.getResponses(sampleid, true);
        samplepayload.setResponses(responses);
        //----------------------------------------------------------------
        return samplepayload;
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
