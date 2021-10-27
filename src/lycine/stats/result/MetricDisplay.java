package lycine.stats.result;
//************************************************************************

//************************************************************************
/**
 * holds data for a particular metric to be displayed.
 * @author marianoscardaccione
 */
public class MetricDisplay {
    //****************************************************************
    int metrictype = 0;
    String label = null;
    StatValueDisplay[] values = null;
    //****************************************************************
    public int metricType () { return metrictype; }
    public String getLabel () {
        if (label == null) return "";
        return label;
    }
    public StatValueDisplay[] getValues () {
        if (values == null) return new StatValueDisplay[0];
        return values;
    }
    //****************************************************************
}
//************************************************************************
