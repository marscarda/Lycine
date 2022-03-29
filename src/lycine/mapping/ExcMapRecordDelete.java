package lycine.mapping;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.TabList;
import methionine.finance.AlterUsage;
import methionine.project.Project;
import threonine.mapping.MapFolder;
import threonine.mapping.MapFeature;
import threonine.mapping.MapRecord;
//************************************************************************
public class ExcMapRecordDelete {
    //********************************************************************
    AurigaObject auriga = null;
    long recordid = 0;
    long userid = 0;
    long projectid = 0;
    float cost = 0;
    //--------------------------------------------------------------------
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    public void setRecordId (long recordid) { this.recordid = recordid; }
    public void setUserId (long userid) { this.userid = userid; }
    //********************************************************************
    public void doRecordDelete () throws AppException, Exception {
        //================================================================
        //We check the user has permission to do this.
        MapRecord record = auriga.getMapsLambda().getMapRecord(recordid);
        MapFolder folder = auriga.getMapsLambda().getMapFolder(record.layerID());
        if (userid != 0)
            auriga.projectAtlas().checkAccess(folder.projectID(), userid, 3);
        //================================================================
        Project project = auriga.projectAtlas().getProject(folder.projectID(), userid);
        //================================================================
        //We need to use the master for this.
        auriga.getMapsLambda().useMaster();
        //================================================================
        TabList tabs = new TabList();
        auriga.getBillingLambda().lockAlterUsage(tabs);
        auriga.getMapsLambda().addLockDeleteRecord(tabs);
        auriga.getMapsLambda().setAutoCommit(0);
        auriga.getMapsLambda().lockTables(tabs);
        //================================================================
        record = auriga.getMapsLambda().getMapRecord(recordid);
        doObjects(record.getID());
        //================================================================
        auriga.getMapsLambda().deleteMapRecord(0, recordid);
        //================================================================
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(cost);
        alter.setStartingEvent("Map record '" + record.getName() + "' Destroyed");
        auriga.getBillingLambda().alterUsage(alter);
        //================================================================
        //We are all done
        auriga.getMapsLambda().commit();
        //================================================================
    }    
    //********************************************************************
    public void doClearObjects () throws AppException, Exception {
        //================================================================
        //We check the user has permission to do this.
        MapRecord record = auriga.getMapsLambda().getMapRecord(recordid);
        MapFolder folder = auriga.getMapsLambda().getMapFolder(record.layerID());
        if (userid != 0)
            auriga.projectAtlas().checkAccess(folder.projectID(), userid, 3);
        //================================================================
        Project project = auriga.projectAtlas().getProject(folder.projectID(), userid);
        //================================================================
        //We need to use the master for this.
        auriga.getMapsLambda().useMaster();
        //================================================================
        TabList tabs = new TabList();
        auriga.getBillingLambda().lockAlterUsage(tabs);
        auriga.getMapsLambda().addLockDeleteRecord(tabs);
        auriga.getMapsLambda().setAutoCommit(0);
        auriga.getMapsLambda().lockTables(tabs);
        //================================================================
        record = auriga.getMapsLambda().getMapRecord(recordid);
        doObjects(record.getID());
        //================================================================
        //auriga.getMapsLambda().deleteMapRecord(0, recordid);
        //================================================================
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(cost);
        alter.setStartingEvent("Map Object Cleared from '" + record.getName() + "'");
        auriga.getBillingLambda().alterUsage(alter);
        //================================================================
        //We are all done
        auriga.getMapsLambda().commit();
        //================================================================
    }
    //********************************************************************
    private void doObjects (long recordid) throws Exception {
        MapFeature[] objects = auriga.getMapsLambda().getObjectsByRecord(recordid, false);
        for (MapFeature obj : objects) cost += obj.cost;
        auriga.getMapsLambda().clearMapObjects(recordid);
    }
    //********************************************************************
    
}
//************************************************************************
