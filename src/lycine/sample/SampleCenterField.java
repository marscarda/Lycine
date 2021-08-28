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
        FormQuestion[] questions = designlambda.getFormQuestions(form.questionaryID());
        return questions;
    }
    //********************************************************************
}
//************************************************************************
