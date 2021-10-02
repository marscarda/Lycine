package lycine.stats;
//************************************************************************
/**
 * Holds variable stats.
 * See class that is inherited where the stats are actually held.
 * This subclass also holds the subset id where the sample is assigned.
 * Typically used when we calculate globally going deep into a universe.
 * @author marianoscardaccione
 */
public class StatSubset {
    long subsetid = 0;
    public void setSubsetId (long subsetid) { this.subsetid = subsetid; }
    public long subsetID () { return subsetid; }
}
//************************************************************************
