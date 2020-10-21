package lycine.samplemetric;
//***************************************************************************
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import tryptophan.survey.ActionItemType;
import tryptophan.survey.publicview.PVCandidate;
import tryptophan.survey.publicview.PublicViewLambda;
import tryptophan.survey.reaction.ResponseRecord;
import tryptophan.survey.reaction.ReactionLambda;
import tryptophan.survey.reaction.ReactionItem;
import tryptophan.survey.sampling.SampleLamda;
import tryptophan.survey.sampling.SampleRecord;
//***************************************************************************
public class SampleMetricBuilder {
    //***********************************************************************
    AuthLamda authlambda = null;
    SampleLamda samplelambda = null;
    ReactionLambda resplamda = null;
    PublicViewLambda actionlambda = null;
    //-----------------------------------------------------------------------
    ResultVector resultvector = null;
    //=======================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setSampleLambda (SampleLamda samplelambda) { this.samplelambda = samplelambda; }
    public void setReactionLambda (ReactionLambda resplambda) { this.resplamda = resplambda; }
    public void setActionLambda (PublicViewLambda actionlambda) { this.actionlambda = actionlambda; }
    //***********************************************************************
    public void prepareSampleVector (long sampleid) throws AppException, Exception {
        //===================================================================
        resultvector = new ResultVector();
        //===================================================================
        SampleRecord samplerec = samplelambda.getSample(sampleid);
        try { 
            User user = authlambda.getUser(samplerec.getUserId(), false); 
            samplerec.setUserName(user.loginName());
        }
        catch (AppException e) {}
        resultvector.setSampleRecord(samplerec);
        //===================================================================
        //We Load Up the reactions.
        ResponseRecord[] resprecords = resplamda.getResponseObjects(sampleid, true);
        List<Response> respaddlist = new ArrayList<>();
        Response response;
        for (ResponseRecord resprec : resprecords) {
            //-------------------------------------------------------------
            response = new Response();
            response.setResponseRecord(resprec);
            ReactionItem[] rows = resplamda.getResponseRows(resprec.getID());
            response.setResponseRows(rows);
            respaddlist.add(response);
        }
        Response[] responses = respaddlist.toArray(new Response[0]);
        resultvector.setResponses(responses);
        //===================================================================
        for (Response r : responses)
            loopResponseReactions(r);
        //===================================================================
    }
    //***********************************************************************
    private void loopResponseReactions (Response response) throws Exception {
        ReactionItem[] reactions = response.getReactionRecords();
        for (ReactionItem reaction : reactions)
            setReactionMetric(reaction);
    }
    //***********************************************************************
    private void setReactionMetric (ReactionItem reactiondb) throws Exception {
        ReactionMetric reactionmet;
        //=================================================
        if (!resultvector.checkReactionMetric(reactiondb.getType(), reactiondb.getItemid())) {
            reactionmet = createReactionMetric(reactiondb);
            resultvector.addReactionMetric(reactionmet);
        }
        else reactionmet = resultvector.getReactionMetric(reactiondb.getType(), reactiondb.getItemid());
        //=================================================
        if (reactionmet == null) return;
        addMetricValue(reactionmet, reactiondb);
        //=================================================
    }
    //*****************************************************
    private ReactionMetric createReactionMetric (ReactionItem reaction) throws Exception {
        ReactionMetric reactionmet = null;
        //=================================================
        switch (reaction.getType()) {
            case ActionItemType.PUBIMAGE: reactionmet = new ReactionMetricPublicView(); break;
        }
        //=================================================
        switch (reaction.getType()) {
            case ActionItemType.PUBIMAGE: 
                try {
                    PVCandidate candidate = actionlambda.getCandidate(reaction.getItemid());
                    reactionmet.setItemId(reaction.getItemid());
                    reactionmet.setLabel(candidate.getLabel());
                }
                catch (AppException e) {
                    if (e.getErrorCode() != AppException.SURVEYNOTFOUND) return null;
                }
        }
        //=================================================
        return reactionmet;
        //=================================================
    }
    //***********************************************************************
    public ResultVector getResultVector () {
        if (resultvector == null) return new ResultVector();
        return resultvector;
    }
    //***********************************************************************
    private void addMetricValue (ReactionMetric reactionmet, ReactionItem reactionrec) {
        //==========================================================
        switch (reactionrec.getType()) {
            case ActionItemType.PUBIMAGE: {
                ReactionMetricPublicView met = (ReactionMetricPublicView)reactionmet;
                met.addReactionValue(reactionrec.getValue());
                break;
            }
        }
        //==========================================================
    }
    //***********************************************************************
}
//***************************************************************************

