package lycine.newpackage;
//***************************************************************************
/**
 * Contains variables for a given collect response.
 * @author marian
 */
public class Case {
    //***********************************************************
    long responsecaseid = 0;
    int count = 0;
    Orion[] takenvars = new Orion[0];
    //***********************************************************
    void addTakenVariable (Orion takenvar) {
        //-------------------------------------------------------
        for (int n = 0; n < count; n++) 
            if (takenvar.itemid == takenvars[n].itemid) return;
        //-------------------------------------------------------
        Orion[] newarr = new Orion[count + 1];
        System.arraycopy(takenvars, 0, newarr, 0, count);
        newarr[count] = takenvar;
        count++;
        takenvars = newarr;
        //-------------------------------------------------------
    }
    //***********************************************************
    public Orion[] getTakenVarList () {
        if (takenvars == null) return new Orion[0];
        return takenvars;
    }
    //***********************************************************
}
//***************************************************************************

