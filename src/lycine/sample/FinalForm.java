package lycine.sample;
//************************************************************************
import tryptophan.design.Form;
import tryptophan.design.FormQuestion;
//************************************************************************
public class FinalForm {
    //======================================================
    Form form = null;
    FormQuestion[] questions = null;
    //======================================================
    public Form getForm () {
        if (form == null) return new Form();
        return form;
    }
    //======================================================
    public FormQuestion[] getQuestions () {
        if (questions == null) return new FormQuestion[0];
        return questions;
    }
    //======================================================
}
//************************************************************************
