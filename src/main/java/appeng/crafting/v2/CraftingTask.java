package appeng.crafting.v2;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * A single action that can be performed to solve a {@link CraftingRequest}.
 * Can have multiple inputs and outputs, resolved at runtime during crafting resolution (e.g. for handling substitutions).
 */
public abstract class CraftingTask {
    public enum State {
        NEEDS_MORE_WORK(true),
        SUCCESS(false),
        /**
         * This aborts the entire crafting operation, use only if absolutely necessary
         */
        FAILURE(false);
        public final boolean needsMoreWork;

        State(boolean needsMoreWork) {
            this.needsMoreWork = needsMoreWork;
        }
    }

    public static final class StepOutput {
        @Nonnull
        public final List<CraftingRequest<?>> extraInputsRequired;

        public StepOutput() {
            this(Collections.emptyList());
        }

        public StepOutput(@Nonnull List<CraftingRequest<?>> extraInputsRequired) {
            this.extraInputsRequired = extraInputsRequired;
        }
    }

    public static final int PRIORITY_EXTRACT = Integer.MAX_VALUE - 100;
    public static final int PRIORITY_CRAFTING_EMITTER = PRIORITY_EXTRACT - 200;
    /** Gets added to a priority determined by the priority of the crafting pattern */
    public static final int PRIORITY_CRAFT_OFFSET = 0;

    public static final int PRIORITY_SIMULATE_CRAFT = Integer.MIN_VALUE + 200;
    public static final int PRIORITY_SIMULATE = Integer.MIN_VALUE + 100;

    public final int priority;
    protected State state;

    /**
     * Called when it's this task's turn for computation.
     *
     * @return A {@link StepOutput} instance describing progress made by the task in this call.
     * If success or failure, the task should have cleaned up after itself - cancel won't be called.
     */
    public abstract StepOutput calculateOneStep(CraftingContext context);

    protected CraftingTask(int priority) {
        this.priority = priority;
        this.state = State.NEEDS_MORE_WORK;
    }

    public State getState() {
        return state;
    }

    /**
     * Compares priorities - highest priority first
     */
    public static final Comparator<CraftingTask> PRIORITY_COMPARATOR = Comparator.comparing(ct -> -ct.priority);
}
