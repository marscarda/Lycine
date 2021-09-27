package lycine.trialbuild;
//************************************************************************
import lycine.stats.StatHold;
import threonine.universe.SubSet;
//************************************************************************
public class DigData {
    //*************************************************
    private SubSet subset = null;
    void setSubset (SubSet subset) { this.subset = subset; }
    long subsetID () {
        if (subset == null) return 0; 
        return subset.getSubsetID();
    }
    public SubSet getSubset () {
        if (subset == null) return subset;
        return subset;
    }
    //*************************************************
    private SubSet[] childrensubset = null;
    void setChildrenSubsets (SubSet[] subsets) { childrensubset = subsets; }
    public SubSet[] getChildrenSubsets () {
        if (childrensubset == null) return new SubSet[0];
        return childrensubset;
    }
    //*************************************************
}
//************************************************************************
