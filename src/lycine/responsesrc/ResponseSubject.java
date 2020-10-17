package lycine.responsesrc;
//***************************************************************************
import tryptophan.survey.responses.ResponseObject;
import tryptophan.survey.responses.ResponseRow;
//***************************************************************************
public class ResponseSubject {
    //************************************************************
    private ResponseObject resprec = null;
    private ResponseRow[] responserows = null;
    //************************************************************
    void setResponseRecord (ResponseObject resp) { resprec = resp; }
    void setResponseRows (ResponseRow[] rows) { responserows = rows; }
    //************************************************************
    int itemCount () {
        if (responserows == null) return 0;
        return responserows.length;
    }
    //============================================================
    ResponseRow[] responseItems () {
        if (responserows == null) return new ResponseRow[0];
        return responserows;
    }
    //************************************************************
}
//***************************************************************************
