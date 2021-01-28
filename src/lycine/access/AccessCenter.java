package lycine.access;
//***************************************************************************
import methionine.AppException;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import serine.access.AccessLambda;
import serine.access.AccessRecord;
//***************************************************************************
public class AccessCenter {
    //***********************************************************************
    AuthLamda authlambda = null;
    AccessLambda accesslambda = null;
    //====================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setAccessLambda (AccessLambda accesslambda) { this.accesslambda = accesslambda; }
    //***********************************************************************
    /**
     * Creates an access.
     * @param record
     * @throws AppException USERNOTFOUND
     * @throws Exception 
     */
    public void createAccess (AccessRecord record) throws AppException, Exception {
        //=========================================================
        //=========================================================
        //If the user name is empty (len = 0) it is assumed that the access is public.
        if (record.getUserName().length() != 0)
            record.setUserID(authlambda.getUserIdByIdentifier(record.getUserName()));
        //=========================================================
        //=========================================================
        //We create the access record.
        accesslambda.createAccess(record);
        //=========================================================
    }
    //***********************************************************************
    /**
     * Returns a list of access records given an object.
     * @param objtype
     * @param objid
     * @return
     * @throws AppException 
     * @throws Exception 
     */
    public AccessRecord[] getRecords (int objtype, long objid) throws AppException, Exception {
        AccessRecord[] records = accesslambda.getAccessListByObject(objtype, objid);
        User user;
        for (AccessRecord record : records) {
            if (record.getUserID() == 0) {
                continue;
            }
            try {
                user = authlambda.getUser(record.getUserID(), false);
                record.setUserName(user.loginName());
            }
            catch (AppException e) {
                if (e.getErrorCode() == AppException.USERNOTFOUND)
                    continue;
                throw e;
            }
        }
        return records;
    }
    //***********************************************************************
}
//***************************************************************************
