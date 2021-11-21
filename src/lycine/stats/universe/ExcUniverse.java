package lycine.stats.universe;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.auth.AuthLamda;
import methionine.billing.AlterUsage;
import methionine.billing.BalanceInfo;
import methionine.billing.BillingErrorCodes;
import methionine.billing.BillingLambda;
import methionine.billing.CommerceTransfer;
import methionine.billing.UsageCost;
import methionine.project.Project;
import methionine.project.ProjectLambda;
import threonine.map.FolderUsage;
import threonine.map.MapErrorCodes;
import threonine.map.MapFolder;
import threonine.map.MapRecord;
import threonine.map.MapsLambda;
import threonine.midlayer.MapObjectGraphic;
import threonine.midlayer.MapReaderGraphic;
import threonine.midlayer.MapRecordGraphic;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
//**************************************************************************
public class ExcUniverse {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //======================================================================
    AuthLamda authlambda = null;
    ProjectLambda projectlambda = null;
    UniverseAtlas universelambda = null;
    MapsLambda mapslambda = null;
    BillingLambda billinglambda = null;
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setProjectLambda (ProjectLambda projectlambda) { this.projectlambda = projectlambda; }
    public void setUniverseLambda (UniverseAtlas universelambda) { this.universelambda = universelambda; }
    public void setMapsLambda (MapsLambda mapslambda) { this.mapslambda = mapslambda; }
    public void setBillingLambda (BillingLambda billinglambda) { this.billinglambda = billinglambda; }
    //**********************************************************************
    /**
     * Creates a new Universe. 
     * @param universe
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void createUniverse (Universe universe, long userid) throws AppException, Exception {
        //------------------------------------------------------------------
        if (universe.getName().length() == 0)
            throw new AppException("Universe Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We check the user thai is trying has write access to the project
        projectlambda.checkAccess(universe.projectID(), userid, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(universe.projectID(), 0);
        //------------------------------------------------------------------
        //The top subset of the new universe. We set the cost here.
        //The subset fields are completed in createUniverse(..)
        SubSet subset = new SubSet();
        subset.setCost(UsageCost.UNIVSUBSET);
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Transaction section
        TabList tabs = new TabList();
        universelambda.AddLockCreateSubset(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        universelambda.setAutoCommit(0);
        universelambda.lockTables(tabs);
        //------------------------------------------------------------------
        //We create the universe.
        universelambda.createUniverse(universe, subset);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(UsageCost.UNIVSUBSET);
        alter.setStartingEvent("Universe '" + universe.getName() + "' Created");
        billinglambda.alterUsage(alter);
        //------------------------------------------------------------------
        //We are done.
        universelambda.commit();
        universelambda.unLockTables();
        //------------------------------------------------------------------
    }
    //**********************************************************************
    /**
     * Returns a universe given its ID
     * @param universeid
     * @param userid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe getUniverse (long universeid, long userid) throws AppException, Exception {
        Universe universe = universelambda.getUniverse(universeid);
        //We check the user has access to the project
        if (userid != 0) projectlambda.checkAccess(universe.projectID(), userid, 1);
        return universe;
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
            projectlambda.checkAccess(projectid, userid, 1);
        return universelambda.getUniverses(projectid);
    }
    //**********************************************************************
    /**
     * Creates a universe subset 
     * @param subset
     * @param userid
     * @throws AppException
     * @throws Exception 
     */
    public void createSubset (SubSet subset, long userid) throws AppException, Exception {
        //------------------------------------------------------------------
        if (subset.getName().length() == 0)
            throw new AppException("Subset Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //------------------------------------------------------------------
        if (subset.getParentSubSet() == 0)
            throw new AppException("Subser cannot be created in the root", AppException.ROOTSUBSETALREADYEXISTS);
        //******************************************************************
        //Reading Part
        //******************************************************************
        //We recover the universe and check the user is able to perform this.
        Universe universe = universelambda.getUniverse(subset.getUniverseID());
        projectlambda.checkAccess(universe.projectID(), userid, 2);
        //------------------------------------------------------------------
        //We recover the project. Needed ahead when altering usage.
        Project project = projectlambda.getProject(universe.projectID(), 0);
        //------------------------------------------------------------------
        //We persist the cost of this particular subset.
        subset.setCost(UsageCost.UNIVSUBSET);
        //******************************************************************
        //Writing Part
        //******************************************************************
        //Lock All tables.
        TabList tabs = new TabList();
        universelambda.AddLockCreateSubset(tabs);
        billinglambda.AddLockAlterUsage(tabs);
        universelambda.setAutoCommit(0);
        universelambda.lockTables(tabs);
        //------------------------------------------------------------------
        //Creating the subset.
        universelambda.createSubSet(subset);
        //------------------------------------------------------------------
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setIncrease(UsageCost.UNIVSUBSET);
        alter.setStartingEvent("Subset " + subset.getName() + " Added to universe " + universe.getName());
        billinglambda.alterUsage(alter);
        //******************************************************************
        //We recalculate the population to parents.
        updateParentsPop(subset.getUniverseID(), subset.getParentSubSet());
        //******************************************************************
        //We are done.
        universelambda.commit();
        universelambda.unLockTables();
        //------------------------------------------------------------------
    }
    //**********************************************************************
    public SubSet getSubset (long universeid, long subsetid, long userid) throws AppException, Exception {
        //------------------------------------------------------------------
        //We check the user has access to the project.
        if (userid != 0) {
            Universe universe = universelambda.getUniverse(universeid);
            projectlambda.checkAccess(universe.projectID(), userid, 1);
        }
        //------------------------------------------------------------------
        SubSet subset;
        if (subsetid == 0) {
            subset = new SubSet();
            subset.setUniverseID(universeid);
            subset.setValid();
            subset.setROOT();
        }
        else subset = universelambda.getSubset(universeid, subsetid);
        return subset;
        //------------------------------------------------------------------
    }
    //**********************************************************************
    public SubSet[] getSubsets (long universeid, long parentid, long userid) throws AppException, Exception {
        //------------------------------------------------------------------
        //We check the user has access to the project.
        if (userid != 0) {
            Universe universe = universelambda.getUniverse(universeid);
            projectlambda.checkAccess(universe.projectID(), userid, 1);
        }
        //------------------------------------------------------------------
        SubSet[] subsets = universelambda.getSubsets(universeid, parentid);
        return subsets;
        //------------------------------------------------------------------
    }
    //**********************************************************************
    public void setMapRecordTo(long subsetid, long recordid, long projectid, long userid) throws AppException, Exception {
        //******************************************************************
        //Reading and Verification part
        //------------------------------------------------------------------
        //We check the user has access to the project.
        auriga.getProjectLambda().checkAccess(projectid, userid, 2);
        //------------------------------------------------------------------
        //We check the owner of the project is able to spend.
        //It is always true. Lets change this to balance check.
        Project projectsubset = auriga.getProjectLambda().getProject(projectid, 0);
        BalanceInfo balance = auriga.getBillingLambda().getTotalBalance(projectsubset.getOwner());
        if (balance.getTotalBalance() <= 0)
            throw new AppException("Not enough balance", BillingErrorCodes.CHARGEREJECTED);
        //------------------------------------------------------------------
        //We recover the record. In the proccess we check if the record can be
        //used in the project that is intended. The usage is useful here to
        //set the using cost of the map record.
        MapRecord record = auriga.getMapsLambda().getMapRecord(recordid);
        FolderUsage usage;
        try { usage = auriga.getMapsLambda().getFolderUsage(projectid, record.getFolderID()); }
        catch (AppException e) {
            if (e.getErrorCode() == MapErrorCodes.FOLDERUSEAGENOTFOUND)
                throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
            throw e;
        }
        //------------------------------------------------------------------
        //We decide wether to make a commerce transfer.
        boolean dotransfer = false;
        MapFolder folder = null;
        Project projectto = null;
        if (usage.costPerUse() != 0) {
            dotransfer = true;
            folder = auriga.getMapsLambda().getMapFolder(record.getFolderID());
            projectto = auriga.getProjectLambda().getProject(folder.projectID(), 0);
            //------------------------------------------------
            //The user to and from must be different. And Balance control to.
            if (projectsubset.getOwner() == projectto.getOwner()) dotransfer = false;
            else {
                if (balance.getTotalBalance() < usage.costPerUse())
                    throw new AppException("Not enough balance", BillingErrorCodes.BALANCEINSUFICIENT);
            }
            //------------------------------------------------
        }
        //------------------------------------------------------------------
        //We check the subset exists and the user has access to the project
        //where it belongs.
        SubSet subset = auriga.getUniverseAtlas().getSubset(0, subsetid);
        Universe universe = auriga.getUniverseAtlas().getUniverse(subset.getUniverseID());
        if (universe.projectID() != projectid)
            throw new AppException("Unauthorized", AuthErrorCodes.UNAUTHORIZED);
        //------------------------------------------------------------------
        MapReaderGraphic mapreader = new MapReaderGraphic();
        mapreader.setMapsLambda(auriga.getMapsLambda());
        MapRecordGraphic recordg = mapreader.getRecord(record);
        MapObjectGraphic[] objects = recordg.getMapObjects();
        //------------------------------------------------------------------
        //If there is no map object in the record.
        if (objects.length == 0)
            throw new AppException("The record " + record.getName() + " has no map object", AppException.NOMAPOBJECTINRECORD);
        //******************************************************************
        //Writing part
        //------------------------------------------------------------------
        //We lock all tables involved
        TabList tablist = new TabList();
        auriga.getUniverseAtlas().AddLockMapRecord(tablist);
        auriga.getBillingLambda().AddLockAlterUsage(tablist);
        auriga.getBillingLambda().addLockCommunityCommerce(tablist);
        auriga.getUniverseAtlas().setAutoCommit(0);
        auriga.getUniverseAtlas().lockTables(tablist);
        //------------------------------------------------------------------
        //We clear the existent map objects the subset could have
        auriga.getUniverseAtlas().clearMapObject(subset.getSubsetID());
        //------------------------------------------------------------------
        //We Add the objects to the subset.
        for (MapObjectGraphic obj : objects)
            auriga.getUniverseAtlas().addMapObject(subset.getSubsetID(), obj.getPoints());
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
            alter.setIncrease(UsageCost.MAPRECORDSUBSET);
            alter.setStartingEvent("Map Record set to subset '");
            auriga.getBillingLambda().alterUsage(alter);
            //--------------------------------------------------------------
            //We record How Much this Cost.
            auriga.getUniverseAtlas().setMapCost(subset.getUniverseID(), subset.getSubsetID(), UsageCost.MAPRECORDSUBSET);
        }
        //==================================================================
        //We are all done.
        auriga.getUniverseAtlas().commit();
        auriga.getUniverseAtlas().unLockTables();
        //******************************************************************
    }
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
        SubSet subset = universelambda.getSubset(universe, pinsubset);
        SubSet[] subsets = universelambda.getSubsets(universe, pinsubset);
        int childrenpop = 0;
        //==============================================
        for (SubSet sbst : subsets)
            childrenpop += sbst.getPopulation();
        //********************************************************
        if (childrenpop > subset.getPopulation()) 
            universelambda.setSubsetPop(universe, pinsubset, childrenpop);
        //********************************************************
        updateParentsPop(universe, subset.getParentSubSet());
        //********************************************************
    }
    //**********************************************************************
}
//**************************************************************************
