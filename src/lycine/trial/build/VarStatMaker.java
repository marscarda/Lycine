package lycine.trial.build;
//************************************************************************
import lycine.stats.VStAlpha;
import lycine.stats.universe.VStUnivPubView;
import methionine.AppException;
import tryptophan.design.DesignAtlas;
import tryptophan.design.Variable;
import tryptophan.sample.ResponseValue;
//************************************************************************
@Deprecated
public class VarStatMaker {
    //********************************************************************
    /**
     * This method creates and return a VStat
     * @param designatlas
     * @param value
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public static VStAlpha createVariableStat (DesignAtlas designatlas, ResponseValue value) throws AppException, Exception {
        //***********************************************************
        //We first recover the variable in question.
        Variable var = designatlas.getVariable(value.variableID());
        //***********************************************************
        VStAlpha varstat = null;
        //-----------------------------------------------------------
        switch (value.getType()) {
            case Variable.VARTYPE_PUBVIEW:
                varstat = new VStUnivPubView();
                varstat.variableid = var.variableID();
                varstat.variabletype= Variable.VARTYPE_PUBVIEW;
                return varstat;
        }
        return null;
    }
    //********************************************************************
    /**
     * Adds a value response to a var stat.
     * @param varstat
     * @param value 
     */
    public static void addResponseToVarSat (VStAlpha varstat, ResponseValue value) {
        //***********************************************************
        //If the value we intend to add is of a diferent type
        //Than the stat. We just leave.
        if (varstat.variabletype != value.getType()) return;
        //***********************************************************
        switch (varstat.variabletype) {
            case Variable.VARTYPE_PUBVIEW: {
                VStUnivPubView varst = (VStUnivPubView)varstat;
                varst.setValue(value.getValue());
            } break;
        }
        //***********************************************************
    }
    //********************************************************************
}
//************************************************************************
