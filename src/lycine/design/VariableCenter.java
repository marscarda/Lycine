package lycine.design;
//************************************************************************
import methionine.AppException;
import methionine.auth.AuthLamda;
import methionine.billing.BillingLambda;
import methionine.project.ProjectLambda;
import newfactor.survey.variable.Variable;
import newfactor.survey.variable.VariableLambda;
//************************************************************************
public class VariableCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    VariableLambda variablelambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setVariableLambda (VariableLambda variablelambda) { this.variablelambda = variablelambda; }
    //********************************************************************
    public void createVariable (Variable variable) throws AppException, Exception {
        
        
        
        variablelambda.createVariable(variable);
        
        

    }
    //********************************************************************
}
//************************************************************************
