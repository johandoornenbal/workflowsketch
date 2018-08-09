package domainapp.modules.simple.dom;

public interface WorkFlowTaskService {

    void maintainTasksFor(final WithWorkFlow domainEntity, final WorkFlowActor actor);

}
