package lycine.trial.build;
//************************************************************************
import lycine.sample.SampleCenterBack;
import lycine.sample.SamplePayLoad;
import lycine.stats.StatSubset;
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
            //We recover the ROOT subset for the universe related.
            //We start the digging into the children subsets and calculate.
            SubSet subset = universeatlas.getRootSubset(universeid);
            SubsetDigging digging = new SubsetDigging();
            digging.setSubset(subset);
            doSubset(digging);
            //************************************************************
        }
        catch (AppException e) {
            return;
        }
        catch (Exception e) {
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
    private void doSubset (SubsetDigging diggingthis) throws AppException, Exception {
        //************************************************************
        SubsetDigging digindchild;
        StatsHolder statholder = new StatsHolder();
        //************************************************************
        statholder.setThisPopulation(diggingthis.getSubset().getPopulation());
        //************************************************************
        //Children Subsets loop.
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
            doSubset(digindchild);
            //=================================================
            doSampleStat(digindchild, statholder);
            //=================================================
        }
        //************************************************************
        calculateByPopulation(diggingthis, statholder);
        //************************************************************



        
        /*
        SubSet[] chld = digdatain.getChildrenSubsets();
        System.out.println("Subset id " + digdatain.subsetID() + " children " + chld.length + 
                " Stats " + objstat.getStatHolds().length);
        StatSubset stat;
        for (SubSet s : chld) {
            System.out.println("  Child: " + s.getSubsetID() + " " + objstat.findStat(s.getSubsetID()));
            if (objstat.findStat(s.getSubsetID())) {
                stat = objstat.getStat();
                VStSmplAlpha[] vars = stat.getVarStatistics();
                for (VStSmplAlpha var : vars) {
                    VStSmplPubView pv = (VStSmplPubView)var;
                    System.out.println("Positives: " + pv.getPositives());
                    System.out.println("Negatives: " + pv.getNegatives());
                }
            }
        }
        */
        
        //************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param digdata
     * @param subsetstat
     * @throws AppException
     * @throws Exception 
     */
    private void doSampleStat (SubsetDigging digging, StatsHolder subsetstat) throws AppException, Exception {
        //********************************************************
        //We prepare the land for recovering a sample slot
        SlotSelector sel = new SlotSelector();
        sel.trialspaceid = trialspaceid;
        sel.universeid = universeid;
        sel.subsetid = digging.subsetID();
        //========================================================
        SampleSlot slot;
        SamplePayLoad samplepayload;
        //********************************************************
        //We try to recover the payload of a sample.
        //If we fail. We check the cause and throw an e or simply return
        try { 
            slot = trialatlas.getSampleSlotAllocation(sel);
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
        statsubset.setSubsetId(digging.subsetID());
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
        subsetstat.addStat(statsubset);
        //********************************************************
    }
    //********************************************************************
    /**
     * 
     * @param digdata
     * @param objstats 
     */
    private void calculateByPopulation (SubsetDigging digdata, StatsHolder objstats) {

        
        
        
        SubSet[] chld = digdata.getChildrenSubsets();
        
        

        
        System.out.println(objstats.childrenPopulation());


        











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
        /*
        if (value.getType() != var.variableType()) {
            //It should happen NEVER. 
            //But if it happens we should not go further.
            System.out.println("Inconcistent type variable/value");
            return new VStSmplAlpha();
        }
        */
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
    //********************************************************************
    //********************************************************************
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
