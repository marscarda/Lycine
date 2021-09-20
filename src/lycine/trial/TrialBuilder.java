package lycine.trial;
//************************************************************************
import methionine.AppException;
import methionine.DataBaseName;
import methionine.Electra;
import methionine.billing.BillingLambda;
import methionine.project.ProjectLambda;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
import tryptophan.sample.SampleLambda;
import tryptophan.trial.SampleSlot;
import tryptophan.trial.SlotSelector;
import tryptophan.trial.Trial;
import tryptophan.trial.TrialAtlas;
import tryptophan.trial.TrialErrorCodes;
import tryptophan.trial.TrialSpace;
//************************************************************************
/**
 * This class is entrusted to carry out the task of completing the creation of a trial
 * @author marianoscardaccione
 */
public class TrialBuilder extends Thread {
    //********************************************************************
    long trialid = 0;
    public void setTrialID (long trialid) { this.trialid = trialid; }
    //====================================================================
    long trialspaceid = 0;
    long universeid = 0;
    //********************************************************************
    DataBaseName dbname = null;
    public void setDataBaseName (DataBaseName dbname) { this.dbname = dbname; }
    //********************************************************************
    Electra electra = null;
    ProjectLambda projectatlas = null;
    BillingLambda billingatlas = null;
    UniverseAtlas universeatlas = null;
    SampleLambda sampleatlas = null;
    TrialAtlas trialatlas = null;
    //====================================================================
    //********************************************************************
    @Override
    public void run () {
        //****************************************************
        electra = new Electra();
        prepareAtlas();
        //****************************************************
        
        doBuilding();
        
        //****************************************************
        electra.disposeDBConnection();
        //****************************************************
    }
    //********************************************************************
    private void doBuilding () {
        try {
            //============================================================
            Trial trial = trialatlas.getTrial(trialid);
            TrialSpace trialspace = trialatlas.getEnvironment(trial.trialSpaceID());
            Universe universe = universeatlas.getUniverse(trialspace.universeID());
            //============================================================
            trialspaceid = trialspace.environmentID();
            universeid = trialspace.universeID();
            //============================================================
            SubSet subset = universeatlas.getRootSubset(universeid);
            digSubset(subset);
            //============================================================
        }
        catch (AppException e) {
            return;
        }
        catch (Exception e) {
            return;
        }
    }
    //********************************************************************
    private void digSubset (SubSet subset) throws AppException, Exception {
        //-----------------------------------------------
        SlotSelector sel = new SlotSelector();
        sel.trialspaceid = trialspaceid;
        sel.universeid = universeid;
        sel.subsetid = subset.getSubsetID();
        //-----------------------------------------------
        SampleSlot slot = null;
        try { slot = trialatlas.getSampleSlotAllocation(sel); }
        catch (AppException e) {
            if (e.getErrorCode() != TrialErrorCodes.SLOTALLOCATIONNOTFOUND) throw e;
        }
        //-----------------------------------------------


        System.out.println(subset.getName());
        
        
        
        //-----------------------------------------------
        SubSet[] childrensubsets = universeatlas.getSubsets(universeid, subset.getSubsetID());
        
        
        for (SubSet s : childrensubsets) {
            digSubset(s);
        }
        
        
        
        
        //-----------------------------------------------
        
        
        
        
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
        universeatlas = new UniverseAtlas();
        universeatlas.setElectraObject(electra);
        universeatlas.setDataBaseName(dbname.universe);
        //---------------------------------------------------
        sampleatlas = new SampleLambda();
        sampleatlas.setElectraObject(electra);
        sampleatlas.setDataBaseName(dbname.sample);
        //---------------------------------------------------
        trialatlas = new TrialAtlas();
        trialatlas.setElectraObject(electra);
        trialatlas.setDataBaseName(dbname.trial);
        //---------------------------------------------------
    }
    //********************************************************************
    
    
    




    //********************************************************************
}
//************************************************************************
