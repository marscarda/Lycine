package lycine.stats.sample;
//************************************************************************


//************************************************************************
public class VStSmplAlphaFix {
    //*******************************************
    public long variableid = 0;
    public int variabletype = 0;
    String label = null;
    //*******************************************
    public void setLabel (String label) { this.label = label; }
    //*******************************************
    public long variableID () { return variableid; }
    public int variableType () { return variabletype; }
    //===========================================
    public String getLabel () {
        if (label == null) return "";
        return label;
    }
    //*******************************************
}
//************************************************************************
