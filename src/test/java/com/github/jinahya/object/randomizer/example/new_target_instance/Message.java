package com.github.jinahya.object.randomizer.example.new_target_instance;

/*-
 * #%L
 * jinahya-object-randomizer
 * %%
 * Copyright (C) 2026 Jin Kwon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Objects;

/**
 * A target which declares no no-argument constructor, and so cannot be instantiated the default way.
 * <p>
 * Its {@code channel} is settled at construction and has no setter, which is what makes the randomizer's own instance
 * observable afterwards: whatever the engine writes, it cannot have written that.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class Message {

    /**
     * Creates a new instance on the specified channel.
     *
     * @param channel the channel; never {@code null}.
     */
    public Message(final String channel) {
        super();
        this.channel = Objects.requireNonNull(channel, "channel is null");
    }

    @Override
    public String toString() {
        return super.toString() + '{'
               + "channel=" + channel
               + ",body=" + body
               + '}';
    }

    public String getChannel() {
        return channel;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * Settled at construction, and never written afterwards.
     */
    private final String channel;

    /**
     * What the engine fills.
     */
    private String body;
}
