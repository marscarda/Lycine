package lycine.sample;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import tryptophan.design.DesignAtlas;
import tryptophan.design.Form;
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
    Form form = null;
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
}
//************************************************************************
