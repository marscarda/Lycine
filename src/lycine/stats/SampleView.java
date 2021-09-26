package lycine.stats;
//************************************************************************
import tryptophan.sample.Sample;
//************************************************************************
public class SampleView extends StatHold {
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
