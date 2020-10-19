package lycine.samplemetric;
//****************************************************************************
public class ReactionMetric {
    //************************************************************
    protected int vartype = 0;
    private long itemid = 0;
    private String label = null;
    //************************************************************
    public void setItemType (int type) { vartype = type; }
    public void setItemId (long itemid) { this.itemid = itemid; }
    //------------------------------------------------------------
    public void setLabel (String label) { this.label = label; }
    //************************************************************
    public int itemType () { return vartype; }
    public long getItemID () { return itemid; }
    //------------------------------------------------------------
    public String getLabel() { 
        if (label == null) return "";
        return label;
    }
    //************************************************************
}
//****************************************************************************
