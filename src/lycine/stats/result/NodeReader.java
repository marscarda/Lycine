package lycine.stats.result;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import tryptophan.design.DesignErrorCodes;
import tryptophan.design.Metric;
import tryptophan.trial.MetricRef;
import tryptophan.trial.MetricValue;
import tryptophan.trial.StatNode;
//************************************************************************
/**
 * Execution of reading metrics for a particular node and prepare them
 * to be displayed on demand.
 * @author marianoscardaccione
 */
public class NodeReader {
    //********************************************************************
    private AurigaObject auriga = null;
    MetricDisplay[] metricdisplays = new MetricDisplay[0];
    //********************************************************************
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    public MetricDisplay[] getMetricDisplays () { return metricdisplays; }
    //********************************************************************
    public void buildNodeMetricViews (long nodeid) throws AppException, Exception {
        //***********************************************************
        //MetricDisplay metricdisplay = new MetricDisplay();
        StatNode node = auriga.getNewAtlas().getStatNode(0, nodeid);
        //***********************************************************
        MetricRef [] metricrefs = auriga.getNewAtlas().getMetricReferences(nodeid);
        for (MetricRef mref : metricrefs) {
            newMetricDisplay(mref);
        }
        //***********************************************************
    }
    //********************************************************************
    private void newMetricDisplay (MetricRef metricref) throws AppException, Exception {
        //**************************************************
        Metric metric;
        try { metric = auriga.getDesignLambda().getVariable(metricref.metricID()); }
        catch (AppException e) {
            if (e.getErrorCode() != DesignErrorCodes.METRICNOTFOUND) throw e;
            return;
        }
        //**************************************************
        MetricDisplay metricdisplay = new MetricDisplay();
        metricdisplay.metrictype = metric.variableType();
        metricdisplay.label = metric.getLabel();
        //**************************************************
        MetricValue[] values = auriga.getNewAtlas().getMetricValues(metricref.metricRefID());
        int count = values.length;
        StatValueDisplay[] nc = new StatValueDisplay[count];
        
        for (int n = 0; n < count; n++) {
            nc[n] = new StatValueDisplay();
            nc[n].responsenumber = values[n].responseNumber();
            nc[n].primarivalue = values[n].primaryValue();
        }

        setResponseLabels(nc);
        metricdisplay.values = nc;
        //**************************************************
        addMetricDisplayToArr(metricdisplay);
        //**************************************************
    }
    //********************************************************************
    
    //********************************************************************
    private void addMetricDisplayToArr (MetricDisplay metric) {
        int count = metricdisplays.length;
        MetricDisplay[] newarr = new MetricDisplay[count + 1];
        System.arraycopy(metricdisplays, 0, newarr, 0, count);
        newarr[count] = metric;
        metricdisplays = newarr;
    }
    //********************************************************************
    //BELOW THE METHODS THAT FILLS THE RESPONSE LABELS.
    //********************************************************************
    /**
     * Very simple for now. Just assign the labels for Public View Type Metrics.
     * @param ncs 
     */
    private void setResponseLabels (StatValueDisplay[] ncs) {
        for (StatValueDisplay nc : ncs) {
            switch (nc.responsenumber) {
                case 1: nc.responseLabel = "Positive"; break;
                case 2: nc.responseLabel = "Neutral"; break;
                case 3: nc.responseLabel = "Negative"; break;
                case 4: nc.responseLabel = "Unknown"; break;
            }
        }
    }
    //********************************************************************
    
    //********************************************************************
}
//************************************************************************
