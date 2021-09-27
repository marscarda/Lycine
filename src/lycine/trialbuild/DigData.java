package lycine.trialbuild;
//************************************************************************
import lycine.stats.StatHold;
import threonine.universe.SubSet;
//************************************************************************
public class DigData {
    //*************************************************
    private SubSet subset = null;
    void setSubset (SubSet subset) {
        this.subset = subset;
        sbsetpop = this.subset.getPopulation();
    }
    //=================================================
    int sbsetpop = 0; //The population of the eval subset.
    int chldsbsetpopsum = 0; //The sum of the children subsets population
    //=================================================
    long subsetID () {
        if (subset == null) return 0; 
        return subset.getSubsetID();
    }
    //=================================================
    public void addChildrenPopulation (int pop) { 
        chldsbsetpopsum += pop;
        if (chldsbsetpopsum > sbsetpop) sbsetpop = chldsbsetpopsum;
    }
    //=================================================
    public int subsetPopulation () { return sbsetpop; }
    public int sbusetPopulationChildren () { return chldsbsetpopsum; }
    public int populationGap () { 
        if (chldsbsetpopsum > sbsetpop) return  0;
        return sbsetpop - chldsbsetpopsum;
    }
    //*************************************************
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
    //*************************************************
}
//************************************************************************
