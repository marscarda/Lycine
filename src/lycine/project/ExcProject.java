package lycine.project;
//************************************************************************
import histidine.AurigaObject;
import java.util.Calendar;
import java.util.TimeZone;
import methionine.AppException;
import methionine.Celaeno;
import methionine.TabList;
import methionine.auth.AuthErrorCodes;
import methionine.auth.AuthLamda;
import methionine.auth.User;
import methionine.finance.BalanceInfo;
import methionine.finance.BillingErrorCodes;
import methionine.finance.BillingLambda;
import methionine.finance.UsageCost;
import methionine.finance.UsagePeriod;
import methionine.project.Project;
import methionine.project.ProjectLambda;
//************************************************************************
public class ExcProject {
    //********************************************************************
    AurigaObject auriga = null;
    public void setAuriga (AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    /**
     * Creates a project for a user.
     * @param project
     * @throws AppException
     * @throws Exception 
     */
    public void createProject (Project project) throws AppException, Exception {
        //*****************************************************
        //We check the user has confirmed their email
        User user = auriga.getAuthLambda().getUser(project.getOwner());
        if (!user.emailConfirmed()) 
            throw new AppException("Email address not confirmed", AuthErrorCodes.EMAILNOTCONFIRMED);
        //*****************************************************
        //We check the user is able to spend.
        BalanceInfo balance = auriga.getBillingLambda().getTotalBalance(project.getOwner());
        float minbalance = UsageCost.MAXDEBT * (-1);
        if (balance.getTotalBalance() < minbalance)
            throw new AppException("Balance Insuficient", BillingErrorCodes.BALANCEINSUFICIENT);
        //*****************************************************
        project.setDayCost(UsageCost.PROJECT);
        ProjectLambda projatlas = auriga.projectAtlas();
        BillingLambda fincatlas = auriga.getBillingLambda();
        AuthLamda authatlas = auriga.getAuthLambda();
        authatlas.useMaster();
        //*****************************************************
        TabList tablist = new TabList();
        projatlas.setAutoCommit(0);
        authatlas.lockUsers(tablist);
        projatlas.lockProjects(tablist);
        fincatlas.lockUsagePeriods(tablist);
        projatlas.lockTables(tablist);
        //*****************************************************
        authatlas.checkUserExists(project.getOwner());
        auriga.projectAtlas().createProject(project);
        //------------------------------------------------
        UsagePeriod period = new UsagePeriod();
        period.setUserID(project.getOwner());
        period.setProjectID(project.projectID());
        period.setCostPerDay(UsageCost.PROJECT);
        period.setProjectName(project.getName());
        period.setStartingEvent("Project created");
        //------------------------------------------------
        auriga.getBillingLambda().startUsage(period);
        //*****************************************************
        //We are all done.
        auriga.projectAtlas().commit();
        //*****************************************************
    }
    //********************************************************************
}
//************************************************************************
