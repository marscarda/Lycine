package lycine.sample;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import tryptophan.design.CustomLabel;
import tryptophan.design.DesignAtlas;
import tryptophan.design.Form;
import tryptophan.design.FormMetricRef;
import tryptophan.sample.ResponseCall;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleAtlas;
//************************************************************************
/**
 * For managed data collection.
 * Survey responses by code. No user auth is required.
 * Typically an URL is sent to the subject to answer the survey without authenticating.
 * @author marianoscardaccione
 */
public class FetchInputCall {
    //********************************************************************
    protected AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //--------------------------------------------------------------------
    String callcode = null;
    public void setCallCode (String callcode) { this.callcode = callcode; }
    //====================================================================
    ResponseCall responsecall = null;
    Sample sample = null;
    Form form = null;
    DisplayLabels displabels = null;
    FormMetricRef[] metricrefs = null;
    //********************************************************************
    public ResponseCall getCall () {
        if (responsecall == null) return new ResponseCall();
        return responsecall;
    }
    //====================================================================
    public Form getForm () {
        if (form == null) return new Form();
        return form;
    }
    //====================================================================
    public DisplayLabels getLabels () {
        if (displabels == null) return new  DisplayLabels();
        return displabels;
    }
    //====================================================================
    public FormMetricRef[] getMetrics () {
        if (metricrefs == null) return new FormMetricRef[0];
        return metricrefs;
    }
    //********************************************************************
    /**
     * 
     * @throws AppException
     * @throws Exception 
     */
    public void prepareIntro () throws AppException, Exception {
        //=========================================================
        SampleAtlas smpatlas = auriga.getSampleLambda();
        DesignAtlas dsgatlas = auriga.getDesignLambda();
        //=========================================================
        //If it is expired or replyed we leave here.
        responsecall = smpatlas.getResponseCall(callcode);
        if (responsecall.getResponded()) return;
        if (responsecall.isExpired()) return;
        //=========================================================
        //We recover the form.
        sample = smpatlas.getSample(responsecall.sampleID());
        form = dsgatlas.getQuestionnaire(sample.formID());
        //=========================================================
    }
    //********************************************************************
    /**
     * 
     * @throws AppException
     * @throws Exception 
     */
    public void PrepareSurvey () throws AppException, Exception {
        //****************************************************************
        //We recover all atlas.
        SampleAtlas smpatlas = auriga.getSampleLambda();
        DesignAtlas dsgatlas = auriga.getDesignLambda();
        //****************************************************************
        //If it is expired or replyed we leave here.
        responsecall = smpatlas.getResponseCall(callcode);
        if (responsecall.getResponded()) return;
        if (responsecall.isExpired()) return;
        //****************************************************************
        //We recover the form.
        sample = smpatlas.getSample(responsecall.sampleID());
        form = dsgatlas.getQuestionnaire(sample.formID());
        CustomLabel[] customlabels = dsgatlas.getCustomLabels(form.projectID(), 0);
        fillLabels(customlabels);
        //****************************************************************
        //We recover the metrics.
        metricrefs = dsgatlas.getFormQuestions(form.formID());
        //****************************************************************
    }
    //********************************************************************
    private void fillLabels (CustomLabel[] labels) {
        //----------------------------------------------------------------
        CustomLabel label;
        displabels = new DisplayLabels();
        //****************************************************************
        //PUBLIC VIEW LABELS
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_FORMULATION);
        if (label.isValid()) displabels.pubviewformulation = label.labelText();
        else displabels.pubviewformulation = CustomLabel.D_PUBVIEW_FORMULATION;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_POSITIVE);
        if (label.isValid()) displabels.pubviewpositive = label.labelText();
        else displabels.pubviewpositive = CustomLabel.D_PUBVIEW_POSITIVE;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_NEUTRAL);
        if (label.isValid()) displabels.pubviewneutral = label.labelText();
        else displabels.pubviewneutral = CustomLabel.D_PUBVIEW_NEUTRAL;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_NEGATIVE);
        if (label.isValid()) displabels.pubviewnegative = label.labelText();
        else displabels.pubviewnegative = CustomLabel.D_PUBVIEW_NEGATIVE;
        //--------------------------------------------
        label = CustomLabel.findLabelByCode(labels, CustomLabel.C_PUBVIEW_UNKNOWN);
        if (label.isValid()) displabels.pubviewunknown = label.labelText();
        else displabels.pubviewunknown = CustomLabel.D_PUBVIEW_UNKNOWN;
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
