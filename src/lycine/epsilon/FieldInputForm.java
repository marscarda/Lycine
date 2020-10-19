package lycine.epsilon;
//***************************************************************************
import tryptophan.survey.ActionItemBase;
//***************************************************************************
public class FieldInputForm {
    //***********************************************************************
    long sampleid = 0;
    long surveyid = 0;
    int itemcount = 0;
    String title = null;
    String brief = null;
    VarItem[] items = null;
    //***********************************************************************
    public void addItem (int type, long id, ActionItemBase item) {
        //-------------------------------------------------------------------
        VarItem newitem = new VarItem();
        newitem.itemtype = type;
        newitem.itemid = id;
        newitem.item = item;
        //-------------------------------------------------------------------
        VarItem[] newarray = new VarItem[itemcount + 1];
        if (items != null)
            System.arraycopy(items, 0, newarray, 0, itemcount);
        newarray[itemcount] = newitem;
        items = newarray;
        itemcount++;
        //-------------------------------------------------------------------
    }
    //=======================================================================
    public int getItemsCount () { return itemcount; }
    //-----------------------------------------------------------------------
    public VarItem[] getItems () {
        if (items == null) return new VarItem[0];
        return items;
    }
    //-----------------------------------------------------------------------
    public String getTitle () {
        if (title == null) return "";
        return title;
    }
    //-----------------------------------------------------------------------
    public String getBrief () {
        if (brief == null) return "";
        return brief;
    }
    //***********************************************************************
}
//***************************************************************************
