package lycine.viewmake;
//************************************************************************
import histidine.AurigaObject;
import lycine.stats.sample.VStSmplPubView;
import lycine.stats.SampleView;
import lycine.sample.SampleCenterPanel;
import tryptophan.sample.SamplePayLoad;
import lycine.stats.VStAlpha;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.User;
import tryptophan.design.Metric;
import tryptophan.sample.Responder;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.Sample;
//************************************************************************
public class ViewMaker {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public SampleView getSampleView (long sampleid, long userid, boolean setusername) throws AppException, Exception {
        //****************************************************************
        //We get the sample payload.
        //Wheter the user has access to the project is checked there.
        SampleCenterPanel samplecenter = new SampleCenterPanel();
        samplecenter.setAuriga(auriga);
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
                User user = auriga.getAuthLambda().getUser(sample.userID(), false);
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
            VStAlpha varstat;
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
    private VStAlpha createVariable (ResponseValue value) throws AppException, Exception {
        //***********************************************************
        //We first recover the variable in question.
        Metric metric = auriga.getDesignLambda().getVariable(value.metricID());
        if (value.getType() != metric.variableType()) {
            //It should happen NEVER. 
            //But if it happens we should not go further.
            System.out.println("Inconcistent type variable/value");
            return null;
        }
        //***********************************************************
        VStAlpha varstat = null;
        //-----------------------------------------------------------
        switch (value.getType()) {
            case Metric.VARTYPE_PUBVIEW:
                varstat = new VStSmplPubView();
                varstat.variableid = metric.metricID();
                varstat.variabletype = Metric.VARTYPE_PUBVIEW;
                varstat.setLabel(metric.getLabel());
                return varstat;
        }
        return null;
    }
    //********************************************************************
    private void addResponseToVarSat (VStAlpha varstat, ResponseValue value) {
        //***********************************************************
        //If the value we intend to add is of a diferent type
        //Than the stat. We just leave.
        if (varstat.variabletype != value.getType()) return;
        //***********************************************************
        switch (varstat.variabletype) {
            case Metric.VARTYPE_PUBVIEW: {
                VStSmplPubView varst = (VStSmplPubView)varstat;
                varst.setValue(value.getValue());
            } break;
        }
        //***********************************************************
    }
    //********************************************************************
}
//************************************************************************
