package lycine.universe;
//************************************************************************
import threonine.universe.SubSet;
import tryptophan.sample.Sample;
//************************************************************************
public class SubsetLoad {
    //=====================================================
    SubSet subset = null;
    Sample sample = null;
    //=====================================================
    public long subsetID () {
        if (subset == null) return 0;
        return subset.getSubsetID();
    }
    //=====================================================
    public SubSet getSubset () {
        if (subset == null) return new SubSet();
        return subset; 
    }
    //=====================================================
    public long sampleID () {
        if (sample == null) return 0;
        return sample.sampleID();
    }
    //=====================================================
    public Sample getSample () {
        if (sample == null) return new Sample();
        return sample;
    }
    //=====================================================
}
//************************************************************************
