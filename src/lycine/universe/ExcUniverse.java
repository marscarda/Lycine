package lycine.universe;
//**************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.auth.Session;
import methionine.finance.AlterUsage;
import methionine.finance.BalanceInfo;
import methionine.finance.BillingErrorCodes;
import methionine.finance.FinanceAtlas;
import methionine.finance.CommerceTransfer;
import methionine.finance.FinanceRules;
import methionine.project.Project;
import methionine.project.ProjectLambda;
import threonine.mapping.FolderUsage;
import threonine.mapping.MapErrorCodes;
import threonine.mapping.MapFolder;
import threonine.mapping.MapRecord;
import threonine.midlayer.MapObjectGraphic;
import threonine.midlayer.MapReaderGraphic;
import threonine.midlayer.MapRecordDraw;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
import threonine.universe.UniverseErrorCodes;
//**************************************************************************
public class ExcUniverse {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //======================================================================
    //When get children subsets. We set the map status of current subset.
    boolean mapstatus = false;
    public boolean mapStatus () { return mapstatus; }
    //**********************************************************************
    /**
     * Creates a new Universe. 
     * @param universe
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createUniverse (Universe universe, Session session) throws AppException, Exception {
        //------------------------------------------------------------------
        if (universe.getName().length() == 0)
            throw new AppException("Universe Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = auriga.projectAtlas().getProject(universe.projectID());
        //------------------------------------------------------------------
        //The top subset of the new universe. We set the cost here.
        //The subset fields are completed in createUniverse(..)
        SubSet subset = new SubSet();
        subset.setName(universe.getName());
        subset.setWeight(1);
        subset.setCost(FinanceRules.UNIVSUBSET);
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Transaction section
        TabList tabs = new TabList();
        auriga.getUniverseAtlas().AddLockCreateSubset(tabs);
        auriga.getBillingLambda().lockAlterUsage(tabs);
        auriga.getUniverseAtlas().setAutoCommit(0);
        auriga.getUniverseAtlas().lockTables(tabs);
        //------------------------------------------------------------------
        //We create the universe.
        auriga.getUniverseAtlas().createUniverse(universe, subset);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(FinanceRules.UNIVSUBSET);
        alter.setStartingEvent("Universe '" + universe.getName() + "' Created");
        auriga.getBillingLambda().alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        auriga.getUniverseAtlas().commit();
        auriga.getUniverseAtlas().unLockTables();
        //------------------------------------------------------------------
    }
    //**********************************************************************
    /**
     * Returns a universe given its ID
     * @param universeid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe getUniverse (long universeid, Session session) throws AppException, Exception {
        Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
        //==================================================================
        //We check the user has write acces to the project where the universe belongs
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 1);
        //==================================================================
        return universe;
        //==================================================================
    }
    //**********************************************************************
    /**
     * Returns the list of universes given a project id
     * @param projectid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe[] getUniversesByProject (long projectid, long userid) throws AppException, Exception {
        //We check the user has access to the project.
        if (userid != 0) 
            auriga.projectAtlas().checkAccess(projectid, userid, 1);
        return auriga.getUniverseAtlas().getUniverses(projectid);
    }
    //**********************************************************************
    /**
     * Creates a universe subset 
     * @param subset
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void createSubset (SubSet subset, Session session) throws AppException, Exception {
        //------------------------------------------------------------------
        if (subset.getName().length() == 0)
            throw new AppException("Subset Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //------------------------------------------------------------------
        if (subset.getParentSubSet() == 0)
            throw new AppException("Subser cannot be created in the root", UniverseErrorCodes.ROOTSUBSETALREADYEXISTS);
        //******************************************************************
        UniverseAtlas uatlas = auriga.getUniverseAtlas();
        ProjectLambda patlas = auriga.projectAtlas();
        FinanceAtlas fatlas = auriga.getBillingLambda();
        //******************************************************************
        //We use the main server
        uatlas.usesrvFullMainSrv();
        patlas.usesrvFullMainSrv();
        fatlas.usesrvFullMainSrv();
        //******************************************************************
        Universe universe = uatlas.getUniverse(subset.getUniverseID());
        //******************************************************************
        //We check the performing user has access to the project.
        //We check the auth to do this.
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 2);        
        //******************************************************************
        //Lock All tables.
        TabList tabs = new TabList();
        uatlas.AddLockCreateSubset(tabs);
        fatlas.lockAlterUsage(tabs);
        patlas.lockProjects(tabs);
        uatlas.setAutoCommit(0);
        uatlas.lockTables(tabs);
        //******************************************************************
        //We recover the project. Needed ahead when altering usage.
        Project project = patlas.getProject(universe.projectID());
        //------------------------------------------------------------------
        subset.setCost(FinanceRules.UNIVSUBSET);
        uatlas.createSubSet(subset);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(FinanceRules.UNIVSUBSET);
        alter.setStartingEvent("Subset " + subset.getName() + " Added to universe " + universe.getName());
        fatlas.alterUsage(alter);        
        //------------------------------------------------------------------
        //We recalculate the population to parents.
        updateParentsPop(subset.getUniverseID(), subset.getParentSubSet());
        //******************************************************************
        //We are done.
        uatlas.commit();
        //******************************************************************
    }
    //**********************************************************************
    /**
     * Returns a Subset by ID. if we already have the universe this is the method.
     * Checks user authorization to fetch.
     * @param universe
     * @param subsetid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubSet getSubset (Universe universe, long subsetid, Session session) throws AppException, Exception {
        //==================================================================
        //We check the user has write acces to the project where the universe belongs
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 1);
        //==================================================================
        SubSet subset;
        if (subsetid == 0) {
            subset = new SubSet();
            subset.setUniverseID(universe.universeID());
            subset.setValid();
            subset.setROOT();
        }
        else subset = auriga.getUniverseAtlas().getSubset(universe.universeID(), subsetid);
        return subset;
        //------------------------------------------------------------------
    }
    //**********************************************************************
    public SubSet getSubset (long universeid, long subsetid) throws AppException, Exception {
        SubSet subset;
        if (subsetid == 0) {
            subset = new SubSet();
            subset.setUniverseID(universeid);
            subset.setValid();
            subset.setROOT();
        }
        else subset = auriga.getUniverseAtlas().getSubset(universeid, subsetid);
        return subset;
    }
    //**********************************************************************
    /**
     * Returns an array of subset given a parent subset
     * @param universeid
     * @param parentid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubSet[] getSubsets (long universeid, long parentid, Session session) throws AppException, Exception {
        Universe universe = auriga.getUniverseAtlas().getUniverse(universeid);
        return getSubsets(universe, parentid, session);
    }    
    //======================================================================
    /**
     * 
     * @param universe
     * @param parentid
     * @param session
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public SubSet[] getSubsets (Universe universe, long parentid, Session session) throws AppException, Exception {
        //==================================================================
        //We check the user has write acces to the project where the universe belongs
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 1);
        //==================================================================
        SubSet[] subsets = auriga.getUniverseAtlas().getSubsets(universe.universeID(), parentid);
        //==================================================================
        mapstatus = false;
        for (SubSet s : subsets)
            if (s.mapStatus()) { mapstatus = true; break; }
        //==================================================================
        return subsets;
        //==================================================================
    }
    //**********************************************************************
    /**
     * 
     * @param subsetid
     * @param recordid
     * @param session
     * @throws AppException
     * @throws Exception 
     */
    public void setMapRecordToSubset (long subsetid, long recordid, Session session) throws AppException, Exception {
        //******************************************************************
        //Reading Part and decisions taking
        //******************************************************************
        UniverseAtlas univatlas = auriga.getUniverseAtlas();
        ProjectLambda prjcatlas = auriga.projectAtlas();
        FinanceAtlas fincatlas = auriga.getBillingLambda();
        //******************************************************************
        //First necesary reads.
        SubSet subset = univatlas.getSubset(0, subsetid);
        Universe universe = univatlas.getUniverse(subset.getUniverseID());
        Project projectsubset = prjcatlas.getProject(universe.projectID());
        //==================================================================
        //We check the user has write acces to the project where the universe belongs
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(projectsubset.projectID(), session, 2);
        //==================================================================
        //We check the owner of the project is able to spend.
        //It is always true. Lets change this to balance check.
        BalanceInfo balance = auriga.getBillingLambda().getTotalBalance(projectsubset.getOwner());
        FinanceRules.spendOk(balance.getAvailableBalance());
        //******************************************************************
        //Second reading. We check there is a valid usage of the folder in
        //the project the subset belongs to.
        MapRecord record = auriga.getMapsLambda().getMapRecord(recordid);
        FolderUsage usage;
        try { usage = auriga.getMapsLambda().getFolderUsage(projectsubset.projectID(), record.layerID()); }
        catch (AppException e) {
            if (e.getErrorCode() == MapErrorCodes.FOLDERUSEAGENOTFOUND)
                throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
            throw e;
        }
        //******************************************************************
        //We decide wether to make a commerce transfer.
        boolean dotransfer = false;
        MapFolder folder = null;
        Project projectto = null;
        if (usage.costPerUse() != 0) {
            dotransfer = true;
            folder = auriga.getMapsLambda().getMapFolder(record.layerID());
            projectto = auriga.projectAtlas().getProject(folder.projectID());
            //------------------------------------------------
            //The user to and from must be different. And Balance control to.
            if (projectsubset.getOwner() == projectto.getOwner()) dotransfer = false;
            else {
                if (balance.getTotalBalance() < usage.costPerUse())
                    throw new AppException("Not enough balance", BillingErrorCodes.BALANCEINSUFICIENT);
            }
            //------------------------------------------------
        }
        //******************************************************************
        //We fetch the map features from the record we want to set.
        MapReaderGraphic mapreader = new MapReaderGraphic();
        mapreader.setMapsLambda(auriga.getMapsLambda());
        MapRecordDraw recordg = mapreader.getRecord(record);
        MapObjectGraphic[] features = recordg.getMapObjects();
        //------------------------------------------------------------------
        //If there is no map object in the record. We throw an exception
        if (features.length == 0)
            throw new AppException("The record " + record.getName() + " has no map object", AppException.NOMAPOBJECTINRECORD);
        
        //******************************************************************
        //WE ENTER THE EXECUTION SECTION
        //******************************************************************
        //We lock all tables involved
        TabList tablist = new TabList();
        univatlas.lockSubset(tablist);
        univatlas.lockMapObject(tablist);
        univatlas.lockLocationPoint(tablist);
        fincatlas.lockAlterUsage(tablist);
        if (dotransfer) fincatlas.lockCommunityCommerce(tablist);
        univatlas.useMaster();
        univatlas.setAutoCommit(0);
        univatlas.lockTables(tablist);
        //******************************************************************
        //We reread from the master
        subset = univatlas.getSubset(0, subsetid);
        //------------------------------------------------------------------
        //We clear the existent map objects the subset could have
        univatlas.clearMapObject(subset.getSubsetID());
        //------------------------------------------------------------------
        //We Add the objects to the subset.
        for (MapObjectGraphic feature : features)
            univatlas.addMapFeature(subset.getSubsetID(), feature.getPoints());
        univatlas.setMapStatus(subsetid, 1);
        //==================================================================
        //If the use of the map object has a cost we create a transfer.
        if (dotransfer) {
            CommerceTransfer transfer = new CommerceTransfer();
            transfer.setFromUserid(projectsubset.getOwner());
            transfer.setFromProjectId(projectsubset.projectID());
            transfer.setToUserId(projectto.getOwner());//If it was a null pointer we would not be here.
            transfer.setToProjectId(projectto.projectID());
            String description = "Map Record " + record.getName() + " Added to subset";
            transfer.setDescription(description);
            transfer.setAmount(usage.costPerUse());
            auriga.getBillingLambda().addCommerceTransfer(transfer);
        }
        //==================================================================
        //We alter the usage cost.
        if (subset.getMapCost() == 0) {
            AlterUsage alter = new AlterUsage();
            alter.setProjectId(projectsubset.projectID());
            alter.setProjectName(projectsubset.getName());
            alter.setIncrease(FinanceRules.MAPRECORDSUBSET);
            alter.setStartingEvent("Map Record set to subset '");
            auriga.getBillingLambda().alterUsage(alter);
            //--------------------------------------------------------------
            //We record How Much this Cost.
            auriga.getUniverseAtlas().setMapCost(subset.getUniverseID(), subset.getSubsetID(), FinanceRules.MAPRECORDSUBSET);
        }
        //==================================================================
        //We are all done.
        auriga.getUniverseAtlas().commit();
        auriga.getUniverseAtlas().unLockTables();
        //******************************************************************
    }
    //**********************************************************************
    //**********************************************************************
    /**
     * 
     * @param subsetid
     * @throws AppException
     * @throws Exception 
     */
    private void updateParentsPop (long universe, long pinsubset) throws AppException, Exception {
        //********************************************************
        //Top reached. Nothing to do.
        if (pinsubset == 0) return;
        //********************************************************
        SubSet subset = auriga.getUniverseAtlas().getSubset(universe, pinsubset);
        SubSet[] subsets = auriga.getUniverseAtlas().getSubsets(universe, pinsubset);
        int childrenpop = 0;
        //==============================================
        for (SubSet sbst : subsets)
            childrenpop += sbst.getPopulation();
        //********************************************************
        if (childrenpop > subset.getPopulation()) 
            auriga.getUniverseAtlas().setSubsetPop(universe, pinsubset, childrenpop);
        //********************************************************
        updateParentsPop(universe, subset.getParentSubSet());
        //********************************************************
    }
    //**********************************************************************
    
    //**********************************************************************
}
//**************************************************************************
