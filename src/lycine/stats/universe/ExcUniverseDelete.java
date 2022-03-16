package lycine.stats.universe;
//**************************************************************************
import histidine.AurigaObject;
import histidine.auth.ProjectAuth;
import methionine.AppException;
import methionine.TabList;
import methionine.auth.Session;
import methionine.finance.AlterUsage;
import methionine.finance.FinanceAtlas;
import methionine.project.Project;
import methionine.project.ProjectLambda;
import threonine.universe.SubSet;
import threonine.universe.Universe;
import threonine.universe.UniverseAtlas;
//**************************************************************************
public class ExcUniverseDelete {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //**********************************************************************
    Project project = null;
    float cost = 0;
    int edits = 0;
    //**********************************************************************
    public void clearMap (long universeid, long subsetid, Session session) throws AppException, Exception {
        //******************************************************************
        //READING
        //******************************************************************
        UniverseAtlas univatlas = auriga.getUniverseAtlas();
        FinanceAtlas fincatlas = auriga.getBillingLambda();
        ProjectLambda prjatlas = auriga.projectAtlas();
        //******************************************************************
        TabList tablist = new TabList();
        univatlas.lockUniverse(tablist);
        univatlas.lockSubset(tablist);
        univatlas.lockMapObject(tablist);
        univatlas.lockLocationPoint(tablist);
        fincatlas.lockUsagePeriods(tablist);
        prjatlas.lockProjects(tablist);
        univatlas.useMaster();
        univatlas.setAutoCommit(0);
        univatlas.lockTables(tablist);
        //******************************************************************
        Universe universe = univatlas.getUniverse(universeid);
        ProjectAuth pauth = new ProjectAuth();
        pauth.setAuriga(auriga);
        pauth.checkAccess(universe.projectID(), session, 3);
        //==================================================================
        //Project needed to alter usage.
        project = prjatlas.getProject(universe.projectID());
        SubSet subset = univatlas.getSubset(universeid, subsetid);
        //******************************************************************
        clearMapFeature(universeid, subset);
        alterUsage("Map feature removed from subset " + subset.getName());
        //------------------------------------------------------------------
        univatlas.commit();
        //------------------------------------------------------------------
    }
    //**********************************************************************
    private void clearMapFeature (long universeid, SubSet subset) throws AppException, Exception {
        UniverseAtlas atlas = auriga.getUniverseAtlas();
        cost += subset.getMapCost();
        atlas.setMapStatus(subset.getSubsetID(), 0);
        atlas.setMapCost(universeid, subset.getSubsetID(), 0);
        atlas.clearMapObject(subset.getSubsetID());
        edits++;
    }
    //**********************************************************************
    private void alterUsage (String description) throws AppException, Exception {
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(cost);
        alter.setStartingEvent(description);
        auriga.getBillingLambda().alterUsage(alter);
        //--------------------------------------------------------------
    }
    //**********************************************************************
}
//**************************************************************************
