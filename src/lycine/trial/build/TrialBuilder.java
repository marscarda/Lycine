package lycine.trial.build;
//************************************************************************
import lycine.sample.SampleCenterBack;
import tryptophan.sample.SamplePayLoad;
import lycine.stats.StatSubset;
import lycine.stats.VStAlpha;
import methionine.AppException;
import methionine.DataBaseName;
import methionine.Electra;
import methionine.billing.BillingLambda;
import methionine.project.ProjectLambda;
import threonine.universe.SubSet;
import threonine.universe.UniverseAtlas;
import tryptophan.design.DesignAtlas;
import tryptophan.sample.Responder;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.SampleErrorCodes;
import tryptophan.sample.SampleAtlas;
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
@Deprecated
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
    SampleAtlas sampleatlas = null;
    TrialAtlas trialatlas = null;
    DesignAtlas designatlas = null;
    //====================================================================
    SampleCenterBack samplecenter = null;
    //********************************************************************
    /**
     * This is where the building thread starts.
     */
    @Override
    public void run () {
        //****************************************************
        electra = new Electra();
        initializeAtlas();
        //****************************************************
        
        doBuilding();
        
        //****************************************************
        electra.disposeDBConnection();
        //****************************************************
    }
    //********************************************************************
    /**
     * The entry point to do the building.
     */
    private void doBuilding () {
        try {
            //************************************************************
            //We recover the trial and the universe related to it.
            Trial trial = trialatlas.getTrial(trialid);
            TrialSpace trialspace = trialatlas.getEnvironment(trial.trialSpaceID());
            universeatlas.getUniverse(trialspace.universeID()); // check if the universe exists.
            trialspaceid = trialspace.environmentID(); // Sets the trial id
            universeid = trialspace.universeID(); // Sets the universe id
            //************************************************************
            //We create the top Nester.
            StatNester nester = new StatNester();
            SubSet subset = universeatlas.getRootSubset(universeid);
            nester.setSubset(subset);
            //************************************************************
            doSubset(nester);
            doCalcByPop(nester);
            //************************************************************
        }
        catch (AppException e) {
            System.out.println(e.getMessage());
            return;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        cleanUp();
    }
    //********************************************************************
    /**
     * This is the recursive method that dig deep into the universe.
     * @param diggingthis
     * @throws AppException
     * @throws Exception 
     */
    private void doSubset (StatNester nester_this) throws AppException, Exception {
        doSubsetStat(nester_this);
        //************************************************************
        SubSet[] subsets_children = universeatlas.getSubsets(universeid, nester_this.subsetID());
        for (SubSet subset : subsets_children)
            nester_this.addChild(subset);
        //************************************************************
        StatNester[] nester_children = nester_this.getChildren();
        for (StatNester nester : nester_children)
            doSubset(nester);
        //************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param nester 
     */
    private void doSubsetStat (StatNester nester) throws AppException, Exception {
        //========================================================
        SubSet subset = nester.getSubset();
        //========================================================
        SlotSelector sel = new SlotSelector();
        sel.trialspaceid = trialspaceid;
        sel.universeid = universeid;
        sel.subsetid = subset.getSubsetID();
        //========================================================
        SamplePayLoad samplepayload;
        //--------------------------------------------------------
        //We try to recover the payload of a sample.
        //If we fail. We check the cause and throw an e or simply return
        try { 
            SampleSlot slot = trialatlas.getSampleSlotAllocation(sel);
            samplepayload = samplecenter.getSamplePayload(slot.sampleID(), 0);
        }
        catch (AppException e) {
            switch(e.getErrorCode()) {
                case TrialErrorCodes.SLOTALLOCATIONNOTFOUND:
                case SampleErrorCodes.SAMPLENOTFOUND:
                    return;
                default: throw e;
            }
        }
        //********************************************************
        //At this point we have a sample payload.
        //********************************************************
        StatSubset statsubset = new StatSubset();
        statsubset.setSubsetId(subset.getSubsetID());
        Responder[] responses = samplepayload.getResponses();
        ResponseValue[] values;
        VStAlpha varstat;
        for (Responder response : responses) {
            //****************************************************
            values = response.getValues();
            for (ResponseValue value : values) {
                //if (!statsubset.checkVariable(value.variableID())) {
                //    varstat = VarStatMaker.createVariableStat(designatlas, value);
                //    statsubset.addVariableStat(varstat);
                //}
                //-----------------------------------------------
                //else varstat = statsubset.getVariable(value.variableID());
                //VarStatMaker.addResponseToVarSat(varstat, value);
                //-----------------------------------------------
            }
        }
        //********************************************************
        nester.setStat(statsubset);
        //********************************************************
    }
    //********************************************************************
    private void doCalcByPop (StatNester nester) throws Exception {
        //********************************************************
        StatNester[] children = nester.getChildren();
        //********************************************************
        for (StatNester child : children)
            doCalcByPop(child);
        //********************************************************
        if (nester.hasStat()) {
            StatSubset stat = nester.getStat();
            //VStAlpha[] vars = stat.getVarStatistics();
            //for (VStAlpha var : vars) {
            //    var.calculateLocal();
            //}
        }
        //********************************************************
        if (nester.childStats() > 1) {
            qwerty(nester);
        }
        
    }
    //********************************************************************
    private void qwerty (StatNester nester) throws Exception {
        StatNester[] children = nester.getChildren();
        StatSubset stat;
        VStAlpha[] vars;
        for (StatNester child : children) {
            if (!child.hasStat()) continue;
            stat = child.getStat();
            //vars = stat.getVarStatistics();
            //for (VStAlpha var : vars)
            //    var.calculateGlobal(child.popSubset(), nester.popSubset());
        }
    }
    //********************************************************************
    /**
     * Prepares all the Atlas Objects to get them ready to start
     * querying the db.
     */
    private void initializeAtlas () {
        //***************************************************
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
        sampleatlas = new SampleAtlas();
        sampleatlas.setElectraObject(electra);
        sampleatlas.setDataBaseName(dbname.sample);
        //---------------------------------------------------
        trialatlas = new TrialAtlas();
        trialatlas.setElectraObject(electra);
        trialatlas.setDataBaseName(dbname.trial);
        //---------------------------------------------------
        designatlas = new DesignAtlas();
        designatlas.setElectraObject(electra);
        designatlas.setDataBaseName(dbname.design);
        //***************************************************
        samplecenter = new SampleCenterBack();
        samplecenter.setSampleLambda(sampleatlas);
        //***************************************************
    }
    //********************************************************************
    private void cleanUp () { electra.disposeDBConnection(); }
    //********************************************************************
}
//************************************************************************
