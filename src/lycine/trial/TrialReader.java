package lycine.trial;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import threonine.universe.SubSet;
import tryptophan.trial.StatLayer;
import tryptophan.trial.Trial;
//************************************************************************
public class TrialReader {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAurigaObject (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public Trial getTrial (long trialid, long userid) {
        return null;
    }
    //********************************************************************
    /**
     * 
     * @param trialid
     * @param parentsubset
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public StatForkPlus[] getStartForks (long trialid, long parentsubset, long userid) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        Trial trial = auriga.getTrialAtlas().getTrial(trialid);
        auriga.getProjectLambda().checkAccess(trial.projectID(), userid, 1);
        //****************************************************************
        StatLayer[] forks = auriga.getTrialAtlas().getStatForks(trialid, parentsubset);
        int count = forks.length;
        StatForkPlus[] forkplus = new StatForkPlus[count];
        for (int n = 0; n < count; n++) {
            forkplus[n] = new StatForkPlus();
            forkplus[n].statfork = forks[n];
        }
        //****************************************************************
        return forkplus;
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
