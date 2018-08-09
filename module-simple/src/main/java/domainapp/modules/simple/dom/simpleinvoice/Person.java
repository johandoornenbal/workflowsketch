package domainapp.modules.simple.dom.simpleinvoice;

import java.util.Arrays;
import java.util.List;

import domainapp.modules.simple.dom.WorkFlowActor;
import domainapp.modules.simple.dom.WorkFlowRole;

public enum Person implements WorkFlowActor {

    JEANNE {
        @Override public List<WorkFlowRole> hasRoles() {
            return Arrays.asList(Role.ADMINISTRATOR);
        }
    },
    AMBROISE {
        @Override public List<WorkFlowRole> hasRoles() {
            return Arrays.asList(Role.MANAGER);
        }
    },
    OSCAR {
        @Override public List<WorkFlowRole> hasRoles() {
            return Arrays.asList(Role.MANAGER);
        }
    },
    PASCAL {
        @Override public List<WorkFlowRole> hasRoles() {
            return Arrays.asList(Role.COUNTRY_LEADER);
        }
    },
    CATHERINE {
        @Override public List<WorkFlowRole> hasRoles() {
            return Arrays.asList(Role.TREASURER, Role.ADMINISTRATOR);
        }
    };

}
