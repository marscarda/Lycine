package lycine.stats;
//************************************************************************
import lycine.stats.sample.VStSmplAlpha;
//************************************************************************
/**
 * ROOT Class. 
 * Holds variable stats.
 * Variable stats can be added and retrieved by variable id.
 * Does not calculate stats. It simply holds the stats.
 * Stats are calculated outside this class.
 * @author marianoscardaccione
 */
public class StatAlpha {
    //********************************************************************
    int varstatcount = 0;
    protected VStAlpha[] varstats = new VStAlpha[0];
    //********************************************************************
    /**
     * Checks if a variable exists in the view.
     * @param variableid
     * @return 
     */
    public boolean checkVariable (long variableid) {
        //========================================================
        for (VStAlpha var : varstats) {
            if (var.variableid == variableid)
                return true;
        }
        //========================================================
        return false;
    }
    //********************************************************************
    /**
     * Returns an existent variable stat in the view
     * If not present returns null
     * You better call checkVariable before calling this method.
     * @param variableid
     * @return 
     */
    public VStAlpha getVariable (long variableid) {
        //========================================================
        for (VStAlpha var : varstats) {
            if (var.variableid == variableid)
                return var;
        }
        //========================================================
        return null;
    }
    //********************************************************************
    /**
     * Adds a new variable stat to the view.
     * @param var 
     */
    public void addVariableStat (VStAlpha var) {
        VStAlpha[] newarr = new VStAlpha[varstatcount + 1];
        System.arraycopy(varstats, 0, newarr, 0, varstatcount);
        newarr[varstatcount] = var;
        varstats = newarr;
        varstatcount++;
    }
    //********************************************************************
    public int varStatsCount () { return varstatcount; }
    public VStAlpha[] getVarStatistics () { return varstats; }
    //********************************************************************
}
//************************************************************************
