package lycine.trialbuild;
//************************************************************************
import lycine.sample.SampleCenterBack;
import lycine.sample.SamplePayLoad;
import lycine.stats.StatHold;
import lycine.stats.VarStatAlpha;
import lycine.stats.VarStatPublicView;
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
    private void doBuilding () {
        try {
            //************************************************************
            Trial trial = trialatlas.getTrial(trialid);
            TrialSpace trialspace = trialatlas.getEnvironment(trial.trialSpaceID());
            universeatlas.getUniverse(trialspace.universeID());
            //============================================================
            trialspaceid = trialspace.environmentID();
            universeid = trialspace.universeID();
            //************************************************************
            SubSet subset = universeatlas.getRootSubset(universeid);
            DigData digind = new DigData();
            digind.setSubset(subset);
            doSubset(digind);
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
    private void doSubset (DigData digdatain) throws AppException, Exception {
        //*****************************************************
        DigData digindcall;
        //*****************************************************
        //StatHold stathold = new StatHold();
        //*****************************************************
        //Children Subsets loop.
        SubSet[] childrensubsets = universeatlas.getSubsets(universeid, digdatain.subsetID());
        for (SubSet childsubset : childrensubsets) {
            digdatain.addChildrenPopulation(childsubset.getPopulation());
            //=================================================
            digindcall = new DigData();
            digindcall.setSubset(childsubset);
            doSubset(digindcall);
            //=================================================
            fillSampleStat(digindcall);
        }
        //*****************************************************
    }
    //********************************************************************
    //********************************************************************
    //********************************************************************
    private void fillSampleStat (DigData digdata) throws AppException, Exception {
        //********************************************************
        SlotSelector sel = new SlotSelector();
        sel.trialspaceid = trialspaceid;
        sel.universeid = universeid;
        sel.subsetid = digdata.subsetID();
        //========================================================
        SampleSlot slot = null;
        SamplePayLoad samplepayload = null;
        //********************************************************
        try { 
            slot = trialatlas.getSampleSlotAllocation(sel);
            samplepayload = samplecenter.getSamplePayload(slot.sampleID(), 0);
        }
        catch (AppException e) {
            switch(e.getErrorCode()) {
                case TrialErrorCodes.SLOTALLOCATIONNOTFOUND:
                case SampleErrorCodes.SAMPLENOTFOUND:
                    break;
                default: throw e;
            }
        }
        //========================================================
        if (samplepayload == null) return;
        //********************************************************
        StatHold stathold = new StatHold();
        Responder[] responses = samplepayload.getResponses();
        ResponseValue[] values;
        VarStatAlpha varstat;
        for (Responder response : responses) {
            //****************************************************
            //If for some reason we need to filter out this response
            //This is the time. Or shut up forever.
            //****************************************************
            values = response.getValues();
            for (ResponseValue value : values) {
                if (!stathold.checkVariable(value.variableID())) {
                    varstat = createVariable(value);
                    stathold.addVariableStat(varstat);
                }
                else varstat = stathold.getVariable(value.variableID());
                addResponseToVarSat(varstat, value);
            }
        }
        //********************************************************
        digdata.addStatHold(stathold);
        //********************************************************
    }
    //********************************************************************
    VarStatAlpha createVariable (ResponseValue value) throws AppException, Exception {
        //***********************************************************
        //We first recover the variable in question.
        Variable var = designatlas.getVariable(value.variableID());
        /*
        if (value.getType() != var.variableType()) {
            //It should happen NEVER. 
            //But if it happens we should not go further.
            System.out.println("Inconcistent type variable/value");
            return new VarStatAlpha();
        }
        */
        //***********************************************************
        VarStatAlpha varstat = null;
        //-----------------------------------------------------------
        switch (value.getType()) {
            case Variable.VARTYPE_PUBVIEW:
                varstat = new VarStatPublicView();
                varstat.variableid = var.variableID();
                varstat.variabletype= Variable.VARTYPE_PUBVIEW;
                //varstat.label = var.getLabel();
                return varstat;
        }
        return null;
        
    }
    //********************************************************************
    //********************************************************************
    //********************************************************************
    //********************************************************************
    //********************************************************************
    
    //********************************************************************
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
