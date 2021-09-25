package lycine.viewmake;
//************************************************************************

//************************************************************************
public class VarStatAlpha {
    //*******************************************
    public long variableid = 0;
    public int variabletype = 0;
    String label = null;
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
