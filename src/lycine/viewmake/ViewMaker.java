package lycine.viewmake;
//************************************************************************
import lycine.stats.sample.VStSmplPubView;
import lycine.stats.sample.VStSmplAlpha;
import lycine.stats.SampleView;
import lycine.sample.SampleCenterPanel;
import lycine.sample.SamplePayLoad;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import methionine.project.ProjectLambda;
import tryptophan.design.DesignLambda;
import tryptophan.design.Variable;
import tryptophan.sample.Responder;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleLambda;
//************************************************************************
public class ViewMaker {
    //********************************************************************
    ProjectLambda projectlambda = null;
    AuthLamda authlambda = null;
    DesignLambda designlambda = null;
    SampleLambda samplelambda = null;
    //====================================================================
    public void setSampleLambda (SampleLambda samplelambda) { this.samplelambda = samplelambda; }
    public void setVariableLambda (DesignLambda variablelambda) { this.designlambda = variablelambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    //********************************************************************
    public SampleView getSampleView (long sampleid, long userid, boolean setusername) throws AppException, Exception {
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
        Sample sample = samplepayload.getSample();
        sampleview.setSample(sample);
        Responder[] responses = samplepayload.getResponses();
        //================================================================
        //If the user name is required we add it to the sample.
        if (setusername) {
            try {
                User user = authlambda.getUser(sample.userID(), false);
                sample.setUserName(user.loginName());
            }
            catch (AppException e) {
                if (e.getErrorCode() != AuthErrorCodes.USERNOTFOUND) throw e;
            }
        }
        //****************************************************************
        ResponseValue[] values;
        for (Responder response : responses) {
            //******************************************************
            //If for some reason we need to filter out this response
            //This is the time. Or shut up forever.
            //******************************************************
            //We get the values in the response and start looping them.
            values = response.getValues();
            VStSmplAlpha varstat;
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
    private VStSmplAlpha createVariable (ResponseValue value) throws AppException, Exception {
        //***********************************************************
        //We first recover the variable in question.
        Variable var = designlambda.getVariable(value.variableID());
        if (value.getType() != var.variableType()) {
            //It should happen NEVER. 
            //But if it happens we should not go further.
            System.out.println("Inconcistent type variable/value");
            return null;
        }
        //***********************************************************
        VStSmplAlpha varstat = null;
        //-----------------------------------------------------------
        switch (value.getType()) {
            case Variable.VARTYPE_PUBVIEW:
                varstat = new VStSmplPubView();
                varstat.variableid = var.variableID();
                varstat.variabletype = Variable.VARTYPE_PUBVIEW;
                varstat.setLabel(var.getLabel());
                return varstat;
        }
        return null;
    }
    //********************************************************************
    private void addResponseToVarSat (VStSmplAlpha varstat, ResponseValue value) {
        //***********************************************************
        //If the value we intend to add is of a diferent type
        //Than the stat. We just leave.
        if (varstat.variabletype != value.getType()) return;
        //***********************************************************
        switch (varstat.variabletype) {
            case Variable.VARTYPE_PUBVIEW: {
                VStSmplPubView varst = (VStSmplPubView)varstat;
                varst.setValue(value.getValue());
            } break;
        }
        //***********************************************************
    }
    //********************************************************************
}
//************************************************************************
