package io.qameta.allure.assertj;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ObjectUtils;
import io.qameta.allure.util.ResultsUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Vendored copy of {@code io.qameta.allure:allure-assertj:2.33.0} AllureAspectJ.
 *
 * <p>Reason: the stock allure-assertj names the {@code assertThat} Allure step by calling
 * {@code ObjectUtils.toString(actual)} on the value under test, producing unreadable step names
 * like {@code assertThat 'ApiResponse[statusCode=200, headers=..., rawBody=...]'}. By vendoring
 * the aspect here we can apply the single naming fix below without forking the full library.
 *
 * <p>Only change vs. the original: {@link #logAssertCreation} uses
 * {@code actual.getClass().getSimpleName()} instead of {@code ObjectUtils.toString(actual)},
 * producing {@code assertThat [ApiResponse]} instead of the full {@code toString()} dump.
 *
 * <p>All pointcuts and advice are identical to the original 2.33.0 source.
 */
@Aspect
public class AllureAspectJ {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureAspectJ.class);

    /*
     * InheritableThreadLocal so that child threads spawned inside a test inherit the same
     * lifecycle instance — matching the original allure-assertj behaviour.
     */
    private static final InheritableThreadLocal<AllureLifecycle> lifecycle =
            new InheritableThreadLocal<AllureLifecycle>() {
                @Override
                protected AllureLifecycle initialValue() {
                    return Allure.getLifecycle();
                }
            };

    public static AllureLifecycle getLifecycle() {
        return lifecycle.get();
    }

    // -------------------------------------------------------------------------
    // Pointcuts (unchanged from original)
    // -------------------------------------------------------------------------

    /** Matches non-private AbstractAssert constructor executions (i.e. every assertThat() call). */
    @Pointcut("execution(!private org.assertj.core.api.AbstractAssert.new(..))")
    public void anyAssertCreation() {
    }

    /** Matches AssertJ proxy-setup methods — excluded from step tracking to reduce noise. */
    @Pointcut("execution(* org.assertj.core.api.AssertJProxySetup.*(..))")
    public void proxyMethod() {
    }

    /** Matches all public assertion methods on AbstractAssert subclasses, excluding proxy setup. */
    @Pointcut("execution(public * org.assertj.core.api.AbstractAssert+.*(..)) && !proxyMethod()")
    public void anyAssert() {
    }

    // -------------------------------------------------------------------------
    // Advice
    // -------------------------------------------------------------------------

    /**
     * Creates and immediately closes a step named {@code assertThat [ClassName]} each time an
     * AssertJ assertion object is constructed.
     *
     * <p><b>Changed vs. original:</b> The original used {@code ObjectUtils.toString(actual)} which
     * produces the full {@code toString()} of the object under test — noisy for domain models.
     * This version uses {@code actual.getClass().getSimpleName()} so the step reads
     * {@code assertThat [ApiResponse]} regardless of how verbose the model's toString() is.
     */
    @After("anyAssertCreation()")
    public void logAssertCreation(final JoinPoint joinPoint) {
        // Changed: class simple name instead of ObjectUtils.toString() to avoid noisy step names
        final String typeName = (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] != null)
                ? joinPoint.getArgs()[0].getClass().getSimpleName()
                : "value";
        final String name = String.format("assertThat [%s]", typeName);

        final String uuid = UUID.randomUUID().toString();
        final StepResult step = new StepResult()
                .setName(name)
                .setStatus(Status.PASSED);
        getLifecycle().startStep(uuid, step);
        getLifecycle().stopStep(uuid);
    }

    /** Opens an Allure step for each assertion method call. */
    @Before("anyAssert()")
    public void stepStart(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String uuid = UUID.randomUUID().toString();
        final String name = joinPoint.getArgs().length > 0
                ? String.format("%s '%s'", methodSignature.getName(), arrayToString(joinPoint.getArgs()))
                : methodSignature.getName();
        final StepResult step = new StepResult()
                .setName(name)
                .setStatus(Status.PASSED);
        getLifecycle().startStep(uuid, step);
    }

    /** Marks the current step as failed/broken when an assertion throws. */
    @AfterThrowing(pointcut = "anyAssert()", throwing = "e")
    public void stepFailed(final Throwable e) {
        getLifecycle().updateStep(s -> {
            s.setStatus(ResultsUtils.getStatus(e).orElse(Status.BROKEN));
            s.setStatusDetails(ResultsUtils.getStatusDetails(e).orElse(new StatusDetails()));
        });
        getLifecycle().stopStep();
    }

    /** Marks the current step as passed and closes it after a successful assertion. */
    @AfterReturning("anyAssert()")
    public void stepStop() {
        getLifecycle().updateStep(s -> s.setStatus(Status.PASSED));
        getLifecycle().stopStep();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String arrayToString(final Object[] args) {
        return Stream.of(args)
                .map(ObjectUtils::toString)
                .collect(Collectors.joining(", "));
    }
}
