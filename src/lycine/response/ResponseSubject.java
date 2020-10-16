package lycine.response;
//***************************************************************************
import tryptophan.survey.responses.ResponseObject;
import tryptophan.survey.responses.ResponseRow;
//***************************************************************************
public class ResponseSubject {
    //************************************************************
    ResponseObject resprec = null;
    ResponseRow[] responserows = null;
    //************************************************************
    void setResponseRecord (ResponseObject resp) { resprec = resp; }
    void setResponseRows (ResponseRow[] rows) { responserows = rows; }
    //************************************************************
    
    //************************************************************
}
//***************************************************************************
