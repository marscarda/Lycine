package lycine.epsilon;
//***************************************************************************
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import tryptophan.survey.BaseToBeNamed;
import tryptophan.survey.baseform.BaseForm;
import tryptophan.survey.baseform.VarPointer;
import tryptophan.survey.baseform.VarClusterLambda;
import tryptophan.survey.publicview.PublicViewLambda;
import tryptophan.survey.responses.ResponseLambda;
import tryptophan.survey.sampling.SampleRecord;
import tryptophan.survey.sampling.SampleLamda;
//***************************************************************************
public class SamplingCenter {
    //***********************************************************************
    AuthLamda authlambda = null;
    SampleLamda samplelambda = null;
    ResponseLambda responselambda = null;
    VarClusterLambda surveylambda = null;
    PublicViewLambda pubviewlambda = null;
    //=======================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setSampleLambda (SampleLamda samplelambda) { this.samplelambda = samplelambda; }
    public void setResponseLambda (ResponseLambda responselambda) { this.responselambda = responselambda; }
    public void setSurveyLambda (VarClusterLambda surveylambda) { this.surveylambda = surveylambda; }
    public void setPublicViewLambda (PublicViewLambda pubviewlambda) { this.pubviewlambda = pubviewlambda; }
    //***********************************************************************
    /**
     * Commits a sample for a survey to a particular user 
     * @param sample
     * @param authuserid
     * @throws AppException
     * @throws Exception 
     */
    public void commitSampleTo (SampleRecord sample, long authuserid) throws AppException, Exception {
        //---------------------------------------------------------------------
        if (!sample.checkValidData())
            throw new AppException("Invalid or incomplet data submited", AppException.INVALIDDATASUBMITED);
        //---------------------------------------------------------------------
        BaseForm formdef = surveylambda.getVarCluster(sample.getSurveyId());
        if (authuserid != formdef.getOwner())
            throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        sample.setOwner(formdef.getOwner());
        sample.setFormDefTitle(formdef.getTitle());
        //---------------------------------------------------------------------
        long commituserid = authlambda.getUserIdByIdentifier(sample.getUserName());
        sample.setUserId(commituserid);
        //---------------------------------------------------------------------
        samplelambda.createCommitSampleTo(sample);
        //---------------------------------------------------------------------
    }
    //***********************************************************************
    /**
     * Fetch Samples commited to a given user.
     * Only returns Samples associated to valid clusters and that are in progress.
     * @param userid
     * @return An Array of Samples
     * @throws Exception 
     */
    public SampleRecord[] fetchCommitedSamples (long userid) throws Exception {
        SampleRecord[] samples = samplelambda.getSamplesByUser(userid);
        List<SampleRecord> validsamples = new ArrayList<>();
        for (SampleRecord sample : samples) {
            try { surveylambda.getVarCluster(sample.getSurveyId()); }
            catch (AppException e) { continue; }
            sample.setRespCount(responselambda.getResponsesCount(sample.getSampleId()));
            validsamples.add(sample);
        }
        return validsamples.toArray(new SampleRecord[0]);
    }
    //***********************************************************************
    public SampleRecord[] getSampleBySurvey (long surveyid) throws Exception {
        SampleRecord[] samples = samplelambda.getSamplesBySurvey(surveyid);
        User user;
        for (SampleRecord sample : samples) {
            try { user = authlambda.getUser(sample.getUserId(), false); }
            catch (AppException e) { continue; }
            sample.setUserName(user.loginName());
        }
        return samples;
    }
    //=======================================================================
    public SampleRecord[] getSamplesByOwner (long userid) throws Exception {
        SampleRecord[] samples = samplelambda.getSamplesByOwner(userid);
        User user;
        for (SampleRecord sample : samples) {
            try { user = authlambda.getUser(sample.getUserId(), false); }
            catch (AppException e) { continue; }
            sample.setRespCount(responselambda.getResponsesCount(sample.getSampleId()));
            sample.setUserName(user.loginName());
        }
        return samples;
    }
    //***********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException SAMPLENOTFOUND, SURVEYNOTFOUND, UNAUTHORIZED
     * @throws Exception 
     */
    public FieldInputForm getSampleForm (long sampleid, long userid) throws AppException, Exception {
        //--------------------------------------------------------------
        FieldInputForm form = new FieldInputForm();
        SampleRecord sample = samplelambda.getSample(sampleid);
        if (userid != 0)
            if (userid != sample.getUserId())
                throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        form.title = sample.getTitle();
        form.brief = sample.getTask();
        //--------------------------------------------------------------
        BaseForm survey = surveylambda.getVarCluster(sample.getSurveyId());
        //--------------------------------------------------------------
        form.sampleid = sampleid;
        form.surveyid = survey.getID();
        //--------------------------------------------------------------
        VarPointer[] pointers = surveylambda.getItemPointers(survey.getID());
        BaseToBeNamed item;
        for (VarPointer pointer : pointers) {
            switch (pointer.getType()) {
                //------------------------------------------------------
                case VarPointer.ITEMTYPE_PUBIMAGE:
                    try { item = pubviewlambda.getCandidate(pointer.getItemId()); }
                    catch (AppException e) { continue; }
                    form.addItem(VarPointer.ITEMTYPE_PUBIMAGE, pointer.getItemId(), item);
                    continue;
                //------------------------------------------------------
                default: continue;
            }
        }
        //--------------------------------------------------------------
        return form;
        //--------------------------------------------------------------
    }
    //***********************************************************************
}
//***************************************************************************

