package lycine.sample;
//************************************************************************
import histidine.AurigaObject;
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import methionine.TabList;
import methionine.finance.BalanceInfo;
import methionine.finance.FinanceRules;
import methionine.finance.SystemCharge;
import methionine.project.Project;
import tryptophan.design.FormMetricRef;
import tryptophan.design.Metric;
import tryptophan.sample.FeedBack;
import tryptophan.sample.ResponseCall;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleAtlas;
import tryptophan.sample.SampleErrorCodes;
//************************************************************************
public class ExcResponseSubmit {
    //********************************************************************
    protected AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //====================================================================
    /**
     * Can be an ID of anything in shift. Call, Sample or whatever.
     */
    long someid = 0; 
    public void setId (long id) { this.someid = id; }
    //====================================================================
    ResponseValue[] values = null;
    public void setValues(ResponseValue[] values) { this.values = values; }
    //********************************************************************
    private Project project = null;
    private Sample sample = null;
    //********************************************************************
    public void doFeedbackCall () throws AppException, Exception {
        
        //****************************************************************
        SampleAtlas smpatlas = auriga.getSampleLambda();
        //****************************************************************
        //We recover the call and check it is valid
        ResponseCall call = smpatlas.getResponseCall(someid);
        boolean valid = true;
        if (call.responded()) 
            throw new AppException("Invalid Feedback Call", SampleErrorCodes.INVALIDCALL);
        if (call.isExpired()) 
            throw new AppException("Invalid Feedback Call", SampleErrorCodes.INVALIDCALL);
        //****************************************************************
        //Recover all necesary
        sample = smpatlas.getSample(call.sampleID());
        project = auriga.projectAtlas().getProject(sample.projectID());
        //****************************************************************
        //All values must correspond to existents metrics
        //As much in the form as in the project.
        validateFeedback(sample.formID());
        //****************************************************************
        //We check the project owner is able to spend.
        ableToSpend();
        //****************************************************************
        //MASTER PART
        //****************************************************************
        //We prepare to work in the master.
        TabList tabs = new TabList();
        smpatlas.lockFeedbackCall(tabs);
        smpatlas.lockFeedBack(tabs);
        smpatlas.lockFeedBackValue(tabs);
        auriga.getBillingLambda().lockSystemCharge(tabs);
        smpatlas.setAutoCommit(0);
        smpatlas.lockTables(tabs);
        smpatlas.useMaster();
        //****************************************************************
        //This time read from the master and validated.
        call = auriga.getSampleLambda().getResponseCall(someid);
        valid = true;
        if (call.responded())
            throw new AppException("Invalid Feedback Call", SampleErrorCodes.INVALIDCALL);
        if (call.isExpired())
            throw new AppException("Invalid Feedback Call", SampleErrorCodes.INVALIDCALL);
        //****************************************************************
        //We persist a new feedback
        FeedBack feedback = new FeedBack();
        feedback.setSampleId(sample.sampleID());
        feedback.setUserId(0);
        feedback.setCallName(call.getName());
        smpatlas.addFeedBack(feedback);
        //----------------------------------------------------------------
        //We persist the values.
        for (ResponseValue value : values)
            smpatlas.addFeedBackValue(feedback.responseID(), value);
        //----------------------------------------------------------------
        //We mark the call as feedbak sent, Responded.
        smpatlas.setFeedbackCallStatus(someid);
        //----------------------------------------------------------------
        //We create the charge
        doBilling();
        //****************************************************************
        //We are all done.
        smpatlas.commit();
        //****************************************************************
    }
    //********************************************************************
    //FINANCE PART
    //********************************************************************
    //Checks the project owner is able to spend.
    private void ableToSpend () throws AppException, Exception {
        BalanceInfo balance = auriga.getBillingLambda().getTotalBalance(project.getOwner());
        FinanceRules.spendOk(balance.getTotalBalance());
    }
    //********************************************************************
    private void doBilling () throws AppException, Exception {
        SystemCharge charge = new SystemCharge();
        charge.setCost(sample.responseSubmitCost());
        charge.setDescription("Response in Sample '" + sample.getName() + "'");
        charge.setProjectId(project.projectID());
        charge.setProjectName(project.getName());
        charge.setUserid(project.getOwner());
        auriga.getBillingLambda().createSystemCharge(charge);        
    }
    //********************************************************************
    //CHECK THE RESPONSES ARE VALID
    //Metric must exist and be present in the form.
    //********************************************************************
    /**
     * 
     * @param values
     * @return 
     */
    private void validateFeedback (long formid) throws Exception {
        //-------------------------------------------------------
        List<ResponseValue> validvals = new ArrayList<>();
        FormMetricRef[] refs = auriga.getDesignLambda().getFormQuestions(formid);
        Metric[] metrics = auriga.getDesignLambda().getVariables(project.projectID(), 0, null);
        //-------------------------------------------------------
        for (ResponseValue value : values) {
            if (isPresent(value, refs, metrics))
                validvals.add(value);
        }
        values = validvals.toArray(new ResponseValue[0]);
        //-------------------------------------------------------
    }
    //********************************************************************
    /**
     * 
     * @param value
     * @param refs
     * @return
     * @throws AppException
     * @throws Exception 
     */
    private boolean isPresent (ResponseValue value, FormMetricRef[] refs, Metric[] metrics) throws AppException, Exception {
        //================================================================
        boolean present = false;
        //================================================================
        //Metric must exist in project
        for (Metric metric : metrics)
            if (value.metricID() == metric.metricID()) {
                value.setType(metric.metricType());
                present = true;
                break;
            }
        if (!present) return false;
        //================================================================
        //Metric must be present in the form.
        for (FormMetricRef ref : refs)
            if (ref.metricID() == value.metricID()) return true;
        //================================================================
        return false;
        //================================================================
    }
    //********************************************************************
}
//************************************************************************
