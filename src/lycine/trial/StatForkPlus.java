package lycine.trial;
//************************************************************************
import threonine.universe.SubSet;
import tryptophan.trial.StatLayer;
//************************************************************************
public class StatForkPlus {
    //***********************************************************
    StatLayer statfork = null;
    public StatLayer getStatFork () {
        if (statfork == null) return new StatLayer();
        return statfork;
    }
    //***********************************************************
    SubSet subset = null;
    public SubSet getSubset () {
        if (subset == null) return new SubSet();
        return subset;
    }
    //***********************************************************
}
//************************************************************************
