package lycine.trialbuild;
//************************************************************************
import lycine.stats.VarStatAlpha;
//************************************************************************
public class ChildContQQ {
    //********************************************************************
    int varstatcount = 0;
    private VarStatAlpha[] varstats = new VarStatAlpha[0];
    //********************************************************************
    /**
     * Checks if a variable exists.
     * @param variableid
     * @return 
     */
    boolean checkVariable (long variableid) {
        //========================================================
        for (VarStatAlpha var : varstats) {
            if (var.variableID() == variableid) return true;
        }
        //========================================================
        return false;
    }
    //********************************************************************
    /**
     * Returns an existent variable stat in the [placeholder]
     * If not present returns null
     * You better call checkVariable before calling this method.
     * @param variableid
     * @return 
     */
    VarStatAlpha getVariable (long variableid) {
        //========================================================
        for (VarStatAlpha var : varstats) {
            if (var.variableID() == variableid)
                return var;
        }
        //========================================================
        return null;
    }
    //********************************************************************
    /**
     * Adds a new variable stat to the [placeholder].
     * @param var 
     */
    void addVariableStat (VarStatAlpha var) {
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
