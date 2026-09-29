package com.github.jinahya.object.randomizer.spec.validation;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

/**
 * The randomizer located, by the naming convention, for {@link Address}.
 * <p>
 * The PODAM flavor is chosen because it is the one which honors {@code jakarta.validation.constraints} with no
 * configuration at all, which is the whole point of randomizing a class the Jakarta Validation specification publishes.
 * The exclusions, which are none, live in {@link Address_Randomizer_Constants}, so that this randomizer and the three
 * beside it are configured from one place.
 * <p>
 * This class belongs to the Jakarta Validation 3.1 specification, and is declared under that generation's own
 * {@code src/test/java-jakarta-ee-11} source root, beside the {@link Address} it randomizes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Address_Randomizer
        extends PodamObjectRandomizer<Address> {

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what {@code ObjectRandomizerUtils} instantiates a located
     *         randomizer by.
     */
    public Address_Randomizer() {
        super(Address.class, Address_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}
