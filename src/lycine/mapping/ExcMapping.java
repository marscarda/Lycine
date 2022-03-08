package lycine.mapping;
//************************************************************************
import histidine.AurigaObject;
import methionine.auth.User;
import methionine.project.Project;
import threonine.map.MapFolder;
//************************************************************************
public class ExcMapping {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public MapFolder[] getPublicFolders (String searchkey) throws Exception {
        //-------------------------------------------------
        Project project;
        User user;
        //-------------------------------------------------
        MapFolder[] folders = auriga.getMapsLambda().getPublicList(searchkey);
        for (MapFolder folder : folders) {
            project = auriga.projectAtlas().getProject(folder.projectID());
            user = auriga.getAuthLambda().getUser(project.getOwner());
            folder.setUserID(user.userID());
            folder.setUserName(user.loginName());
        }
        //-------------------------------------------------
        return folders;
        //-------------------------------------------------
    }
    //********************************************************************    
    
    
    //********************************************************************
}
//************************************************************************
