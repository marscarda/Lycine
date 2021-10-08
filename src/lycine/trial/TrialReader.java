package lycine.trial;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import threonine.universe.SubSet;
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
    /**
     * 
     * @param parentsubset
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public StatNodeView[] getStatNodes (long parentsubset) throws AppException, Exception {
        //****************************************************************
        StatNode[] nodes = auriga.getTrialAtlas().getStatNodes(trial.getID(), parentsubset);
        int count = nodes.length;
        StatNodeView[] nodeviews = new StatNodeView[count];
        for (int n = 0; n < count; n++) {
            nodeviews[n] = new StatNodeView();
            nodeviews[n].statnode = nodes[n];
            try {
                nodeviews[n].subset = auriga.getUniverseAtlas().getSubset(trial.universeID(), nodes[n].subsetID());
            }
            catch (AppException e) {
                
            }
        }
        //================================================================
        return nodeviews;
        //****************************************************************
    }
    //********************************************************************
    public StatNodeView getStatNode (long parent) throws AppException, Exception {
        
        
        
        
        /*
        StatNode node = auriga.getTrialAtlas().getStatNode(trial.getID(), subsetid);
        StatNodeView nodeview = new StatNodeView();
        try {
            nodeview.subset = auriga.getUniverseAtlas().getSubset(trial.universeID(), node.subsetID());
        }
        catch (AppException e) {
            
        }
        //================================================================
        return nodeview;
        //****************************************************************
        */
        
        
        
        
        return null;
        
    }
    //********************************************************************
}
//************************************************************************
