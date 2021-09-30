package lycine.trial.build;
//************************************************************************
import lycine.stats.StatSubset;
//************************************************************************
/**
 * This class holds the stats for a given subset's children. Meaning....
 * holds the StatSubsets for every child according to the sample assigned to that child subset.
 * StatSubsets are the hol
 * @author marianoscardaccione
 */
public class StatsHolder {
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
    private StatSubset[] statsubsets = new StatSubset[0];
    private StatSubset foundstat = null;
    void addStatHold (StatSubset stathold) {
        if (stathold == null) return;
        StatSubset[] newarr = new StatSubset[statsholdcount + 1];
        System.arraycopy(statsubsets, 0, newarr, 0, statsholdcount);
        newarr[statsholdcount] = stathold;
        statsubsets = newarr;
        statsholdcount++;
    }
    public StatSubset[] getStatHolds () { return statsubsets; }
    //=========================================================
    public boolean findStat (long subsetid) {
        for (StatSubset stat : statsubsets) {
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
