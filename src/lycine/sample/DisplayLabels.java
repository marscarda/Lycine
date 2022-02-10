package lycine.sample;
//************************************************************************
import tryptophan.design.CustomLabel;
//************************************************************************
public class DisplayLabels {
    //********************************************************************
    //Public View
    //====================================================================
    String pubviewformulation = null;
    String pubviewpositive = null;
    String pubviewneutral = null;
    String pubviewnegative = null;
    String pubviewunknown = null;
    //====================================================================
    public String pubviewFormulation () { 
        if (pubviewformulation == null) return "";
        return pubviewformulation; 
    }
    public String pubViewPositive () { 
        if (pubviewpositive == null) return "";
        return pubviewpositive; 
    }
    public String pubViewNeutral () {
        if (pubviewneutral == null) return "";
        return pubviewneutral; 
    }
    public String pubViewNegative () { 
        if (pubviewnegative == null) return "";
        return pubviewnegative; 
    }
    public String pubViewUnknown () { 
        if (pubviewunknown == null) return "";
        return pubviewunknown; 
    }
    //********************************************************************
}
//************************************************************************
