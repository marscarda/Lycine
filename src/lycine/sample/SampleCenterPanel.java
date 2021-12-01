package lycine.sample;
//************************************************************************
import histidine.AurigaObject;
import tryptophan.sample.SamplePayLoad;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.User;
import methionine.finance.AlterUsage;
import methionine.finance.UsageCost;
import methionine.project.Project;
import tryptophan.design.Form;
import tryptophan.sample.Responder;
import tryptophan.sample.Sample;
//************************************************************************
public class SampleCenterPanel {
    //********************************************************************
    protected AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
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
        auriga.projectAtlas().checkAccess(sample.projectID(), userid, 2);
        //****************************************************************
        //We check the form ID belongs to the same project.
        Form form = auriga.getDesignLambda().getQuestionnaire(sample.formID());
        Project project = auriga.projectAtlas().getProject(form.projectID(), 0);
        if (form.projectID() != sample.projectID())
            throw new AppException("Form does not belong to the project", AppException.NOTTHESAMEPROJECT);
        //****************************************************************
        //If a user name is entrusted we find the user id.
        if (sample.getUserName().length() != 0)
            sample.setUserId(auriga.getAuthLambda().getUserIdByIdentifier(sample.getUserName()));
        //----------------------------------------------------------------
        //Set the form name in the sample for response sake
        sample.setFormName(form.getName());
        //****************************************************************
        // Writing part
        //****************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        auriga.getSampleLambda().addCreateSampleLock(tabs);
        auriga.getBillingLambda().AddLockAlterUsage(tabs);
        auriga.getSampleLambda().setAutoCommit(0);
        auriga.getSampleLambda().lockTables(tabs);
        //----------------------------------------------------------------
        //We create the sample.
        sample.cost = UsageCost.SAMPLE;
        auriga.getSampleLambda().createSample(sample);
        //----------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(sample.cost);
        alter.setStartingEvent("Sample '" + sample.getName() + "' Created");
        auriga.getBillingLambda().alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        auriga.getSampleLambda().commit();
        auriga.getSampleLambda().unLockTables();
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
            auriga.projectAtlas().checkAccess(projectid, userid, 1);
        //****************************************************************
        Sample[] samples = auriga.getSampleLambda().getSamplesByProject(projectid);
        if (!fillextras) return samples;
        //----------------------------------------------------------------
        User user;
        Form form;
        for (Sample sample : samples) {
            //------------------------------------------------------------
            //Fill the user name
            try { 
                user = auriga.getAuthLambda().getUser(sample.userID(), false); 
                sample.setUserName(user.loginName());
            }
            catch (AppException e) {}
            //------------------------------------------------------------
            try {
                form = auriga.getDesignLambda().getQuestionnaire(sample.formID());
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
        /*
        //****************************************************************
        //We fetch the sample and check the performing user has access to the project.
        Sample sample = auriga.getSampleLambda().getSample(sampleid);
        auriga.getProjectLambda().checkAccess(sample.projectID(), userid, 3);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = auriga.getProjectLambda().getProject(sample.projectID(), 0);
        //****************************************************************
        TabList tabs = new TabList();
        auriga.getSampleLambda().addDestroySampleLock(tabs);
        auriga.getBillingLambda().AddLockAlterUsage(tabs);
        auriga.getSampleLambda().setAutoCommit(0);
        auriga.getSampleLambda().lockTables(tabs);
        //------------------------------------------------------------------
        auriga.getSampleLambda().destroySample(sampleid);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(sample.cost);
        alter.setStartingEvent("Sample '" + sample.getName() + "' Destroyed");
        auriga.getBillingLambda().alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        auriga.getSampleLambda().commit();
        auriga.getSampleLambda().unLockTables();
        //****************************************************************
        */
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
        Sample sample = auriga.getSampleLambda().getSample(sampleid);
        //----------------------------------------------------------------
        //We check (If required) if the user has access to the project.
        if (userid != 0)
            auriga.projectAtlas().checkAccess(sample.projectID(), userid, 1);
        //****************************************************************
        //We create the sample payload instance and set the sampleid.
        SamplePayLoad samplepayload = new SamplePayLoad();
        samplepayload.setSample(sample);
        //****************************************************************
        //We recover the responses and add them to the payload.
        Responder[] responses = auriga.getSampleLambda().getResponses(sampleid, true);
        samplepayload.setResponses(responses);
        //----------------------------------------------------------------
        return samplepayload;
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
