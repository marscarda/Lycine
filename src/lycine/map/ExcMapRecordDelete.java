package lycine.map;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.TabList;
import methionine.billing.AlterUsage;
import methionine.project.Project;
import threonine.map.MapFolder;
import threonine.map.MapObject;
import threonine.map.MapRecord;
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
    public void doDelete () throws AppException, Exception {
        //================================================================
        //We check the user has permission to do this.
        MapRecord record = auriga.getMapsLambda().getMapRecord(recordid);
        MapFolder folder = auriga.getMapsLambda().getMapFolder(record.getFolderID());
        if (userid != 0)
            auriga.getProjectLambda().checkAccess(folder.projectID(), userid, 3);
        //================================================================
        Project project = auriga.getProjectLambda().getProject(folder.projectID(), userid);
        //================================================================
        //We need to use the master for this.
        auriga.getMapsLambda().useMaster();
        //================================================================
        TabList tabs = new TabList();
        auriga.getBillingLambda().AddLockAlterUsage(tabs);
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
    private void doObjects (long recordid) throws Exception {
        MapObject[] objects = auriga.getMapsLambda().getObjectsByRecord(recordid, false);
        for (MapObject obj : objects) cost += obj.cost;
        auriga.getMapsLambda().clearMapObjects(recordid);
    }
    //********************************************************************
    
}
//************************************************************************
