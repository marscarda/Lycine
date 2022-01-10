package lycine.trial;
//************************************************************************
import histidine.AurigaObject;
import histidine.trial.build.TrialBuilder;
import methionine.AppException;
import methionine.project.ProjectErrorCodes;
import threonine.universe.Universe;
import tryptophan.trial.PlayRoom;
import tryptophan.trial.Trial;
//************************************************************************
public class ExcTrial {
    AurigaObject auriga = null;
    public void setAuriga(AurigaObject auriga) { this.auriga = auriga; }
    //********************************************************************
    public void createTrial (Trial trial, long userid) throws AppException, Exception {
        //****************************************************************
        if ( trial.getName().length() == 0)
            throw new AppException("Name cannot be empty", AppException.INVALIDDATASUBMITED);
        //****************************************************************
        //We check the user has access to the project.
        auriga.projectAtlas().checkAccess(trial.projectID(), userid, 2);
        //****************************************************************
        //These checks are probablu futile. They are ccarried out thogh.
        //================================================================
        PlayRoom space = auriga.getTrialAtlas().getEnvironment(trial.trialSpaceID());
        if (space.projectID() != trial.projectID())
            throw new AppException("Project inconcistency", ProjectErrorCodes.ENTITYPROJECTINCONCISTENCY);
        //---------------------------------------------------------------
        Universe universe = auriga.getUniverseAtlas().getUniverse(space.universeID());
        if (universe.projectID() != trial.projectID())
            throw new AppException("Project inconcistency", ProjectErrorCodes.ENTITYPROJECTINCONCISTENCY);
        trial.setUniverseId(universe.universeID());        
        //****************************************************************
        //We create the trial
        
        //Balance check and locking tables needed.
        
        auriga.getNewAtlas().createTrial(trial);
        
        //****************************************************************
        //We trigger the building.
        TrialBuilder builder = new TrialBuilder();
        builder.setTrialID(trial.getID());
        builder.start();
        //****************************************************************
    }
    //********************************************************************
}
//************************************************************************
