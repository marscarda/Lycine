package lycine.samplemetric;
//****************************************************************************
import tryptophan.survey.ActionItemType;
//****************************************************************************
public class ReactionMetricPublicView extends ReactionMetric {
    //*******************************************************************
    public ReactionMetricPublicView () { vartype = ActionItemType.PUBIMAGE; }
    //*******************************************************************
    private int positives = 0;
    private int negatives = 0;
    private int neutrals = 0;
    private int ignorances = 0;
    private int known = 0;
    private int total = 0;
    //*******************************************************************
    public void addReactionValue (int value) {
        switch (value) {
            case 1: positives++; break;
            case 2: neutrals++; break;
            case 3: negatives++; break;
            case 4: ignorances++; break;
        }
        if (value > 0 && value < 5) total++;
        if (value > 0 && value < 4) known++;
    }
    //*******************************************************************
    public int positiveMetric () {
        if (total == 0) return 0;
        float prop = (float)positives / total;
        return (int)(prop * 100);
    }
    //============================================================
    public int neutralMetric () {
        if (total == 0) return 0;
        float prop = (float)neutrals / total;
        return (int)(prop * 100);
    }
    //============================================================
    public int negativeMetric () {
        if (total == 0) return 0;
        float prop = (float)negatives / total;
        return (int)(prop * 100);
    }
    //============================================================
    public int ignoranceMetric () {
        if (total == 0) return 0;
        float prop = (float)ignorances / total;
        return (int)(prop * 100);
    }
    //============================================================
    public int knowledgeMetric () {
        if (total == 0) return 0;
        float prop = (float)known / total;
        return (int)(prop * 100);
    }
    //============================================================
    public int onknownPositive () {
        if (known == 0) return 0;
        float prop = (float)positives / known;
        return (int)(prop * 100);
    }
    //============================================================
    public int onknownNeutral () {
        if (known == 0) return 0;
        float prop = (float)neutrals / known;
        return (int)(prop * 100);
    }
    //============================================================
    public int onknownNegative () {
        if (known == 0) return 0;
        float prop = (float)negatives / known;
        return (int)(prop * 100);
    }
    //*******************************************************************
}
//****************************************************************************
