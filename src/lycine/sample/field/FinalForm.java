package lycine.sample.field;
//************************************************************************
import tryptophan.design.Form;
import tryptophan.design.FormQuestion;
//************************************************************************
public class FinalForm {
    //******************************************************
    Form form = null;
    FormQuestion[] questions = null;
    //======================================================
    public Form getForm () {
        if (form == null) return new Form();
        return form;
    }
    //------------------------------------------------------
    public FormQuestion[] getQuestions () {
        if (questions == null) return new FormQuestion[0];
        return questions;
    }
    //******************************************************
    String labelpubviewf = null;
    String labelpubviewpos = null;
    String labelpubviewneu = null;
    String labelpubviewneg = null;
    String labelpubviewunk = null;
    //======================================================
    public String labelPuvViewFormlt () {
        if (labelpubviewf == null) return "";
        return labelpubviewf;
    }
    //======================================================
    public String labelPubViewPositive () {
        if (labelpubviewpos == null) return "";
        return labelpubviewpos;
    }
    //======================================================
    public String labelPubViewNeutral () {
        if (labelpubviewneu == null) return "";
        return labelpubviewneu;
    }
    //======================================================
    public String labelPubViewNegative () {
        if (labelpubviewneg == null) return "";
        return labelpubviewneg;
    }
    //======================================================
    public String labelPubViewUnknown () {
        if (labelpubviewunk == null) return "";
        return labelpubviewunk;
    }
    //******************************************************
}
//************************************************************************
