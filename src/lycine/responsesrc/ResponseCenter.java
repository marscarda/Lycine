package lycine.responsesrc;
//***************************************************************************
import methionine.AppException;
import tryptophan.survey.responses.ResponseLambda;
import tryptophan.survey.responses.ResponseObject;
import tryptophan.survey.responses.ResponseRow;
import tryptophan.survey.sampling.SampleLamda;
import tryptophan.survey.sampling.SampleRecord;
//***************************************************************************
public class ResponseCenter {
    //***********************************************************************
    SampleLamda samplelambda = null;
    ResponseLambda resplamda = null;
    //=======================================================================
    public void setSampleLambda (SampleLamda samplelambda) { this.samplelambda = samplelambda; }
    public void setResponseLambda (ResponseLambda resplambda) { this.resplamda = resplambda; }
    //***********************************************************************
    public FutureSample getSample (long sampleid) throws AppException, Exception {
        FutureSample sample = new FutureSample();
        //===================================================================
        SampleRecord samplerec = samplelambda.getSample(sampleid);
        sample.setSampleRecord(samplerec);
        //===================================================================
        ResponseObject[] resprecords = resplamda.getResponseObjects(sampleid, true);
        ResponseSubject response;
        for (ResponseObject resprec : resprecords) {
            //-------------------------------------------------------------
            response = new ResponseSubject();
            response.setResponseRecord(resprec);
            ResponseRow[] rows = resplamda.getResponseRows(resprec.getID());
            response.setResponseRows(rows);
            //-------------------------------------------------------------
            sample.addResponse(response);
        }
        //===================================================================
        return sample;
        //===================================================================
    }
    //***********************************************************************
}
//***************************************************************************

