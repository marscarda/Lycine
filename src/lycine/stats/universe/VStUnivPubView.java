package lycine.stats.universe;
//************************************************************************
import lycine.sample.ConstVarValues;
import lycine.stats.VStAlpha;
//************************************************************************
/**
 * Statistic component for variables kind of Public View Candidates.
 * Name stands for Variable Statistic Universe Public View
 * @author marianoscardaccione
 */
public class VStUnivPubView extends VStAlpha {
    //**********************************************************
    int positives = 0;
    int neutrals = 0;
    int negatives = 0;
    int unknowns = 0;
    //**********************************************************
    public void setValue (int value) {
        switch (value) {
            case ConstVarValues.PUBVIEW_POSITIVE: positives++; break;
            case ConstVarValues.PUBVIEW_NEUTRAL: neutrals++; break;
            case ConstVarValues.PUBVIEW_NEGATIVE: negatives++; break;
            case ConstVarValues.PUBVIEW_UNKNOWN: unknowns++; break;
        }
    }
    //**********************************************************
    public int Positives () { return positives; }
    public int Negatives () { return negatives; }
    //**********************************************************
    //Local proportions of responses
    float localpositive = 0;
    float localneutral = 0;
    float localnegative = 0;
    float localunknown = 0;
    //==========================================================
    //Global proportions of responses.
    float globalpositive = 0;
    float globalneutral = 0;
    float globalnegative = 0;
    float globalunknown = 0;
    //**********************************************************
    @Override
    public void calculateLocal () {
        int total = positives + neutrals + negatives + unknowns;
        int aux;
        //--------------------------------------------------
        aux = (int)((positives / (float)total) * 1000);
        localpositive = (float)aux / (float)1000;
        //--------------------------------------------------
        aux = (int)((neutrals / (float)total) * 1000);
        localneutral = (float)aux / (float)1000;
        //--------------------------------------------------
        aux = (int)((negatives / (float)total) * 1000);
        localnegative = (float)aux / (float)1000;
        //--------------------------------------------------
        aux = (int)((unknowns / (float)total) * 1000);
        localunknown = (float)aux / (float)1000;
        //--------------------------------------------------
        System.out.println("Calculate local");
        System.out.println(localpositive);
        System.out.println(localneutral);
        System.out.println(localnegative);
        System.out.println(localunknown);
        //--------------------------------------------------
    }
    //=========================================================
    @Override
    public void calculateGlobal (int localpop, int globalpop) {
        float proppop = (float)localpop / (float)globalpop;
        
        
        
        
        System.out.println("Proportion pop: " + proppop);
        
        
        int aux;
        //--------------------------------------------------
        aux = (int)((localpositive * proppop) * 1000);
        globalpositive = (float)aux / (float)1000;
        //--------------------------------------------------
        aux = (int)((localneutral * proppop) * 1000);
        globalneutral = (float)aux / (float)1000;
        //--------------------------------------------------
        aux = (int)((localnegative * proppop) * 1000);
        globalnegative = (float)aux / (float)1000;
        //--------------------------------------------------
        aux = (int)((localunknown * proppop) * 1000);
        globalunknown = (float)aux / (float)1000;
        //--------------------------------------------------
        System.out.println("Calculate global");
        System.out.println(globalpositive);
        System.out.println(globalneutral);
        System.out.println(globalnegative);
        System.out.println(globalunknown);
    }
    //**********************************************************
}
//************************************************************************
