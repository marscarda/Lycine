package lycine.map;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.project.Project;

import threonine.map.MapFolder;
import threonine.map.MapRecord;
//************************************************************************
public class ExcMapFolderDelete {
    //********************************************************************
    AurigaObject auriga = null;
    long folderid = 0;
    long userid = 0;
    long projectid = 0;
    //----------------------------------------------
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    public void setFolderId (long folderid) { this.folderid = folderid; }
    public void setUserId (long userid) { this.userid = userid; }
    //********************************************************************
    public void doDelete () throws AppException, Exception {
        //================================================================
        //We check the user has permission to do this.
        if (userid != 0) {
            MapFolder folder = auriga.getMapsLambda().getMapFolder(folderid);
            auriga.getProjectLambda().checkAccess(folder.projectID(), userid, 3);
        }
        //================================================================
        //We need to use the master for this.
        auriga.getMapsLambda().useMaster();
        
        
        doFolder(folderid);



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
            //Do Objects
        }
        
        auriga.getMapsLambda().deleteMapRecord(folderid, 0);
        





    }
    //********************************************************************
}
//************************************************************************
