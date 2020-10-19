package lycine.samplemetric;
//***************************************************************************
import tryptophan.survey.sampling.SampleRecord;
//***************************************************************************
public class ResultVector {
    //***********************************************************************
    SampleRecord sample = null;
    //=======================================================
    public void setSampleRecord (SampleRecord sample) { this.sample = sample; }
    public SampleRecord getSampleRecord () {
        if (sample == null) return new SampleRecord();
        return sample;
    }
    //***********************************************************************
    Response[] responses = null;
    int responsecount = 0;
    //=======================================================================
    public void setResponses (Response[] responses) {
        this.responses = responses; 
        responsecount = responses.length;
    }
    //=======================================================================
    public int responseCount () { return responsecount; }
    //-----------------------------------------------------------------------
    public Response[] getResponses () {
        if (responses == null) return new Response[0];
        return responses;
    }
    //***********************************************************************
    int reactmetriccount = 0;
    ReactionMetric[] reactionmetrics = new ReactionMetric[0];
    //=======================================================================
    boolean checkReactionMetric (int itemtype, long itemid) {
        //========================================================
        for (ReactionMetric reaction : reactionmetrics) {
            if (reaction.itemType() == itemtype &&
                reaction.getItemID() == itemid) return true;
        }
        //========================================================
        return false;
        //========================================================
    }
    //=======================================================================
    public void addReactionMetric (ReactionMetric reactionmet) {
        //========================================================
        ReactionMetric[] newarr = new ReactionMetric[reactmetriccount + 1];
        System.arraycopy(reactionmetrics, 0, newarr, 0, reactmetriccount);
        newarr[reactmetriccount] = reactionmet;
        reactmetriccount++;
        reactionmetrics = newarr;
        //========================================================
    }
    //***********************************************************************
    public ReactionMetric[] getReactionMetrics () {
        if (reactionmetrics == null) return new ReactionMetric[0];
        return reactionmetrics;
    }
    //***********************************************************************
}
//***************************************************************************
