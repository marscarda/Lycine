package lycine.trialbuild;
//************************************************************************

import lycine.stats.StatHold;


/**
 *
 * @author marianoscardaccione
 */
//************************************************************************
public class SubsetStat {
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
    private StatHold[] statholds = new StatHold[0];
    void addStatHold (StatHold stathold) {
        if (stathold == null) return;
        StatHold[] newarr = new StatHold[statsholdcount + 1];
        System.arraycopy(statholds, 0, newarr, 0, statsholdcount);
        newarr[statsholdcount] = stathold;
        statholds = newarr;
        statsholdcount++;
    }
    //**********************************************************
}
//************************************************************************
