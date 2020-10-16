package lycine.epsilon;
//***************************************************************************
import java.util.ArrayList;
import java.util.List;
import methionine.AppException;
import tryptophan.survey.BaseToBeNamed;
import tryptophan.survey.publicview.PVCandidate;
import tryptophan.survey.baseform.VarClusterLambda;
import tryptophan.survey.publicview.PublicViewLambda;
import tryptophan.survey.baseform.BaseForm;
import tryptophan.survey.baseform.VarPointer;
//***************************************************************************
public class VarClusterCenter {
    //***********************************************************************
    VarClusterLambda clusterlambda = null;
    PublicViewLambda pubviewlambda = null;
    //***********************************************************************
    public void setVarClusterLambda (VarClusterLambda varclusterlambda) { this.clusterlambda = varclusterlambda; }
    public void setPublicViewLambda (PublicViewLambda pubviewlambda) { this.pubviewlambda = pubviewlambda; }
    //***********************************************************************
    /**
     * Creates a variable pointer for a given BaseForm
     * @param pointer
     * @return
     * @throws AppException UNAUTHORIZED SURVEYNOTFOUND CANDIDATENOTFOUND
     * @throws Exception 
     */
    public String createVarPointer (VarPointer pointer, long userid) throws AppException, Exception {
        //==================================================================
        String label = null;
        //==================================================================
        if (userid != 0) {
            BaseForm varcluster = clusterlambda.getVarCluster(pointer.getClusterID());
            if (varcluster.getOwner() != userid)
                throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        }
        //==================================================================
        switch (pointer.getType()) {
            //--------------------------------------------------------------
            case VarPointer.ITEMTYPE_PUBIMAGE: {
                PVCandidate candidate = pubviewlambda.getCandidate(pointer.getItemId());
                if (userid != 0 && candidate.getOwner() != userid)
                    throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
                label = candidate.getLabel();
                break;
            }
            //--------------------------------------------------------------
        }        
        //==================================================================
        clusterlambda.createVarPointer(pointer);
        return label;
        //==================================================================
    }
    //***********************************************************************
    /**
     * Destroys a variable pointer
     * @param varclusterid
     * @param variableid
     * @param userid
     * @throws Exception 
     */
    public void destroyVarPointer (long varclusterid, long variableid, long userid) throws Exception {
        //==================================================================
        if (userid != 0) {
            BaseForm varcluster = clusterlambda.getVarCluster(varclusterid);
            if (varcluster.getOwner() != userid)
                throw new AppException("Unauthorized", AppException.UNAUTHORIZED);
        }
        //==================================================================
        clusterlambda.destroyVarPointer(varclusterid, variableid);
        //==================================================================
    }
    //***********************************************************************
    /**
     * Returns Variable items for a given BaseForm.
     * @param varclusterid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public VarItem[] getVariablesItems (long varclusterid) throws AppException, Exception {
        VarPointer[] pointers = clusterlambda.getItemPointers(varclusterid);
        List<VarItem> vars = new ArrayList<>();
        VarItem vitem;
        BaseToBeNamed item;
        for (VarPointer pointer : pointers) {
            switch (pointer.getType()) {
                //------------------------------------------------------
                case VarPointer.ITEMTYPE_PUBIMAGE:
                    try { item = pubviewlambda.getCandidate(pointer.getItemId()); }
                    catch (AppException e) { continue; }
                    vitem = new VarItem();
                    vitem.itemtype = VarPointer.ITEMTYPE_PUBIMAGE;
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
