package lycine.sample;
//************************************************************************
import methionine.AppException;
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
}
//************************************************************************
