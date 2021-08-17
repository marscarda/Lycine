package lycine.sample;
//************************************************************************
import methionine.AppException;
import methionine.auth.AuthLamda;
import methionine.billing.BillingLambda;
import methionine.project.ProjectLambda;
import tryptophan.design.DesignLambda;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleLambda;
//************************************************************************
public class SampleCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    BillingLambda billinglambda = null;
    DesignLambda designlambda = null;
    SampleLambda samplelambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda workteamlambda) { this.projectlambda = workteamlambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setVariableLambda (DesignLambda variablelambda) { this.designlambda = variablelambda; }
    public void setSampleLambda (SampleLambda samplelambda) { this.samplelambda = samplelambda; }
    //********************************************************************
    public void createSample (Sample sample, long userid) throws AppException, Exception {
        
        System.out.println(sample.getName());
        
    }
    //********************************************************************
}
//************************************************************************
