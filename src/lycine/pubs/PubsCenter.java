package lycine.pubs;
//***************************************************************************
import methionine.AppException;
import methionine.auth.AuthLamda;
import serine.access.AccessLambda;
import serine.access.AccessRecord;
import serine.pubs.object.ObjectPub;
import serine.pubs.object.ObjectPubLambda;
//***************************************************************************
public class PubsCenter {
    //***********************************************************************
    AuthLamda authlambda = null;
    AccessLambda accesslambda = null;
    ObjectPubLambda objectpublambda = null;
    //=======================================================================
    public void setAuthLambda (AuthLamda authlambda) { this.authlambda = authlambda; }
    public void setAccessLambda (AccessLambda accesslambda) { this.accesslambda = accesslambda; }
    public void setObjectPubLambda (ObjectPubLambda objectpublambda) { this.objectpublambda = objectpublambda; }
    //***********************************************************************
    public void createObjectPub (ObjectPub pub) throws AppException, Exception {
        AccessRecord accrecord = accesslambda.getAccessRecordByName(pub.accessName());
        if (!accrecord.isPublic())
            throw new AppException("Not public", 0);
        pub.setAccessID(accrecord.getID());
        objectpublambda.createObjectPub(pub);
    }
    //***********************************************************************
}
//***************************************************************************
