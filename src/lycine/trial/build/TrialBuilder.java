package lycine.trial.build;
//************************************************************************
import lycine.sample.SampleCenterBack;
import lycine.sample.SamplePayLoad;
import lycine.stats.StatSubset;
import lycine.stats.VStAlpha;
import lycine.stats.universe.VStUnivAlpha;
import lycine.stats.universe.VStUnivPubView;
import methionine.AppException;
import methionine.DataBaseName;
import methionine.Electra;
import methionine.billing.BillingLambda;
import methionine.project.ProjectLambda;
import threonine.universe.SubSet;
import threonine.universe.UniverseAtlas;
import tryptophan.design.DesignLambda;
import tryptophan.design.Variable;
import tryptophan.sample.Responder;
import tryptophan.sample.ResponseValue;
import tryptophan.sample.SampleErrorCodes;
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
    DesignLambda designatlas = null;
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
            //We recover the ROOT subset for the universe related.
            //We start the digging into the children subsets and calculate.
            
            //SubsetDigging digging = new SubsetDigging();
            //digging.setSubset(subset);
            doSubset(nester);
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
        //************************************************************
        //SubsetDigging digindchild;
        //StatsHolder statholder = new StatsHolder();
        //************************************************************
        //statholder.setThisPopulation(diggingthis.getSubset().getPopulation());
        //************************************************************
        
        System.out.println("Subset ID " + nester_this.subsetID());
        
        
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
        //Children Subsets loop.
        /*
        //------------------------------------------------------------
        //We recover the children subsets and add them to the "this digging".
        SubSet[] childrensubsets = universeatlas.getSubsets(universeid, diggingthis.subsetID());
        diggingthis.setChildrenSubsets(childrensubsets);
        //------------------------------------------------------------
        //The loop itself.
        for (SubSet childsubset : childrensubsets) {
            statholder.addChildPopulation(childsubset.getPopulation());
            //=================================================
            digindchild = new SubsetDigging();
            digindchild.setSubset(childsubset);
            
            nester_this.addChild(childsubset);
            
            //doSubset(digindchild, nester);
            //=================================================
            doSampleStat(digindchild, statholder);
            //=================================================
        }
        */
        //************************************************************
        //calculateByPopulation(diggingthis, statholder);
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
        VStUnivAlpha varstat;
        for (Responder response : responses) {
            //****************************************************
            values = response.getValues();
            for (ResponseValue value : values) {
                if (!statsubset.checkVariable(value.variableID())) {
                    varstat = createVariableStat(value);
                    statsubset.addVariableStat(varstat);
                }
                //-----------------------------------------------
                else varstat = (VStUnivAlpha)statsubset.getVariable(value.variableID());
                addResponseToVarSat(varstat, value);
                //-----------------------------------------------
            }
        }
        //********************************************************
        nester.setStat(statsubset);
        //********************************************************
    }
    //********************************************************************
    //********************************************************************
    /**
     * This method creates and return a VStat
     * @param value
     * @return
     * @throws AppException
     * @throws Exception 
     */
    private VStUnivAlpha createVariableStat (ResponseValue value) throws AppException, Exception {
        //***********************************************************
        //We first recover the variable in question.
        Variable var = designatlas.getVariable(value.variableID());
        //***********************************************************
        VStUnivAlpha varstat = null;
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
    private void addResponseToVarSat (VStUnivAlpha varstat, ResponseValue value) {
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
        sampleatlas = new SampleLambda();
        sampleatlas.setElectraObject(electra);
        sampleatlas.setDataBaseName(dbname.sample);
        //---------------------------------------------------
        trialatlas = new TrialAtlas();
        trialatlas.setElectraObject(electra);
        trialatlas.setDataBaseName(dbname.trial);
        //---------------------------------------------------
        designatlas = new DesignLambda();
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
