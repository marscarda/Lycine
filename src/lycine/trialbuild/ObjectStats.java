package lycine.trialbuild;
//************************************************************************
import lycine.stats.StatSubset;
//************************************************************************
public class ObjectStats {
    //**********************************************************
    int thispopulation = 0; //The population of the eval subset.
    int childrenpopulation = 0; //The sum of the children subsets population
    //**********************************************************
    public void addChildPopulation (int pop) { 
        childrenpopulation += pop;
        if (childrenpopulation > thispopulation) thispopulation = childrenpopulation;
    }
    public void setThisPopulation (int pop) { thispopulation = pop; }
    //==========================================================
    public int thisPopulation () { return thispopulation; }
    public int childrenPopulation () { return childrenpopulation; }
    public int populationGap () { 
        if (childrenpopulation > thispopulation) return  0;
        return thispopulation - childrenpopulation;
    }
    //**********************************************************
    private int statsholdcount = 0;
    private StatSubset[] statholds = new StatSubset[0];
    private StatSubset foundstat = null;
    void addStatHold (StatSubset stathold) {
        if (stathold == null) return;
        StatSubset[] newarr = new StatSubset[statsholdcount + 1];
        System.arraycopy(statholds, 0, newarr, 0, statsholdcount);
        newarr[statsholdcount] = stathold;
        statholds = newarr;
        statsholdcount++;
    }
    public StatSubset[] getStatHolds () { return statholds; }
    //=========================================================
    public boolean findStat (long subsetid) {
        for (StatSubset stat : statholds) {
            if (stat.subsetID() == subsetid) {
                foundstat = stat;
                return true;
            }
        }
        return false;
    }
    //=========================================================
    public StatSubset getStat () { return foundstat; }
    //**********************************************************
}
//************************************************************************
