package lycine.design;
//***************************************************************************
import tryptophan.survey.sampling.SampleRecord;
//***************************************************************************
public class FutureSample {
    //************************************************************
    long sampleid = 0;
    SampleRecord samplerecord = null;
    ResponseSubject[] responses = null;
    int respcount = 0;
    //************************************************************
    void setSampleRecord(SampleRecord srec) { samplerecord = srec; }
    //************************************************************
    void addResponse (ResponseSubject response) {
        ResponseSubject[] newarr = new ResponseSubject[respcount + 1];
        System.arraycopy(responses, 0, newarr, 0, respcount);
        newarr[respcount] = response;
        respcount++;
        responses = newarr;
    }
    //************************************************************
}
//***************************************************************************
