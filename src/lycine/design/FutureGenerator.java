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
        SampleRecord samplerec = samplelambda.getSample(sampleid);
        ResponseObject[] respobjs = resplamda.getResponseObjects(sampleid, true);
        
        
        
        



        
        
        
        return null;


    }
    //***********************************************************************
}
//***************************************************************************

