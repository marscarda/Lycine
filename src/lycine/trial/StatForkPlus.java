package lycine.trial;
//************************************************************************
import threonine.universe.SubSet;
import tryptophan.trial.StatNode;
//************************************************************************
public class StatForkPlus {
    //***********************************************************
    StatNode statfork = null;
    public StatNode getStatFork () {
        if (statfork == null) return new StatNode();
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
