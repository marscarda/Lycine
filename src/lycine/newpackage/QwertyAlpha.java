package lycine.newpackage;
//*************************************************************************
public class QwertyAlpha {
    //***********************************************************************
    protected QwertyAlpha () {}
    //=======================================================================
    int itemtype = 0;
    long itemid = 0;
    String label = null;
    float value = 0;
    //***********************************************************************
    public int itemType () { return itemtype; }
    public long itemID () { return itemid; }
    //=======================================================================
    public String getLabel () {
        if (label == null) return "";
        return label;
    }
    //***********************************************************************
}
//*************************************************************************




