package lycine.responsesrc;
//****************************************************************************
import tryptophan.survey.baseform.VarPointer;
import tryptophan.survey.responses.ResponseRow;
import tryptophan.survey.sampling.SampleRecord;
//****************************************************************************
public class FutureSample {
    //************************************************************************
    long sampleid = 0;
    SampleRecord samplerecord = null;
    ResponseSubject[] responses = null;
    int respcount = 0;
    //------------------------------------------------------------
    ItemVarStat[] itemvars = null;
    int varcount = 0;
    //************************************************************************
    void setSampleRecord(SampleRecord srec) { samplerecord = srec; }
    public SampleRecord getSampleRecord () {
        if (samplerecord == null) return new SampleRecord();
        return samplerecord;
    }
    //************************************************************************
    void addResponse (ResponseSubject response) {
        ResponseSubject[] newarr = new ResponseSubject[respcount + 1];
        if (responses != null)
            System.arraycopy(responses, 0, newarr, 0, respcount);
        newarr[respcount] = response;
        respcount++;
        responses = newarr;
    }
    //************************************************************************
    void loopResponses () {
        for (int n = 0; n < respcount; n++)
            loopResponseItems(responses[n]);
    }
    //************************************************************
    private void loopResponseItems (ResponseSubject resp) {
        //========================================================
        ResponseRow[] rows = resp.responseItems();
        int count = resp.itemCount();
        //========================================================
        for (int n = 0; n < count; n++) {
            resolveItem(rows[n]);
        }
        //========================================================
    }
    //************************************************************
    /**
     * This method adds an item if
     * @param respitem
     * @return 
     */
    private ItemVarStat resolveItem (ResponseRow respitem) {
        //========================================================
        ItemVarStat itemvar = null;
        for (int n = 0; n < varcount; n++) {
            itemvar = itemvars[n];
            if (itemvar.vartype == respitem.getType() && itemvar.itemid == respitem.getItemid()) {
                return itemvar;
            }
        }
        //========================================================
        //If we are here is due we did not find the item.
        switch (respitem.getType()) {
            case VarPointer.ITEMTYPE_PUBIMAGE: itemvar = new PublicViewVarStat(); break;
        }
        //========================================================
        ItemVarStat[] newarr = new ItemVarStat[varcount + 1];
        if (itemvars != null)
            System.arraycopy(itemvars, 0, newarr, 0, varcount);
        newarr[respcount] = itemvar;
        respcount++;
        itemvars = newarr;
        //========================================================
        return itemvar;
        //========================================================
    }
    //************************************************************
    
    
    //************************************************************************
    public ItemVarStat[] getItems () {
        if (itemvars == null) return new ItemVarStat[0];
        return itemvars;
    }
    //************************************************************************
}
//****************************************************************************
