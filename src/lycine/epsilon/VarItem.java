package lycine.epsilon;
//*************************************************************************
import tryptophan.survey.ActionItemBase;
//*************************************************************************
public class VarItem {
    //*******************************************************
    int itemtype = 0;
    long itemid = 0;
    ActionItemBase item = null;
    //*******************************************************
    public int getType () { return itemtype; }
    public long getItemId () { return itemid; }
    public ActionItemBase getItem () {
        if (item == null) return new ActionItemBase();
        return item;
    }
    //*******************************************************
}
//*************************************************************************
