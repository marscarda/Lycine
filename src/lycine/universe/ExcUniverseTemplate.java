package lycine.universe;
//**************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import threonine.universe.Universe;
//**************************************************************************
public class ExcUniverseTemplate {
    //**********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //======================================================================
    //When get children subsets. We set the map status of current subset.
    boolean mapstatus = false;
    public boolean mapStatus () { return mapstatus; }
    //**********************************************************************
    /**
     * Returns a universe given its ID
     * @param universeid
     * @return
     * @throws AppException
     * @throws Exception 
     */
    public Universe getTemplate (long universeid) throws AppException, Exception {
        Universe universe = auriga.templateUniverseAtlas().getTemplate(universeid);
        return universe;
        //==================================================================
    }    
    //**********************************************************************
}
//**************************************************************************
