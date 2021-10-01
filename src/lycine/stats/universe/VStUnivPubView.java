package lycine.stats.universe;
//************************************************************************
//************************************************************************

import lycine.sample.ConstVarValues;

/**
 * Statistic component for variables kind of Public View Candidates.
 * Name stands for Variable Statistic Universe Public View
 * @author marianoscardaccione
 */
public class VStUnivPubView extends VStUnivAlpha {
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
    
    
    
    public void setPositives(int p) { positives = p; }
    public void setNeutrals (int n) { neutrals = n; }
    public void setNegatives (int n) { negatives = n; }
    public void setUnknowns (int u) { unknowns = u; } 
    //**********************************************************
    //Local proportions of responses
    float localpositive = 0;
    float localneutral = 0;
    float localnegtive = 0;
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
        System.out.println("calculate local");
    }
    //=========================================================
    @Override
    public void calculateGlobal () {
        System.out.println("Calculate global");
        
    }
    //**********************************************************
}
//************************************************************************
