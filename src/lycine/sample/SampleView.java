package lycine.sample;
//************************************************************************

import tryptophan.sample.Sample;


//************************************************************************
public class SampleView {
    //****************************************************
    Sample sample = null;
    void setSample(Sample sample) { this.sample = sample; }
    //====================================================
    int responsecount = 0;
    ResponseView[] responseviews = new ResponseView[0];
    //****************************************************
    void addResponseView (ResponseView respview) {
        ResponseView[] newarr = new ResponseView[responsecount + 1];
            System.arraycopy(responseviews, 0, newarr, 0, responsecount);
            newarr[responsecount] = respview;
            responseviews = newarr;
            responsecount++;
    }
    //****************************************************
}
//************************************************************************
