package lycine.trial;
//************************************************************************
import methionine.AppException;
import methionine.DataBaseName;
import methionine.TabList;
import methionine.auth.AuthLamda;
import methionine.billing.AlterUsage;
import methionine.billing.BillingLambda;
import methionine.billing.UsageCost;
import methionine.project.Project;
import methionine.project.ProjectErrorCodes;
import methionine.project.ProjectLambda;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
import tryptophan.sample.Sample;
import tryptophan.sample.SampleErrorCodes;
import tryptophan.trial.PlayRoom;
import tryptophan.trial.PlayRoomAtlas;
import tryptophan.sample.SampleAtlas;
import tryptophan.trial.SampleSlot;
import tryptophan.trial.SlotSelector;
import tryptophan.trial.Trial;
import tryptophan.trial.TrialErrorCodes;
//************************************************************************
public class TrialCenter {
    //********************************************************************
    AuthLamda authlambda = null;
    ProjectLambda projectatlas = null;
    BillingLambda billinglambda = null;
    PlayRoomAtlas triallambda = null;
    UniverseAtlas universelambda = null;
    SampleAtlas samplelambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda projectatlas) { this.projectatlas = projectatlas; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    public void setEnvironmentLambda (PlayRoomAtlas environmentlambda) { this.triallambda = environmentlambda; }
    public void setUniverseLambda (UniverseAtlas universelambda) { this.universelambda = universelambda; }
    public void setSampleLambda (SampleAtlas samplelambda) { this.samplelambda = samplelambda; }
    //********************************************************************
    public void createEnvirnment (PlayRoom environment, long userid) throws AppException, Exception {
        //****************************************************************
        if (environment.getName().length() == 0)
            throw new AppException("Trial Space Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //----------------------------------------------------------------
        if (environment.universeID() == 0)
            throw new AppException("A universe must be selected", AppException.INVALIDDATASUBMITED);
        //****************************************************************
        //Reading Part
        //****************************************************************
        //We check the user has write acces to the project
        projectatlas.checkAccess(environment.projectID(), userid, 2);
        //----------------------------------------------------------------
        //We recover the universe. We check it exists and add name to environment.
        Universe universe = universelambda.getUniverse(environment.universeID());
        environment.setUniverseName(universe.getName());
        //----------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectatlas.getProject(environment.projectID(), 0);
        //----------------------------------------------------------------
        //We persist the cost of this particular variable.
        environment.cost = UsageCost.ENVIRONMENT;
        //****************************************************************
        //Writing Part
        //****************************************************************
        //Lock All Tables
        TabList tabs = new TabList();
        triallambda.addCreateEnvironmentLock(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        triallambda.setAutoCommit(0);
        triallambda.lockTables(tabs);
        //----------------------------------------------------------------
        triallambda.createEnvironment(environment);
        //----------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(environment.cost);
        alter.setStartingEvent("Environment '" + environment.getName() + "' Created");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        triallambda.commit();
        triallambda.unLockTables();
        //------------------------------------------------------------------
    }
    //********************************************************************
    /**
     * Returns a trial space given its ID.
     * @param trialspaceid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public PlayRoom getTrialSpace (long trialspaceid, long userid) throws AppException, Exception {
        //****************************************************************
        //We recover the trial space.
        PlayRoom trialspace = triallambda.getEnvironment(trialspaceid);
        //****************************************************************
        //We check the performing user has access to the project.
        if (userid != 0)
            projectatlas.checkAccess(trialspace.projectID(), userid, 1);
        //****************************************************************
        //We recover the universe so we can fill the data in the trial space.
        Universe universe = universelambda.getUniverse(trialspace.universeID());
        trialspace.setUniverseName(universe.getName());
        //****************************************************************
        return trialspace;
    }
    //********************************************************************
    /**
     * Returns an array of environments given a project id
     * @param projectid
     * @param userid
     * @param fillextras
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public PlayRoom[] getTrialSpaces (long projectid, long userid, boolean fillextras) throws AppException, Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        projectatlas.checkAccess(projectid, userid, 1);
        //****************************************************************
        PlayRoom[] environments = triallambda.getEnviromentsByProject(projectid);
        if (!fillextras) return environments;
        //----------------------------------------------------------------
        Universe universe;
        for (PlayRoom environment : environments) {
            //----------------------------------------------
            try {
                universe = universelambda.getUniverse(environment.universeID());
                environment.setUniverseName(universe.getName());
            }
            catch (AppException e) {}
            //----------------------------------------------
        }
        //----------------------------------------------------------------
        return environments;
        //****************************************************************
    }
    //********************************************************************
    /**
     * Destroys an environment.
     * @param environmentid
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void destroyEnvironments (long environmentid, long userid) throws AppException, Exception {
        //****************************************************************
        //We fetch the environment and check the performing user has access to the project.
        PlayRoom environment = triallambda.getEnvironment(environmentid);
        projectatlas.checkAccess(environment.projectID(), userid, 3);
        //----------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectatlas.getProject(environment.projectID(), 0);
        //****************************************************************
        TabList tabs = new TabList();
        triallambda.addDestroyEnvironment(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        triallambda.setAutoCommit(0);
        triallambda.lockTables(tabs);
        //------------------------------------------------------------------
        triallambda.destroyEnvironment(environmentid);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(environment.cost);
        alter.setStartingEvent("Environment '" + environment.getName() + "' Destroyed");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        triallambda.commit();
        triallambda.unLockTables();
        //****************************************************************
    }
    //********************************************************************
    //********************************************************************
    public TrialSubset[] getTrialSubsets (long trialspaceid, long parentid, long userid) throws AppException, Exception {
        //****************************************************************
        PlayRoom trialspace = triallambda.getEnvironment(trialspaceid);
        //****************************************************************
        //We create the subsets array and fill them with the actual subsets
        SubSet[] subsets = universelambda.getSubsets(trialspace.universeID(), parentid);
        int subsetscount = subsets.length;
        TrialSubset[] tsubsets = new TrialSubset[subsetscount];
        for (int n = 0; n < subsetscount; n++) {
            tsubsets[n] = new TrialSubset();
            tsubsets[n].subset = subsets[n];
        }
        //****************************************************************
        //We set (Or at least we try) a sample in the subset where present.
        SampleSlot slot;
        SlotSelector selector = new SlotSelector();
        Sample sample;
        for (TrialSubset tsubSubset : tsubsets) {
            //==========================================================
            selector.trialspaceid = trialspace.environmentID();
            selector.universeid = trialspace.universeID();
            selector.subsetid = tsubSubset.subsetID();
            //==========================================================
            try { slot = triallambda.getSampleSlotAllocation(selector); }
            catch (AppException e) {
                if (e.getErrorCode() == TrialErrorCodes.SLOTALLOCATIONNOTFOUND) continue;
                throw e;
            }
            //---------------------------------------------
            try { sample = samplelambda.getSample(slot.sampleID()); }
            catch (AppException e) {
                if (e.getErrorCode() == SampleErrorCodes.SAMPLENOTFOUND) continue;
                throw e;
            }
            //==========================================================
            tsubSubset.sample = sample;
            //==========================================================
        }
        //****************************************************************
        return tsubsets;
        //****************************************************************
    }
    //********************************************************************
    /**
     * 
     * @param slotalloc
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void setSampleToSubset (SampleSlot slotalloc, long userid) throws AppException, Exception {
        //****************************************************************
        //We fetch the environment and check the performing user has access to the project.
        PlayRoom tialspace = triallambda.getEnvironment(slotalloc.trialSpaceID());
        long projectid = tialspace.projectID();
        projectatlas.checkAccess(projectid, userid, 2);
        //================================================================
        //We check the universe belongs to the same project
        Universe universe = universelambda.getUniverse(tialspace.universeID());
        if (universe.projectID() != projectid)
            throw new AppException("Objects from different projects", ProjectErrorCodes.ENTITYPROJECTINCONCISTENCY);
        slotalloc.setUniverseId(universe.universeID());
        //================================================================
        //We check the subset exists
        universelambda.getSubset(universe.universeID(), slotalloc.subsetID());
        //================================================================
        //We get the sample referenced.
        Sample sample = samplelambda.getSample(slotalloc.sampleID());
        if (sample.projectID() != projectid)
            throw new AppException("Objects from different projects", ProjectErrorCodes.ENTITYPROJECTINCONCISTENCY);
        slotalloc.setSample(sample);
        //****************************************************************
        triallambda.addSampleToSlot(slotalloc);
        //****************************************************************
    }
    //********************************************************************
    public void removeSampleAllocation (SlotSelector selector, long userid) throws AppException, Exception {
        //****************************************************************
        //We fetch the environment and check the performing user has access to the project.
        PlayRoom environment = triallambda.getEnvironment(selector.trialspaceid);
        if (userid != 0)
            projectatlas.checkAccess(environment.projectID(), userid, 3);
        //----------------------------------------------------------------
        triallambda.removeSampleAllocation(selector);
        //----------------------------------------------------------------
    }
    //********************************************************************
    public void createTrial (Trial trial, long userid, DataBaseName dbname) throws AppException, Exception {
        //****************************************************************
        if ( trial.getName().length() == 0)
            throw new AppException("Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //****************************************************************
        //We check the user has access to the project.
        projectatlas.checkAccess(trial.projectID(), userid, 2);
        //****************************************************************
        //Read and validation tests
        PlayRoom trialspace = triallambda.getEnvironment(trial.trialSpaceID());
        if (trialspace.projectID() != trial.projectID())
            throw new AppException("Project inconcistency", ProjectErrorCodes.ENTITYPROJECTINCONCISTENCY);
        //================================================================
        Universe universe = universelambda.getUniverse(trialspace.universeID());
        if (universe.projectID() != trial.projectID())
            throw new AppException("Project inconcistency", ProjectErrorCodes.ENTITYPROJECTINCONCISTENCY);
        trial.setUniverseId(universe.universeID());
        //****************************************************************
        triallambda.createTrial(trial);
        //****************************************************************
        //We trigger the building.
        
        //TrialBuilder builder = new TrialBuilder();
        //builder.setDataBaseName(dbname);
        //builder.setTrialID(trial.getID());
        //builder.start();
        //****************************************************************
    }
    //********************************************************************
    public Trial[] getTrials (long projectid, long userid) throws Exception {
        //****************************************************************
        //We check the performing user has access to the project.
        projectatlas.checkAccess(projectid, userid, 1);
        //****************************************************************
        Trial[] trials = triallambda.getTrials(projectid);
        //****************************************************************
        return trials;
    }
    //********************************************************************
}
//************************************************************************
