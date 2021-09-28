package lycine.stats;
//************************************************************************

import lycine.stats.sample.VarStatAlpha;

public class StatAlpha {
    //********************************************************************
    int varstatcount = 0;
    protected VarStatAlpha[] varstats = new VarStatAlpha[0];
    //********************************************************************
    /**
     * Checks if a variable exists in the view.
     * @param variableid
     * @return 
     */
    public boolean checkVariable (long variableid) {
        //========================================================
        for (VarStatAlpha var : varstats) {
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
    public VarStatAlpha getVariable (long variableid) {
        //========================================================
        for (VarStatAlpha var : varstats) {
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
    public void addVariableStat (VarStatAlpha var) {
        VarStatAlpha[] newarr = new VarStatAlpha[varstatcount + 1];
        System.arraycopy(varstats, 0, newarr, 0, varstatcount);
        newarr[varstatcount] = var;
        varstats = newarr;
        varstatcount++;
    }
    //********************************************************************
    public int varstatscount () { return varstatcount; }
    public VarStatAlpha[] getVarStatistics () { return varstats; }
    //********************************************************************
}
//************************************************************************
