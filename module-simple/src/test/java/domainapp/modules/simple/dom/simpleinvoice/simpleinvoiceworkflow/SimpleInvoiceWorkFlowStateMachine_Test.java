package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;

import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleInvoiceWorkFlowStateMachine_Test {

    SimpleInvoice invoice;
    SimpleInvoiceWorkFlowStateMachine sm = new SimpleInvoiceWorkFlowStateMachine();

    @Test
    public void initialize_works() throws Exception {

        // given
        invoice = new SimpleInvoice();
        // when
        sm.initialise(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.INIT);

    }

    @Test
    public void transition_works() throws Exception {

        // Null->NEW, NEW->NEW
        // given
        invoice = new SimpleInvoice();
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);
        assertExpectedRoleForTask(Role.ADMINISTRATOR);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);

        // NEW->DISCARDED, DISCARDED->DISCARDED
        // given
        invoice.setDiscardedOn(LocalDate.now());
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.DISCARDED);
        assertExpectedRoleForTask(null);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.DISCARDED);

        // NEW->COMPLETED, COMPLETED->COMPLETED
        // given
        invoice.setInvoiceState(SimpleInvoiceState.NEW);
        invoice.setDiscardedOn(null);
        invoice.setCompletedOn(LocalDate.now());
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.COMPLETED);
        assertExpectedRoleForTask(Role.MANAGER);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.COMPLETED);

        // COMPLETED->NEW
        // given
        invoice.setRejectedOn(LocalDate.now());
        invoice.setCompletedOn(null);
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);

        // COMPLETED->APPROVED, APPROVED->APPROVED
        // given
        invoice.setInvoiceState(SimpleInvoiceState.COMPLETED);
        invoice.setRejectedOn(null);
        invoice.setAmount(new BigDecimal("10000.00"));
        invoice.setApprovedByManagerOn(LocalDate.now());
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.APPROVED_BY_MANAGER);
        assertExpectedRoleForTask(Role.COUNTRY_LEADER);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.APPROVED_BY_MANAGER);

        // COMPLETED->PAYABLE, PAYABLE->PAYABLE
        // given
        invoice.setInvoiceState(SimpleInvoiceState.COMPLETED);
        invoice.setAmount(new BigDecimal("9999.99"));
        // and when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);
        assertExpectedRoleForTask(Role.TREASURER);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);

        // APPROVED->PAYABLE, PAYABLE->PAYABLE
        // given
        invoice.setInvoiceState(SimpleInvoiceState.APPROVED_BY_MANAGER);
        invoice.setApprovedByCountryLeaderOn(LocalDate.now());
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);
        assertExpectedRoleForTask(Role.TREASURER);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);

        // PAYABLE->PAID, PAID->PAID
        // given
        invoice.setPaidOn(LocalDate.now());
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAID);
        assertExpectedRoleForTask(null);
        // and when again
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAID);

        // TODO: maybe introduce state REJECTED in order for this scenario not to happen
        // NEW->COMPLETED, COMPLETED->NEW when rejected is lingering...
        invoice.setInvoiceState(SimpleInvoiceState.NEW);
        invoice.setRejectedOn(LocalDate.now());
        invoice.setCompletedOn(LocalDate.now());
        invoice.setApprovedByManagerOn(null);
        // when
        sm.transition(invoice);
        // then still
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.NEW);

    }

    private void assertExpectedRoleForTask(Role role) {
        if (invoice.getInvoiceState().hasTaskFor()==null){
            assertThat(role).isNull();
        } else {
            assertThat(invoice.getInvoiceState().hasTaskFor().equals(role));
        }
    }

    @Test
    public void reinitialise_works() throws Exception {

        // Simple example scenario where amount on invoice is adapted and invoice is re-initialised

        // given
        invoice = new SimpleInvoice();
        invoice.setAmount(new BigDecimal("9999.99"));
        invoice.setCompletedOn(LocalDate.now());
        invoice.setApprovedByManagerOn(LocalDate.now());
        // when
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);

        // and when
        invoice.setAmount(new BigDecimal("10000.00"));
        invoice.setInvoiceState(null); // THIS IS : REINITIALISE
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.APPROVED_BY_MANAGER);
        // and when
        invoice.setApprovedByCountryLeaderOn(LocalDate.now());
        sm.transition(invoice);
        // then
        assertThat(invoice.getInvoiceState()).isEqualTo(SimpleInvoiceState.PAYABLE);

    }




}