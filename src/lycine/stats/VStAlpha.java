package lycine.stats;
//************************************************************************
/**
 *
 * @author marianoscardaccione
 */
public class VStAlpha {
    //*******************************************
    public long variableid = 0;
    public int variabletype = 0;
    public void setVariableId (long variableid) { this.variableid = variableid; }
    public long variableID () { return variableid; }
    public int variableType () { return variabletype; }
    String label = null;
    //*******************************************
    public void setLabel (String label) { this.label = label; }
    public String getLabel () {
        if (label == null) return "";
        return label;
    }
    //*******************************************
}
//************************************************************************
