package lycine.stats.result;
//************************************************************************

//************************************************************************
public class StatValueDisplay {
    //********************************************************************
    int responsenumber = 0;
    String responseLabel = null;
    float primarivalue = 0;
    //********************************************************************
    public int responseNumber () { return responsenumber; }
    public String getLabel () {
        if (responseLabel == null) return "";
        return responseLabel;
    }
    //********************************************************************
    public float primaryValue () { return primarivalue; }
    public int getPrimaryPercent () { return (int)(primarivalue * 100); }
    //********************************************************************
}
//************************************************************************
