package com.github.jinahya.object.randomizer.example.new_target_instance;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

import java.util.List;

/**
 * A randomizer of {@link Message}, which constructs the instance itself.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see #newTargetInstance()
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class Message_Randomizer
        extends PodamObjectRandomizer<Message> {

    /**
     * The channel every instance this randomizer produces is constructed on; {@value}.
     */
    public static final String CHANNEL = "example";

    /**
     * Creates a new instance.
     */
    public Message_Randomizer() {
        super(Message.class, List.of());
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link Message} on {@link #CHANNEL}.
     * @implSpec The default would look for a no-argument constructor, which {@link Message} does not declare,
     *         so the instance is constructed here and handed to the engine to fill.
     */
    @Override
    protected Message newTargetInstance() {
        return new Message(CHANNEL);
    }
}
