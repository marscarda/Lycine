package lycine.sample;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import tryptophan.sample.ResponseCall;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.Sample;
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
    public void doCall () throws AppException, Exception {
        
        //****************************************************************
        //We recover the call and check it is valid
        ResponseCall call = auriga.getSampleLambda().getResponseCall(someid);
        boolean valid = true;
        if (call.responded()) valid = false;
        if (call.isExpired()) valid = false;
        if (!valid) {
            throw new Exception("Invalid");
        }
        //****************************************************************
        //We now recover the sample.
        Sample sample = auriga.getSampleLambda().getSample(call.sampleID());
        
        
        //****************************************************************
        
        
        
        
        
        
        
        
        
        
        
        
    }
    //********************************************************************
}
//************************************************************************
