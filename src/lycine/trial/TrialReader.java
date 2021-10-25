package lycine.trial;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import threonine.universe.SubSet;
import threonine.universe.UniverseErrorCodes;
import tryptophan.trial.StatNode;
import tryptophan.trial.Trial;
//************************************************************************
public class TrialReader {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAurigaObject (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    private Trial trial = null;
    //********************************************************************
    public void initTrial (long trialid, long userid) throws AppException, Exception {
        trial = auriga.getTrialAtlas().getTrial(trialid);
        auriga.getProjectLambda().checkAccess(trial.projectID(), userid, 1);
    }
    //********************************************************************
    /**
     * 
     * @return 
     */
    public Trial getTrial () { return trial; }
    //********************************************************************
    public StatNodeView getStatNode (long nodecode) throws AppException, Exception {
        //=============================================================
        StatNodeView nodeview = new StatNodeView();
        //=============================================================
        //If this is the root node. We just return a root node
        if (nodecode == 0) {
            nodeview.setROOT();
            return nodeview;
        }
        //=============================================================
        StatNode node = auriga.getNewAtlas().getStatNode(trial.getID(), nodecode);
        nodeview.statnode = node;
        try { nodeview.subset = auriga.getUniverseAtlas().getSubset(trial.universeID(), node.subsetID()); }
        catch (AppException e) {
            if (e.getErrorCode() != UniverseErrorCodes.SUBSETNOTFOUND) throw e;
            nodeview.subset = new SubSet();
        }
        //=============================================================
        return nodeview;
        //=============================================================
    }
    //********************************************************************
    /**
     * 
     * @param nodecode //The parent code of the nodes requested.
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public StatNodeView[] getStatNodes (long nodecode) throws AppException, Exception {
        //****************************************************************
        StatNode[] nodes = auriga.getTrialAtlas().getStatNodes(trial.getID(), nodecode);
        int count = nodes.length;
        StatNodeView[] nodeviews = new StatNodeView[count];
        for (int n = 0; n < count; n++) {
            nodeviews[n] = new StatNodeView();
            nodeviews[n].statnode = nodes[n];
            try { nodeviews[n].subset = auriga.getUniverseAtlas().getSubset(trial.universeID(), nodes[n].subsetID()); }
            catch (AppException e) {
                if (e.getErrorCode() != UniverseErrorCodes.SUBSETNOTFOUND) throw e;
                nodeviews[n].subset = new SubSet();
            }
        }
        //================================================================
        return nodeviews;
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
