package lycine.trialbuild;
//************************************************************************
import threonine.universe.SubSet;
//************************************************************************
public class DiginData {
    //=================================================
    private SubSet subset = null;
    void setSubset (SubSet subset) {
        this.subset = subset;
        sbstpop = this.subset.getPopulation();
    }
    //=================================================
    int sbstpop = 0; //The population of the eval subset.
    int sbstpopchlsum = 0; //The sum of the children subsets population
    //=================================================
    long subsetID () {
        if (subset == null) return 0; 
        return subset.getSubsetID();
    }
    //=================================================
    public void addChildrenPopulation (int pop) { 
        sbstpopchlsum += pop;
        if (sbstpopchlsum > sbstpop) sbstpop = sbstpopchlsum;
    }
    //=================================================
    public int subsetPopulation () { return sbstpop; }
    public int sbusetPopulationChildren () { return sbstpopchlsum; }
    public int populationGap () { 
        if (sbstpopchlsum > sbstpop) return  0;
        return sbstpop - sbstpopchlsum;
    }
    //=================================================
    
}
//************************************************************************
