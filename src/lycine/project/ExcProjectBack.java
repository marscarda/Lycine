package lycine.project;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import methionine.auth.AuthErrorCodes;
import methionine.auth.User;
import methionine.project.Project;
//************************************************************************
public class ExcProjectBack {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    /**
     * Returns projects by User or By simple existence.
     * @param userid
     * @param offset
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Project[] getProjectList (long userid, int offset) throws AppException, Exception {
        //============================================================
        Project[] projects = auriga.projectAtlas().getProjects(userid, offset);
        //============================================================
        int count = projects.length;
        User user;
        for (int n = 0; n < count; n++) {
            try {
                user = auriga.getAuthLambda().getUser(projects[n].getOwner());
                projects[n].setOwnerName(user.loginName());
            }
            catch (AppException e) {
                if (e.getErrorCode() == AuthErrorCodes.USERNOTFOUND) projects[n].setOwnerName("-- User not found --");
            }
        }
        //============================================================
        return projects;
        //============================================================
    }
    //********************************************************************
}
//************************************************************************
