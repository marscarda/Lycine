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
    private int population = 0;
    private int childrenpop = 0;
    //=======================================================
    public int popSubset () { return population; }
    public int popGap () { return population - childrenpop; }
    //*******************************************************
    private SubSet subset = null;
    public void setSubset (SubSet subset) { 
        this.subset = subset;
        population = this.subset.getPopulation();
    }
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
    private int childrencount = 0;
    StatNester[] children = new StatNester[0];
    //=======================================================
    public void addChild (SubSet subset) {
        StatNester nester = new StatNester();
        nester.setSubset(subset);
        addChild(nester);
        childrenpop += subset.getPopulation();
        if (childrenpop > population) population = childrenpop;
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
    public int childStats () {
        int count = 0;
        for (StatNester nester : children) 
            if (nester.hasStat()) count++;
        return count;
    }
    //*******************************************************
}
//************************************************************************
