package lycine.viewmake;
//************************************************************************
import lycine.sample.SampleCenterPanel;
import lycine.sample.SamplePayLoad;
import methionine.AppException;
import methionine.project.ProjectLambda;
import tryptophan.design.DesignLambda;
import tryptophan.design.Variable;
import tryptophan.sample.Responder;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.SampleLambda;
//************************************************************************
public class ViewMaker {
    //********************************************************************
    ProjectLambda projectlambda = null;
    DesignLambda designlambda = null;
    SampleLambda samplelambda = null;
    //====================================================================
    public void setSampleLambda (SampleLambda samplelambda) { this.samplelambda = samplelambda; }
    public void setVariableLambda (DesignLambda variablelambda) { this.designlambda = variablelambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    //********************************************************************
    public SampleView getSampleView (long sampleid, long userid) throws AppException, Exception {
        //****************************************************************
        //We get the sample payload.
        //Wheter the user has access to the project is checked there.
        SampleCenterPanel samplecenter = new SampleCenterPanel();
        samplecenter.setSampleLambda(samplelambda);
        samplecenter.setProjectLambda(projectlambda);
        SamplePayLoad samplepayload = samplecenter.getSamplePayload(sampleid, userid);
        //****************************************************************
        //We create the SampleView Instance and get the needed data.
        SampleView sampleview = new SampleView();
        Responder[] responses = samplepayload.getResponses();
        //****************************************************************
        ResponseValue[] values;
        for (Responder response : responses) {
            //******************************************************
            //If for some reason we need to filter out this response
            //This is the time. Or shut up forever.
            //******************************************************
            //We get the values in the response and start looping them.
            values = response.getValues();
            VarStatAlpha varstat;
            for (ResponseValue value : values) {
                if (!sampleview.checkVariable(value.variableID())) {
                    varstat = createVariable(value);
                    sampleview.addVariableStat(varstat);
                }
                else varstat = sampleview.getVariable(value.variableID());
                addResponseToVarSat(varstat, value);
            }
            //******************************************************
        }
        //****************************************************************
        return sampleview;
        //****************************************************************
    }
    //********************************************************************
    private VarStatAlpha createVariable (ResponseValue value) throws AppException, Exception {
        //***********************************************************
        //We first recover the variable in question.
        Variable var = designlambda.getVariable(value.variableID());
        if (value.getType() != var.variableType()) {
            //It should happen NEVER. 
            //But if it happens we should not go further.
            System.out.println("Inconcistent type variable/value");
            return new VarStatAlpha();
        }
        //***********************************************************
        VarStatAlpha varstat = null;
        //-----------------------------------------------------------
        switch (value.getType()) {
            case Variable.VARTYPE_PUBVIEW:
                varstat = new VarStatPublicView();
                varstat.variableid = var.variableID();
                varstat.variabletype = Variable.VARTYPE_PUBVIEW;
                varstat.label = var.getLabel();
                return varstat;
        }
        return null;
    }
    //********************************************************************
    private void addResponseToVarSat (VarStatAlpha varstat, ResponseValue value) {
        //***********************************************************
        //If the value we intend to add is of a diferent type
        //Than the stat. We just leave.
        if (varstat.variabletype != value.getType()) return;
        //***********************************************************
        switch (varstat.variabletype) {
            case Variable.VARTYPE_PUBVIEW: {
                VarStatPublicView varst = (VarStatPublicView)varstat;
                varst.setValue(value.getValue());
            } break;
        }
        //***********************************************************
    }
    //********************************************************************
}
//************************************************************************
