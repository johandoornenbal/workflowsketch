package domainapp.modules.simple.dom;

import java.util.ArrayList;
import java.util.List;

public abstract class WorkFlowStateMachineAbstract implements WorkFlowStateMachine {

    @Override
    public WithWorkFlow transition(WithWorkFlow domainEntity){

        if (domainEntity.getWorkFlowState()==null){
            initialise(domainEntity);
        }

        List<WorkFlowState> statesVisited = new ArrayList<>();
        while (!statesVisited.contains(domainEntity.getWorkFlowState())){
            statesVisited.add(domainEntity.getWorkFlowState());
            WorkFlowState nextState = transitionToNext(domainEntity);
            domainEntity.setWorkFlowState(nextState);
        }

        return domainEntity;

    }

    /**
     * Note: This method iterates over all available states to transition to. The first state with a satisfied guard will
     * be transitioned to. So the order of elements in the List<TargetAndGuard> of {@link WorkFlowState#canTransitionTo() WorkFlowState#canTransitionTo} does matter
     * in cases where more than one guard is satisfied.
     *
     * @param domainEntity
     * @return
     */
    private WorkFlowState transitionToNext(final WithWorkFlow domainEntity) {
        for (TargetAndGuard targetAndGuard : domainEntity.getWorkFlowState().canTransitionTo()){
            if (targetAndGuard.getGuard().isSatisfiedFor(domainEntity)){
                return targetAndGuard.getTarget();
            }
        }
        return domainEntity.getWorkFlowState();
    }

    @Override
    public abstract WithWorkFlow initialise(WithWorkFlow domainEntity);

}
