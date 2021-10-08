package lycine.trial;
//************************************************************************
import threonine.universe.SubSet;
import tryptophan.trial.StatNode;
//************************************************************************
public class StatNodeView {
    //***********************************************************
    StatNode statnode = null;
    public StatNode getStatFork () {
        if (statnode == null) return new StatNode();
        return statnode;
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
