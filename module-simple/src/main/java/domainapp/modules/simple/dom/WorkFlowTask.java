package domainapp.modules.simple.dom;

import java.util.List;

public interface WorkFlowTask {

    WithWorkFlow attachedTo();

    List<WorkFlowState> applicableForStates();

    WorkFlowRole assignedToRole();

    WorkFlowActor assignedToActor();

}
