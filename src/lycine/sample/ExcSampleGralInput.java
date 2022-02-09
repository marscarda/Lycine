package lycine.sample;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import tryptophan.sample.ResponseCall;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleAtlas;
//************************************************************************
public class ExcSampleGralInput {
    //********************************************************************
    protected AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //--------------------------------------------------------------------
    String callcode = null;
    public void setCallCode (String callcode) { this.callcode = callcode; }
    //====================================================================
    ResponseCall responsecall = null;
    Sample sample = null;
    //********************************************************************
    /**
     * 
     * @throws AppException
     * @throws Exception 
     */
    public void getPrepareForm () throws AppException, Exception {
        SampleAtlas smpatlas = auriga.getSampleLambda();
        responsecall = smpatlas.getResponseCall(callcode);
        sample = smpatlas.getSample(responsecall.sampleID());
        
        
       
        
        
        
        
        
        
        
        
        
        
        
    }
    //********************************************************************
}
//************************************************************************
