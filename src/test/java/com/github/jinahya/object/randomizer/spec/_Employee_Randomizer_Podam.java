package com.github.jinahya.object.randomizer.spec;

import com.github.jinahya.object.randomizer.__Randomizer;

/**
 * A PODAM randomizer of {@link _Employee}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.__RandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code _EmployeeRandomizer} and {@code _Employee_Randomizer} -- so it is never located, and never
 * competes with {@link _Employee_Randomizer}, the one located by the convention. It stands beside the three other
 * flavors so that each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * The instance comes from {@link __Randomizer#newTargetInstance()}, and is populated through its setters; PODAM never
 * assigns a field.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Randomizer_Podam extends __Randomizer.___OfPodam<_Employee> {

    /**
     * Creates a new instance.
     */
    public _Employee_Randomizer_Podam() {
        super(_Employee.class, _Employee_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}
