package domainapp.modules.simple.dom;

public interface WorkFlowStateMachine {

    WithWorkFlow transition(WithWorkFlow domainEntity);

    WithWorkFlow initialise(WithWorkFlow domainEntity);

}
