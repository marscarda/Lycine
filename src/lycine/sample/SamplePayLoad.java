package lycine.sample;
//************************************************************************
import tryptophan.sample.Responder;
import tryptophan.sample.Sample;
//************************************************************************
public class SamplePayLoad {
    //****************************************************
    Sample sample = null;
    void setSample(Sample sample) { this.sample = sample; }
    public Sample getSample () {
        if (sample == null) return new Sample();
        return sample;
    }
    //****************************************************
    Responder[] responses = null;
    public void setResponses (Responder[] responses) { this.responses = responses; }
    public Responder[] getResponses () {
        if (responses == null) return new Responder[0];
        return responses;
    }
    //****************************************************
}
//************************************************************************
