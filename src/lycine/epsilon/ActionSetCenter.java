package lycine.epsilon;
//***************************************************************************
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import methionine.project.WorkTeamLambda;
import tryptophan.survey.ActionItemBase;
import tryptophan.survey.publicview.PVCandidate;
import tryptophan.survey.action.ActionSetLambda;
import tryptophan.survey.publicview.PublicViewLambda;
import tryptophan.survey.action.ActionItemPointer;
//***************************************************************************
public class ActionSetCenter {
    //***********************************************************************
    ActionSetLambda actionsetlambda = null;
    PublicViewLambda pubviewlambda = null;
    WorkTeamLambda projectlambda = null;
    //***********************************************************************
    public void setActionSetLambda (ActionSetLambda actionsetlambda) { this.actionsetlambda = actionsetlambda; }
    public void setPublicViewLambda (PublicViewLambda pubviewlambda) { this.pubviewlambda = pubviewlambda; }
    public void setProjectLambda (WorkTeamLambda projectlambda) { this.projectlambda = projectlambda; }
    //***********************************************************************
    /**
     * Creates a variable pointer for a given ActionSet
     * @param pointer
     * @param projectid
     * @return
     * @throws AppException UNAUTHORIZED SURVEYNOTFOUND CANDIDATENOTFOUND
     * @throws Exception 
     */
    public String createVarPointer (ActionItemPointer pointer, long projectid) throws AppException, Exception {
        //==================================================================
        //We check the Action set is in the stated project.
        actionsetlambda.getActionSet(pointer.getClusterID(), projectid);
        //==================================================================
        String label = null;
        //==================================================================
        switch (pointer.getType()) {
            case ActionItemPointer.ITEMTYPE_PUBIMAGE: {
                PVCandidate candidate = pubviewlambda.getCandidate(pointer.getItemId());
                if (candidate.projectID() != projectid)
                    throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
                label = candidate.getLabel();
                break;
            }            
        }        
        //==================================================================
        actionsetlambda.createVarPointer(pointer);
        return label;
        //==================================================================
    }
    //***********************************************************************
    /**
     * Returns Variable items for a given ActionSet.
     * @param varclusterid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public VarItem[] getVariablesItems (long varclusterid) throws AppException, Exception {
        ActionItemPointer[] pointers = actionsetlambda.getItemPointers(varclusterid);
        List<VarItem> vars = new ArrayList<>();
        VarItem vitem;
        ActionItemBase item;
        for (ActionItemPointer pointer : pointers) {
            switch (pointer.getType()) {
                //------------------------------------------------------
                case ActionItemPointer.ITEMTYPE_PUBIMAGE:
                    try { item = pubviewlambda.getCandidate(pointer.getItemId()); }
                    catch (AppException e) { continue; }
                    vitem = new VarItem();
                    vitem.itemtype = ActionItemPointer.ITEMTYPE_PUBIMAGE;
                    vitem.itemid = pointer.getItemId();
                    vitem.item = item;
                    vars.add(vitem);
                    continue;
                //------------------------------------------------------
                default: continue;
            }            
        }
        return vars.toArray(new VarItem[0]);
    }
    //***********************************************************************
}
//***************************************************************************
