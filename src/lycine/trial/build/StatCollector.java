package lycine.trial.build;
//************************************************************************

//************************************************************************
/**
 * @author marianoscardaccione
 */
public class StatCollector {
    //*******************************************************
    long universeid = 0;
    long subsetid = 0;
    //=======================================================
    int population = 0;
    int childpopulation = 0;
    //*******************************************************
    int childrencount = 0;
    StatCollector[] children = null;
    //=======================================================
    public void addChild (StatCollector collector) {
        StatCollector[] newarr = new StatCollector[childrencount + 1];
        System.arraycopy(children, 0, newarr, 0, childrencount);
        newarr[childrencount] = collector;
        children = newarr;
        childrencount++;
    }
    //*******************************************************
}
//************************************************************************
