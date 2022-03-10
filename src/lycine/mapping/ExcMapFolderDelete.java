package lycine.mapping;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.TabList;
import methionine.finance.AlterUsage;
import methionine.project.Project;
import threonine.mapping.MapFolder;
import threonine.mapping.MapObject;
import threonine.mapping.MapRecord;
//************************************************************************
public class ExcMapFolderDelete {
    //********************************************************************
    AurigaObject auriga = null;
    long folderid = 0;
    long userid = 0;
    long projectid = 0;
    float cost = 0;
    //----------------------------------------------
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    public void setFolderId (long folderid) { this.folderid = folderid; }
    public void setUserId (long userid) { this.userid = userid; }
    //********************************************************************
    public void doDelete () throws AppException, Exception {
        //================================================================
        //We check the user has permission to do this.
        MapFolder folder = auriga.getMapsLambda().getMapFolder(folderid);
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
        auriga.getMapsLambda().addLockDeleteFolder(tabs);
        auriga.getMapsLambda().setAutoCommit(0);
        auriga.getMapsLambda().lockTables(tabs);
        //================================================================
        doFolder(folderid);
        //================================================================
        //We alter the usage cost.
        AlterUsage alter = new AlterUsage();
        alter.setProjectId(project.projectID());
        alter.setProjectName(project.getName());
        alter.setDecrease(cost);
        alter.setStartingEvent("Map Folder '" + folder.getName() + "' Destroyed");
        auriga.getBillingLambda().alterUsage(alter);
        //================================================================
        //We are all done
        auriga.getMapsLambda().commit();
        auriga.getMapsLambda().unLockTables();
        //================================================================
    }
    //********************************************************************
    private void doFolder (long folderid) throws AppException, Exception {
        MapFolder[] folders = auriga.getMapsLambda().getChildrenFolders(projectid, folderid);
        //======================================================
        for (MapFolder folder : folders)
            doFolder(folder.getID());
        //======================================================
        doRecords(folderid);
        auriga.getMapsLambda().deleteFolder(folderid);
        auriga.getMapsLambda().deleteFolderUsage(folderid, 0);
        //======================================================
    }
    //********************************************************************
    private void doRecords (long folderid) throws AppException, Exception {
        MapRecord[] records = auriga.getMapsLambda().getMapRecords(folderid);
        for (MapRecord record : records) {
            doObjects(record.getID());
        }
        auriga.getMapsLambda().deleteMapRecord(folderid, 0);
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
