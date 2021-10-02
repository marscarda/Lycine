package lycine.trial.build;
//************************************************************************
import lycine.stats.StatSubset;
import threonine.universe.SubSet;
//************************************************************************
/**
 * @author marianoscardaccione
 */
public class StatNester {
    //*******************************************************
    //long universeid = 0;
    //long subsetid = 0;
    //=======================================================
    //int population = 0;
    //int childpopulation = 0;
    //*******************************************************
    private SubSet subset = null;
    public void setSubset (SubSet subset) { this.subset = subset; }
    public SubSet getSubset () { return subset; }
    //=======================================================
    public long subsetID () { return subset.getSubsetID(); }
    //*******************************************************
    private StatSubset stat = null;
    public void setStat (StatSubset stat) { this.stat = stat; }
    public boolean hasStat () { return stat != null; }
    public StatSubset getStat () throws Exception {
        if (stat == null) throw new Exception("Stat not available. Check hasStat first");
        return stat;
    }
    //*******************************************************
    int childrencount = 0;
    StatNester[] children = new StatNester[0];
    //=======================================================
    public void addChild (SubSet subset) {
        StatNester nester = new StatNester();
        nester.subset = subset;
        addChild(nester);
    }
    //=======================================================
    public void addChild (StatNester collector) {
        StatNester[] newarr = new StatNester[childrencount + 1];
        System.arraycopy(children, 0, newarr, 0, childrencount);
        newarr[childrencount] = collector;
        children = newarr;
        childrencount++;
    }
    //=======================================================
    public StatNester[] getChildren () { return children; }
    //*******************************************************
}
//************************************************************************
