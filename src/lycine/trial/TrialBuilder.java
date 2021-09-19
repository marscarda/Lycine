package lycine.trial;
//************************************************************************
import methionine.DataBaseName;
import methionine.Electra;
import methionine.billing.BillingLambda;
import methionine.project.ProjectLambda;
import threonine.universe.UniverseLambda;
import tryptophan.sample.SampleLambda;
import tryptophan.trial.Trial;
import tryptophan.trial.TrialLambda;
//************************************************************************
/**
 * This class is entrusted to carry out the task of completing the creation of a trial
 * @author marianoscardaccione
 */
public class TrialBuilder extends Thread {
    //********************************************************************
    long trialid = 0;
    DataBaseName dbname = null;
    public void setTrialID (long trialid) { this.trialid = trialid; }
    public void setDataBaseName (DataBaseName dbname) { this.dbname = dbname; }
    //********************************************************************
    Electra electra = null;
    ProjectLambda projectatlas = null;
    BillingLambda billingatlas = null;
    UniverseLambda universeatlas = null;
    SampleLambda sampleatlas = null;
    TrialLambda trialatlas = null;
    //********************************************************************
    @Override
    public void run () {
        //****************************************************
        electra = new Electra();
        prepareAtlas();
        //****************************************************
        
        
        qqm();
        
        
        //****************************************************
        electra.disposeDBConnection();
        //****************************************************
    }
    //********************************************************************
    private void qqm () {
        
        try {
            Trial trial = trialatlas.getTrial(trialid);
            
            System.out.println(trial.getID());
            System.out.println(trial.getName());
            
            
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        
        
    }
    //********************************************************************
    /**
     * Prepares all the Atlas Objects to get them ready to start
     * querying the db.
     */
    private void prepareAtlas () {
        //---------------------------------------------------
        projectatlas = new ProjectLambda();
        projectatlas.setElectraObject(electra);
        projectatlas.setDataBaseName(dbname.project);
        //---------------------------------------------------
        billingatlas = new BillingLambda();
        billingatlas.setElectraObject(electra);
        billingatlas.setDataBaseName(dbname.billing);
        //---------------------------------------------------
        universeatlas = new UniverseLambda();
        universeatlas.setElectraObject(electra);
        universeatlas.setDataBaseName(dbname.universe);
        //---------------------------------------------------
        sampleatlas = new SampleLambda();
        sampleatlas.setElectraObject(electra);
        sampleatlas.setDataBaseName(dbname.sample);
        //---------------------------------------------------
        trialatlas = new TrialLambda();
        trialatlas.setElectraObject(electra);
        trialatlas.setDataBaseName(dbname.trial);
        //---------------------------------------------------
    }
    //********************************************************************
    
    
    




    //********************************************************************
}
//************************************************************************
