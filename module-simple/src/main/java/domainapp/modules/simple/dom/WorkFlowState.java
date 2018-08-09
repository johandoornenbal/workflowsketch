package domainapp.modules.simple.dom;

import java.util.List;

public interface WorkFlowState {

    List<TargetAndGuard> canTransitionTo();

    WorkFlowRole hasTaskFor();

}
