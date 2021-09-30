package lycine.stats;
//************************************************************************
import tryptophan.sample.Sample;
//************************************************************************
/**
 * Holds variable stats.
 * See class that is inherited where the stats are actually held.
 * this subclass also holds a sample.
 * typically used to at the stage of displaying stats of single and plain sample.
 * @author marianoscardaccione
 */
public class SampleView extends StatAlpha {
    //********************************************************************
    Sample sample = null;
    public void setSample (Sample sample) { this.sample = sample; }
    public Sample getSample () { 
        if (sample == null) return new Sample();
        return sample; 
    }
    //********************************************************************
}
//************************************************************************
