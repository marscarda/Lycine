package lycine.response;
//***************************************************************************
public class PublicViewVarStat extends ItemVarStat {
    //**************************************************
    int positives = 0;
    int negatives = 0;
    int neutrals = 0;
    int unknowns = 0;
    //**************************************************
    public void addResponse (int value) {
        switch (value) {
            case 1: negatives++; break;
            case 2: neutrals++; break;
            case 3: positives++; break;
            case 4: unknowns++; break;
        }
    }
    //**************************************************
    
    
    
    //==================================================
    
    
    //**************************************************
}
//***************************************************************************
