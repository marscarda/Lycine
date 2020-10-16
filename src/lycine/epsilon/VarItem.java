package lycine.epsilon;
//*************************************************************************
import tryptophan.survey.BaseToBeNamed;
//*************************************************************************
public class VarItem {
    //*******************************************************
    int itemtype = 0;
    long itemid = 0;
    BaseToBeNamed item = null;
    //*******************************************************
    public int getType () { return itemtype; }
    public long getItemId () { return itemid; }
    public BaseToBeNamed getItem () {
        if (item == null) return new BaseToBeNamed();
        return item;
    }
    //*******************************************************
}
//*************************************************************************
