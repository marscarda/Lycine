package lycine.billing;
//***************************************************************************
import methinine.billing.BillingLambda;
import methinine.billing.BillingPeriod;
import methionine.AppException;
import methionine.project.Project;
import methionine.project.ProjectLambda;
//***************************************************************************
public class PeriodBiller {
    //***********************************************************************
    private static final int CPD_PROJECT = 500;
    //***********************************************************************
    BillingLambda billinglambda = null;
    ProjectLambda projectlambda = null;
    //=======================================================================
    public void setBillingLambda (BillingLambda lambda) { billinglambda = lambda; }
    public void setProjectLambda (ProjectLambda lambda) { projectlambda = lambda; }
    //***********************************************************************
    public void doBilling (int count) throws Exception {
        //---------------------------------------------------------------
        int c = billinglambda.getBillingPeriodsCountByStatus(0);
        if (count > c) count = c;
        //---------------------------------------------------------------
        //We close the number of open usage periods.
        for (int n = 0; n < count; n++)
            billinglambda.doBillingStage1();
        //---------------------------------------------------------------
        while (true){
            BillingPeriod period = billinglambda.startBillingPeriodStage2();
            if (period == null) break;
            this.qqm(period);
            billinglambda.doBillingStage2(period);
        }
        //---------------------------------------------------------------
    }
    //***********************************************************************
    private void qqm (BillingPeriod period) throws Exception {
        switch (period.itemCode()) {
            //-------------------------------------------------
            case 1: {
                try {
                    Project project = projectlambda.getWorkTeam(period.itemID(), 0);
                    period.setItemType("Project: ");
                    period.setItemName(project.getName());
                    period.setCostPerDay(CPD_PROJECT);
                    return;
                }
                catch (AppException e) { period.setItemName("Deleted"); }
            }
            break;
            //-------------------------------------------------
        }
    }
    //***********************************************************************
    
    
    
    
    //***********************************************************************
    
}
//***************************************************************************
