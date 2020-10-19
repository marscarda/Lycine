package lycine.tobenamed;
//****************************************************************************

import tryptophan.survey.baseform.ActionItemType;

public class ReactionMetricPublicView extends ReactionMetric {
    //*******************************************************************
    public ReactionMetricPublicView () { vartype = ActionItemType.PUBIMAGE; }
    //*******************************************************************
    private int positives = 0;
    private int negatives = 0;
    private int neutrals = 0;
    private int ignorances = 0;
    private int total = 0;
    //*******************************************************************
    public void addReactionValue (int value) {
        switch (value) {
            case 1: negatives++; break;
            case 2: neutrals++; break;
            case 3: positives++; break;
            case 4: ignorances++; break;
        }
        if (value > 0 && value < 4) total++;
    }
    //*******************************************************************
    
    //*******************************************************************
}
//****************************************************************************
