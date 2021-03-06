/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Base class for all types of events. Contains the event identifier and timestamp.
 *
 * @author Allard Buijze
 * @since 0.4
 */
public abstract class EventBase implements Event {

    private static final long serialVersionUID = 8354215007776930168L;
    private static final IdentifierFactory IDENTIFIER_FACTORY = IdentifierFactory.getInstance();

    private final MutableEventMetaData metaData;
    private long eventRevision;

    /**
     * Initialize a new event. This constructor will set the event identifier to a random UUID and the timestamp to the
     * current date and time. The revision of this event defaults to 0.
     */
    protected EventBase() {
        this(0);
    }

    /**
     * Constructs a newly created event with the given <code>eventRevision</code> number. The timestamp is set to the
     * current date and time (including time zone) and a unique identifier is generated.
     * <p/>
     * This constructor is <em>not</em> intended for the reconstruction of existing events (e.g. during
     * deserialization). In that case, consider using {@link #EventBase(String, org.joda.time.DateTime, long)} instead.
     *
     * @param eventRevision The revision of the event type
     */
    protected EventBase(long eventRevision) {
        this(IDENTIFIER_FACTORY.generateIdentifier(), new DateTime(), eventRevision);
    }

    /**
     * Initializes the event with given parameters. This constructor is intended to reconstruct an existing event (e.g.
     * during deserialization).
     *
     * @param identifier    The event identifier
     * @param timestamp     The original creation timestamp
     * @param eventRevision The revision of the event type
     */
    protected EventBase(String identifier, DateTime timestamp, long eventRevision) {
        metaData = new MutableEventMetaData(timestamp, identifier);
        this.eventRevision = eventRevision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventIdentifier() {
        return metaData.getEventIdentifier();
    }

    /**
     * Insert a key-value pair into the meta data of this event. If a value already exists for the given key, it is
     * overwritten with the <code>value</code> provided.
     * <p/>
     * Note: this method should <em>*never*</em> be called after an event has been dispatched or stored.
     * <p/>
     * Be careful when using key values with the underscore ( _ ) prefix. They might collide with internal Axon meta
     * data.
     *
     * @param key   The key of the key-value pair
     * @param value The value to store in the meta data
     */
    protected final void addMetaData(String key, Serializable value) {
        metaData.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getTimestamp() {
        return metaData.getTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventMetaData getMetaData() {
        return metaData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable getMetaDataValue(String key) {
        return metaData.get(key);
    }

    /**
     * Checks the equality of two events. Events are equal if they have the same type and identifier.
     *
     * @param o the object to compare this event to
     * @return <code>true</code> if <code>o</code> is equal to this event instance
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventBase that = (EventBase) o;

        return metaData.getEventIdentifier().equals(that.metaData.getEventIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return metaData.getEventIdentifier().hashCode();
    }

    /**
     * Sets the revision of the implementing event definition. Revision numbers are use by {@link
     * org.axonframework.eventstore.EventUpcaster UpCasters} to decide which transformations to apply when
     * deserializing an event. Revision numbers only need to be supplied if the structure has been changed in such a
     * way that the event serializer cannot deserialize it without help from an UpCaster.
     *
     * @param eventRevision The revision of the event definition
     * @deprecated Set event revision in the constructor instead
     */
    @Deprecated
    protected void setEventRevision(long eventRevision) {
        this.eventRevision = eventRevision;
    }

    /**
     * Returns the revision number of this event definition.
     *
     * @return the revision number of this event definition
     */
    public long getEventRevision() {
        return eventRevision;
    }
}
