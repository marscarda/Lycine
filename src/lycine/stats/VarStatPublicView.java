package lycine.stats;
//************************************************************************
import lycine.stats.VarStatAlpha;
import lycine.sample.ConstVarValues;
//************************************************************************
public class VarStatPublicView extends VarStatAlpha {
    //***********************************
    int positives = 0;
    int neutrals = 0;
    int negativees = 0;
    int unknowns = 0;
    //-----------------------------------
    int proppositive = 0;
    int propneutral = 0;
    int propnegative = 0;
    int propunknown = 0;
    int propknowledge = 0;
    //***********************************
    public void setValue (int value) {
        switch (value) {
            case ConstVarValues.PUBVIEW_POSITIVE: positives++; break;
            case ConstVarValues.PUBVIEW_NEUTRAL: neutrals++; break;
            case ConstVarValues.PUBVIEW_NEGATIVE: negativees++; break;
            case ConstVarValues.PUBVIEW_UNKNOWN: unknowns++; break;
        }
    }
    //***********************************
    public int getPositives () { return positives; }
    public int getNeutrals () { return neutrals; }
    public int getNegatives () { return negativees; }
    public int getUnknowns () { return unknowns; }
    //***********************************
    public void doTotalCalculate () {
        int total = positives + neutrals + negativees + unknowns;
        float prop = (float)positives / (float)total;
        proppositive = (int)(prop * (float)100);
        prop = (float)neutrals / (float)total;
        propneutral = (int)(prop * (float)100); 
        prop = (float)negativees / (float)total;
        propnegative = (int)(prop * (float)100);
        propunknown = 100 - proppositive - propneutral - propnegative;
        propknowledge = 100 - propunknown;
    }
    public void doOnKnowledgeCalculate () {
        int total = positives + neutrals + negativees;
        float prop = (float)positives / (float)total;
        proppositive = (int)(prop * (float)100);
        prop = (float)negativees / (float)total;
        propnegative = (int)(prop * (float)100); 
        propneutral = 100 - proppositive - propnegative;
    }
    //***********************************
    public int propPositive () { return proppositive; }
    public int propNeutral () { return propneutral; }
    public int propNegative () { return propnegative; }
    public int propUnknown () { return propunknown; }
    public int getKnowledge () { return propknowledge; }
    //***********************************
}
//************************************************************************
