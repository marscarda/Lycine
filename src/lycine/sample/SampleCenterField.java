package lycine.sample;
//************************************************************************
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.billing.SystemCharge;
import methionine.project.Project;
import tryptophan.design.CustomLabel;
import tryptophan.design.DesignErrorCodes;
import tryptophan.design.Form;
import tryptophan.design.FormMetricRef;
import tryptophan.sample.Responder;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleErrorCodes;
//************************************************************************
public class SampleCenterField extends SampleCenterPanel {
    //********************************************************************
    /**
     * Returns a list of samples given an entrusted user id
     * @param userid
     * @return A list of samples
     * @throws AppException
     * @throws Exception 
     */
    public Sample[] getUserActiveSamples (long userid) throws AppException, Exception {
        //****************************************************************
        Sample[] samples = auriga.getSampleLambda().getUserActiveSamples(userid);
        return samples;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Sample getSample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = auriga.getSampleLambda().getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //---------------------------------------------------------------
        //Check if the form exists. If not the sample is invalid
        try { auriga.getDesignLambda().getQuestionnaire(sample.formID()); }
        catch (AppException e) {
            if (e.getErrorCode() == DesignErrorCodes.FORMNOTFOUND)
                throw new AppException("Sample out of taking", SampleErrorCodes.SMPLEOUTOFTAKING);
        }
        return sample;
        //---------------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Form getFormBySample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = auriga.getSampleLambda().getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        Form form = auriga.getDesignLambda().getQuestionnaire(sample.formID());
        return form;
    }
    //********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public FormMetricRef[] getQuestionsBySample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = auriga.getSampleLambda().getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        Form form = auriga.getDesignLambda().getQuestionnaire(sample.formID());
        FormMetricRef[] questions = auriga.getDesignLambda().getFormQuestions(form.formID());
        return questions;
    }
    //********************************************************************
    /**
     * Returns the final form including questions and vars ready to start survey.
     * Typically used to deliver the form to an app client.
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public FinalForm getFinalFormBySample (long sampleid, long userid) throws AppException, Exception {
        //********************************************************
        Sample sample = auriga.getSampleLambda().getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //**************************************
        Form form = auriga.getDesignLambda().getQuestionnaire(sample.formID());
        //---------------------------------
        //Metric Ref are selected by an inner Join. Metric existence is asured.
        FormMetricRef[] questions = auriga.getDesignLambda().getFormQuestions(form.formID());
        //---------------------------------
        FinalForm finalform = new FinalForm();
        finalform.form = form;
        finalform.questions = questions;
        //********************************************************
        CustomLabel label;
        CustomLabel[] labels = auriga.getDesignLambda().getCustomLabels(sample.projectID(), CustomLabel.G_PUBVIEW);
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_FORMULATION);
        if (label.isValid()) finalform.labelpubviewf = label.labelText();
        else finalform.labelpubviewf = CustomLabel.D_PUBVIEW_FORMULATION;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_POSITIVE);
        if (label.isValid()) finalform.labelpubviewpos = label.labelText();
        else finalform.labelpubviewpos = CustomLabel.D_PUBVIEW_POSITIVE;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_NEUTRAL);
        if (label.isValid()) finalform.labelpubviewneu = label.labelText();
        else finalform.labelpubviewneu = CustomLabel.D_PUBVIEW_NEUTRAL;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_NEGATIVE);
        if (label.isValid()) finalform.labelpubviewneg = label.labelText();
        else finalform.labelpubviewneg = CustomLabel.D_PUBVIEW_NEGATIVE;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_UNKNOWN);
        if (label.isValid()) finalform.labelpubviewunk = label.labelText();
        else finalform.labelpubviewunk = CustomLabel.D_PUBVIEW_UNKNOWN;
        //--------------------------------------------
        return finalform;
        //--------------------------------------------
    }
    //********************************************************************
    /**
     * Adds a new field response to a sample
     * @param responder
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void fieldResponse (Responder responder, long userid) throws AppException, Exception {
        //***************************************************
        //We first get the sample involved and check if the
        //user has is the one entrusted to it.
        Sample sample = auriga.getSampleLambda().getSample(responder.sampleID());
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //***************************************************
        //We get the project where the response is
        Project project = auriga.projectAtlas().getProject(sample.projectID(), 0);
        //***************************************************
        responder.setProjectId(project.projectID());
        responder.setUserId(userid);
        //***************************************************
        TabList tabs = new TabList();
        auriga.getSampleLambda().submitResponseLock(tabs);
        auriga.getBillingLambda().addSystemChargeLock(tabs);
        auriga.getSampleLambda().setAutoCommit(0);
        auriga.getSampleLambda().lockTables(tabs);
        //===================================================
        auriga.getSampleLambda().addFieldResponse(responder);
        //===================================================
        SystemCharge charge = new SystemCharge();
        charge.setCost(sample.responseSubmitCost());
        charge.setDescription("Response in Sample '" + sample.getName() + "'");
        charge.setProjectId(project.projectID());
        charge.setProjectName(project.getName());
        charge.setUserid(project.getOwner());
        auriga.getBillingLambda().createSystemCharge(charge);
        //===================================================
        //We are all done.
        auriga.getSampleLambda().commit();
        //***************************************************
    }
    //********************************************************************
}
//************************************************************************
