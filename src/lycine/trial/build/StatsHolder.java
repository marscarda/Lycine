package lycine.trial.build;
//************************************************************************
import lycine.stats.StatSubset;
//************************************************************************
/**
 * This class holds the stats for a given subset's children. Meaning....
 * holds the StatSubsets for every child subset having a sample assigned.
 * It also holds the population of the subset. The population "declared" and the sum of its children as well.
 * @author marianoscardaccione
 */
public class StatsHolder {
    //**********************************************************
    private int thispopulation = 0; //The population of declared for the current subset.
    private int childrenpopulation = 0; //The sum of the children subsets population
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
    //=========================================================
    /**
     * Adds a stat subset to hold. 
     * @param statsubset
     */
    public void addStat (StatSubset statsubset) {
        if (statsubset == null) return;
        StatSubset[] newarr = new StatSubset[statsholdcount + 1];
        System.arraycopy(statsubsets, 0, newarr, 0, statsholdcount);
        newarr[statsholdcount] = statsubset;
        statsubsets = newarr;
        statsholdcount++;
    }
    //=========================================================
    /**
     * Returns the stat subsets that are held.
     * @return 
     */
    public StatSubset[] getStats () { return statsubsets; }
    //=========================================================
    /**
     * Finds a stat subset by subset id and if found leaves it available to retrieve.
     * @param subsetid
     * @return 
     */
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
    /**
     * Returns the found subset.
     * findStats must be called first.
     * @return 
     */
    public StatSubset getStat () { return foundstat; }
    //**********************************************************
}
//************************************************************************
