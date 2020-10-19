package lycine.samplemetric;
//***************************************************************************
import tryptophan.survey.reaction.ResponseRecord;
import tryptophan.survey.reaction.ReactionItem;
//***************************************************************************
public class Response {
    //************************************************************
    private ResponseRecord resprec = null;
    private ReactionItem[] reactions = new ReactionItem[0];
    private int reactcount = 0;
    //************************************************************
    void setResponseRecord (ResponseRecord resp) { resprec = resp; }
    void setResponseRows (ReactionItem[] rows) { 
        if (rows == null) return;
        reactions = rows;
        reactcount = reactions.length;
    }
    //************************************************************
    int itemCount () { return reactcount; }
    //============================================================
    ReactionItem[] getReactionRecords () {
        if (reactions == null) return new ReactionItem[0];
        return reactions;
    }
    //************************************************************
}
//***************************************************************************

