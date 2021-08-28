package lycine.sample;
//************************************************************************
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import tryptophan.design.Form;
import tryptophan.design.FormQuestion;
import tryptophan.sample.Sample;
//************************************************************************
public class SampleCenterField extends SampleCenterPanel {
    //********************************************************************
    /**
     * Returns a list of samples given an entrusted user id
     * @param userid
     * @return A list of samples
     * @throws AppException
     * @throws Exception 
     */
    public Sample[] getUserActiveSamples (long userid) throws AppException, Exception {
        //****************************************************************
        Sample[] samples = samplelambda.getUserActiveSamples(userid);
        return samples;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Sample getSample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = samplelambda.getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        return sample;
    }
    //********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Form getFormBySample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = samplelambda.getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        Form form = designlambda.getQuestionnaire(sample.formID());
        return form;
    }
    //********************************************************************
    /**
     * 
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public FormQuestion[] getQuestionsBySample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = samplelambda.getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        Form form = designlambda.getQuestionnaire(sample.formID());
        FormQuestion[] questions = designlambda.getFormQuestions(form.formID());
        return questions;
    }
    //********************************************************************
    /**
     * Returns the final form including questions and vars ready to start survey.
     * Typically used to deliver the form to an app client.
     * @param sampleid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public FinalForm getFinalFormBySample (long sampleid, long userid) throws AppException, Exception {
        Sample sample = samplelambda.getSample(sampleid);
        if (sample.userID() != userid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        Form form = designlambda.getQuestionnaire(sample.formID());
        FormQuestion[] questions = designlambda.getFormQuestions(form.formID());
        FinalForm finalform = new FinalForm();
        finalform.form = form;
        finalform.questions = questions;
        return finalform;
    }
    //********************************************************************
}
//************************************************************************
