package lycine.tobenamed;
//***************************************************************************
import tryptophan.survey.responses.ResponseDB;
import tryptophan.survey.responses.ReactionDB;
//***************************************************************************
public class Response {
    //************************************************************
    private ResponseDB resprec = null;
    private ReactionDB[] reactions = new ReactionDB[0];
    private int reactcount = 0;
    //************************************************************
    void setResponseRecord (ResponseDB resp) { resprec = resp; }
    void setResponseRows (ReactionDB[] rows) { 
        if (rows == null) return;
        reactions = rows;
        reactcount = reactions.length;
    }
    //************************************************************
    int itemCount () { return reactcount; }
    //============================================================
    ReactionDB[] getReactionRecords () {
        if (reactions == null) return new ReactionDB[0];
        return reactions;
    }
    //************************************************************
}
//***************************************************************************

