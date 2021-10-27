package lycine.devstats;
//************************************************************************
import histidine.AurigaObject;
import methionine.AppException;
import tryptophan.trial.MetricRef;
import tryptophan.trial.StatNode;
//************************************************************************
public class NodeReader {
    //********************************************************************
    private AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public MetricDisplay[] getNodeMetricView (long nodeid) throws AppException, Exception {
        //***********************************************************
        //MetricDisplay metricdisplay = new MetricDisplay();
        StatNode node = auriga.getNewAtlas().getStatNode(0, nodeid);
        //***********************************************************
        MetricRef [] metricrefs = auriga.getNewAtlas().getMetricReferences(nodeid);
        int count = metricrefs.length;
        MetricDisplay[] metricdisplay = new MetricDisplay[count];



        for (int n = 0; n < count; n++) {
            metricdisplay[n] = new MetricDisplay();
            metricdisplay[n].label = "Label here";
        }



        //***********************************************************
        return metricdisplay;
    }
    //********************************************************************
}
//************************************************************************
