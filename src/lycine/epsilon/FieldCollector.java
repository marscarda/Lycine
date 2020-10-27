package lycine.epsilon;
//***************************************************************************
import tryptophan.survey.reaction.ReactionItem;
import methionine.AppException;
import tryptophan.survey.reaction.ReactionLambda;
import tryptophan.survey.reaction.ResponseRecord;
import tryptophan.survey.action.ActionSet;
import tryptophan.survey.action.ActionSetLambda;
import tryptophan.survey.sampling.SampleRecord;
import tryptophan.survey.sampling.SampleLamda;
//***************************************************************************
public class FieldCollector {
    //***********************************************************************
    ActionSetLambda surveylambda = null;
    SampleLamda samplelambda = null;
    ReactionLambda responselambda = null;
    //=======================================================================
    public void setSampleLambda (SampleLamda samplelambda) { this.samplelambda = samplelambda; }
    public void setSurveyLambda (ActionSetLambda surveylambda) { this.surveylambda = surveylambda; }
    public void setResponseLambda (ReactionLambda responselambda) { this.responselambda = responselambda; }
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
        ActionSet survey = surveylambda.getVarCluster(sample.getSurveyId());
        //-------------------------------------------------------------------
        if (fieldcast.userID() != sample.getUserId())
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        //-------------------------------------------------------------------
        ReactionItem[] rows = fieldcast.getSingleOptionsRows();
        //-------------------------------------------------------------------
        ResponseRecord response = new ResponseRecord();
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
