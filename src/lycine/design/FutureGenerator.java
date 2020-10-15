package lycine.design;
//***************************************************************************
import methionine.AppException;
import tryptophan.survey.responses.ResponseLambda;
import tryptophan.survey.responses.ResponseObject;
import tryptophan.survey.sampling.SampleLamda;
import tryptophan.survey.sampling.SampleRecord;
//***************************************************************************
public class FutureGenerator {
    //***********************************************************************
    SampleLamda samplelambda = null;
    ResponseLambda resplamda = null;
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
            response = new ResponseSubject();
            response.setResponseRecord(resprec);
            
            
            
            
            sample.addResponse(response);
        }
        //===================================================================
        return sample;
        //===================================================================
    }
    //***********************************************************************
}
//***************************************************************************

