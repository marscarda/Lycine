package lycine.epsilon;
//***************************************************************************
import tryptophan.survey.responses.ResponseRow;
import methionine.AppException;
import tryptophan.survey.responses.ResponseLambda;
import tryptophan.survey.responses.ResponseObject;
import tryptophan.survey.baseform.BaseForm;
import tryptophan.survey.baseform.VarClusterLambda;
import tryptophan.survey.sampling.SampleRecord;
import tryptophan.survey.sampling.SampleLamda;
//***************************************************************************
public class FieldCollector {
    //***********************************************************************
    VarClusterLambda surveylambda = null;
    SampleLamda samplelambda = null;
    ResponseLambda responselambda = null;
    //=======================================================================
    public void setSampleLambda (SampleLamda samplelambda) { this.samplelambda = samplelambda; }
    public void setSurveyLambda (VarClusterLambda surveylambda) { this.surveylambda = surveylambda; }
    public void setResponseLambda (ResponseLambda responselambda) { this.responselambda = responselambda; }
    //***********************************************************************
    /**
     * Casts responses from a field intake.
     * @param fieldcast
     * @throws AppException SAMPLENOTFOUND SURVEYNOTFOUND UNAUTHORIZED INVALIDDATASUBMITED
     * @throws Exception 
     */
    public void castFieldResponses (FieldCast fieldcast) throws AppException, Exception {
        //===================================================================
        SampleRecord sample = samplelambda.getSample(fieldcast.sampleID());
        BaseForm survey = surveylambda.getVarCluster(sample.getSurveyId());
        //-------------------------------------------------------------------
        if (fieldcast.userID() != sample.getUserId())
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //-------------------------------------------------------------------
        ResponseRow[] rows = fieldcast.getSingleOptionsRows();
        //-------------------------------------------------------------------
        ResponseObject response = new ResponseObject();
        response.setTakeFormID(survey.getID());
        response.setSampleID(sample.getSampleId());
        response.setUserID(fieldcast.userid);
        response.setRows(rows);
        //===================================================================
        responselambda.createResponse(response);
        //===================================================================
    }
    //***********************************************************************
}
//***************************************************************************
