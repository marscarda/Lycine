package lycine.epsilon;
//***************************************************************************
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import tryptophan.survey.reaction.ReactionItem;
//***************************************************************************
public class FieldCast {
    //==========================================================
    long sampleid = 0;
    long userid = 0;
    String responsestable = null;
    //==========================================================
    public void setSampleID (long sampleid) { this.sampleid = sampleid; }
    public void setUserID (long userid) { this.userid = userid; }
    public void setResponsesTable (String responsestable) { this.responsestable = responsestable; }
    //==========================================================
    public long sampleID () { return sampleid; }
    public long userID () { return userid; }
    //==========================================================
    /**
     * 
     * @return
     * @throws AppException INVALIDDATASUBMITED
     */
    public ReactionItem[] getSingleOptionsRows () throws AppException {
        //======================================================
        if (responsestable == null)
            throw new AppException("Invalid data submited", AppException.INVALIDDATASUBMITED);
        //======================================================
        String[] trows = responsestable.split("\\r?\\n");
        String[] cols;
        List<ReactionItem> rows = new ArrayList<>();
        ReactionItem row;
        for (String trow : trows) {
            cols = trow.split(",");
            //--------------------------------------------------
            if (cols.length < 3) 
                throw new AppException("Invalid data submited", AppException.INVALIDDATASUBMITED);
            //--------------------------------------------------
            row = new ReactionItem();
            try { row.setType(Integer.parseInt(cols[0])); } catch(Exception e) {}
            try { row.setItemID(Long.parseLong(cols[1])); } catch(Exception e) {}
            try { row.setValue(Integer.parseInt(cols[2])); } catch(Exception e) {}
            //--------------------------------------------------
            rows.add(row);
            //--------------------------------------------------
        }
        //======================================================
        return rows.toArray(new ReactionItem[0]);
        //======================================================
    }
    //==========================================================
}
//***************************************************************************
