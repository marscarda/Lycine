package lycine.viewmake;
//************************************************************************

import lycine.sample.ConstVarValues;


//************************************************************************
public class VarStatPublicView extends VarStatAlpha {
    //***********************************
    int positives = 0;
    int neutrals = 0;
    int negativees = 0;
    int unknowns = 0;
    //***********************************
    void setValue (int value) {
        switch (value) {
            case ConstVarValues.PUBVIEW_POSITIVE: positives++; break;
            case ConstVarValues.PUBVIEW_NEUTRAL: neutrals++; break;
            case ConstVarValues.PUBVIEW_NEGATIVE: negativees++; break;
            case ConstVarValues.PUBVIEW_UNKNOWN: unknowns++; break;
        }
    }
    //***********************************
}
//************************************************************************
