package lycine.sample;
//************************************************************************
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
import tryptophan.sample.Sample;
import tryptophan.sample.SampleLambda;
//************************************************************************
public class SampleCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    DesignLambda designlambda = null;
    SampleLambda samplelambda = null;
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
}
//************************************************************************
