package lycine.tobenamed;
//***************************************************************************
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import tryptophan.survey.baseform.ActionItemType;
import tryptophan.survey.publicview.PVCandidate;
import tryptophan.survey.publicview.PublicViewLambda;
import tryptophan.survey.responses.ResponseDB;
import tryptophan.survey.responses.ResponseLambda;
import tryptophan.survey.responses.ReactionDB;
import tryptophan.survey.sampling.SampleLamda;
import tryptophan.survey.sampling.SampleRecord;
//***************************************************************************
public class CenterTBN {
    //***********************************************************************
    SampleLamda samplelambda = null;
    ResponseLambda resplamda = null;
    PublicViewLambda actionlambda = null;
    //-----------------------------------------------------------------------
    ResultVector resultvector = null;
    //=======================================================================
    public void setSampleLambda (SampleLamda samplelambda) { this.samplelambda = samplelambda; }
    public void setResponseLambda (ResponseLambda resplambda) { this.resplamda = resplambda; }
    public void setActionLambda (PublicViewLambda actionlambda) { this.actionlambda = actionlambda; }
    //***********************************************************************
    public void prepareSampleVector (long sampleid) throws AppException, Exception {
        //===================================================================
        resultvector = new ResultVector();
        //===================================================================
        SampleRecord samplerec = samplelambda.getSample(sampleid);
        resultvector.setSampleRecord(samplerec);
        //===================================================================
        //We Load Up the reactions.
        ResponseDB[] resprecords = resplamda.getResponseObjects(sampleid, true);
        List<Response> respaddlist = new ArrayList<>();
        Response response;
        for (ResponseDB resprec : resprecords) {
            //-------------------------------------------------------------
            response = new Response();
            response.setResponseRecord(resprec);
            ReactionDB[] rows = resplamda.getResponseRows(resprec.getID());
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
        ReactionDB[] reactions = response.getReactionRecords();
        for (ReactionDB reaction : reactions)
            setReactionMetric(reaction);
    }
    //***********************************************************************
    private void setReactionMetric (ReactionDB reactiondb) throws Exception {
        ReactionMetric reactionmet;
        boolean ext = resultvector.checkReactionMetric(reactiondb.getType(), reactiondb.getItemid());
        if (!resultvector.checkReactionMetric(reactiondb.getType(), reactiondb.getItemid())) {
            reactionmet = createReactionMetric(reactiondb);
            resultvector.addReactionMetric(reactionmet);
        }
        else {
            //Lets get the reaction met here.
        }
    }
    //*****************************************************
    private ReactionMetric createReactionMetric (ReactionDB reaction) throws Exception {
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
    //*****************************************************

    //***********************************************************************
    public ResultVector getResultVector () {
        if (resultvector == null) return new ResultVector();
        return resultvector;
    }
    //***********************************************************************
}
//***************************************************************************

