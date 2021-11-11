package event.logging.base.impl;

import event.logging.EventAction;
import event.logging.base.ComplexLoggedOutcome;
import event.logging.base.ComplexLoggedRunnable;
import event.logging.base.ComplexLoggedSupplier;
import event.logging.base.EventLoggerBuilder;
import event.logging.base.EventLoggingService;

import java.util.function.Supplier;

public class MockEventLoggerBasicBuilder<T_EVENT_ACTION extends EventAction>
        extends EventLoggerBasicBuilderImpl<T_EVENT_ACTION> {

    MockEventLoggerBasicBuilder(final EventLoggingService eventLoggingService) {
        super(eventLoggingService);
    }

    @Override
    public EventLoggerBuilder.ActionSubBuilder<T_EVENT_ACTION> withSimpleLoggedAction(
            final Runnable loggedAction) {

        return new MockActionSubBuilder<>(
                this,
                ComplexLoggedOutcome::success);
    }

    @Override
    public EventLoggerBuilder.ActionSubBuilder<T_EVENT_ACTION> withComplexLoggedAction(
            final ComplexLoggedRunnable<T_EVENT_ACTION> loggedAction) {

        return new MockActionSubBuilder<>(this, loggedAction);
    }

    @Override
    public <T_RESULT> EventLoggerBuilder.ResultSubBuilder<T_EVENT_ACTION, T_RESULT> withSimpleLoggedResult(
            final Supplier<T_RESULT> loggedWork) {

        return new MockResultSubBuilder<>(
                this,
                eventAction ->
                        ComplexLoggedOutcome.success(loggedWork.get(), eventAction));
    }

    @Override
    public <T_RESULT> EventLoggerBuilder.ResultSubBuilder<T_EVENT_ACTION, T_RESULT> withComplexLoggedResult(
            final ComplexLoggedSupplier<T_RESULT, T_EVENT_ACTION> loggedWork) {

        return new MockResultSubBuilder<>(this, loggedWork);
    }

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


public static class MockActionSubBuilder<T_EVENT_ACTION extends EventAction>
        extends ActionSubBuilderImpl<T_EVENT_ACTION> {

    private final MockEventLoggerBasicBuilder<T_EVENT_ACTION> mockEventLoggerBasicBuilder;

    public MockActionSubBuilder(
            final MockEventLoggerBasicBuilder<T_EVENT_ACTION> mockEventLoggerBasicBuilder,
            final ComplexLoggedRunnable<T_EVENT_ACTION> loggedAction) {
        super(mockEventLoggerBasicBuilder, loggedAction);

        this.mockEventLoggerBasicBuilder = mockEventLoggerBasicBuilder;
    }

    @Override
    public void runActionAndLog() {
        getLoggedAction().run(mockEventLoggerBasicBuilder.getEventAction());
    }
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

public static class MockResultSubBuilder<T_EVENT_ACTION extends EventAction, T_RESULT>
        extends ResultSubBuilderImpl<T_EVENT_ACTION, T_RESULT> {

    private final MockEventLoggerBasicBuilder<T_EVENT_ACTION> mockEventLoggerBasicBuilder;

    public MockResultSubBuilder(
            final MockEventLoggerBasicBuilder<T_EVENT_ACTION> mockEventLoggerBasicBuilder,
            final ComplexLoggedSupplier<T_RESULT, T_EVENT_ACTION> loggedWork) {
        super(mockEventLoggerBasicBuilder, loggedWork);

        this.mockEventLoggerBasicBuilder = mockEventLoggerBasicBuilder;
    }

    @Override
    public T_RESULT getResultAndLog() {
        return getLoggedWork()
                .get(mockEventLoggerBasicBuilder.getEventAction())
                .getResult();
    }
}
}
