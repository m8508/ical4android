/*
 * $Id: VFreeBusy.java,v 1.26 2007/01/04 04:48:39 fortuna Exp $ [Apr 5, 2004]
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.component;

import java.io.IOException;
import java.util.Iterator;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.PropertyValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines an iCalendar VFREEBUSY component.
 *
 * <pre>
 *  4.6.4 Free/Busy Component
 *
 *     Component Name: VFREEBUSY
 *
 *     Purpose: Provide a grouping of component properties that describe
 *     either a request for free/busy time, describe a response to a request
 *     for free/busy time or describe a published set of busy time.
 *
 *     Formal Definition: A &quot;VFREEBUSY&quot; calendar component is defined by the
 *     following notation:
 *
 *       freebusyc  = &quot;BEGIN&quot; &quot;:&quot; &quot;VFREEBUSY&quot; CRLF
 *                    fbprop
 *                    &quot;END&quot; &quot;:&quot; &quot;VFREEBUSY&quot; CRLF
 *
 *       fbprop     = *(
 *
 *                  ; the following are optional,
 *                  ; but MUST NOT occur more than once
 *
 *                  contact / dtstart / dtend / duration / dtstamp /
 *                  organizer / uid / url /
 *
 *                  ; the following are optional,
 *                  ; and MAY occur more than once
 *
 *                  attendee / comment / freebusy / rstatus / x-prop
 *
 *                  )
 *
 *     Description: A &quot;VFREEBUSY&quot; calendar component is a grouping of
 *     component properties that represents either a request for, a reply to
 *     a request for free or busy time information or a published set of
 *     busy time information.
 *
 *     When used to request free/busy time information, the &quot;ATTENDEE&quot;
 *     property specifies the calendar users whose free/busy time is being
 *     requested; the &quot;ORGANIZER&quot; property specifies the calendar user who
 *     is requesting the free/busy time; the &quot;DTSTART&quot; and &quot;DTEND&quot;
 *     properties specify the window of time for which the free/busy time is
 *     being requested; the &quot;UID&quot; and &quot;DTSTAMP&quot; properties are specified to
 *     assist in proper sequencing of multiple free/busy time requests.
 *
 *     When used to reply to a request for free/busy time, the &quot;ATTENDEE&quot;
 *     property specifies the calendar user responding to the free/busy time
 *     request; the &quot;ORGANIZER&quot; property specifies the calendar user that
 *     originally requested the free/busy time; the &quot;FREEBUSY&quot; property
 *     specifies the free/busy time information (if it exists); and the
 *     &quot;UID&quot; and &quot;DTSTAMP&quot; properties are specified to assist in proper
 *     sequencing of multiple free/busy time replies.
 *
 *     When used to publish busy time, the &quot;ORGANIZER&quot; property specifies
 *     the calendar user associated with the published busy time; the
 *     &quot;DTSTART&quot; and &quot;DTEND&quot; properties specify an inclusive time window
 *     that surrounds the busy time information; the &quot;FREEBUSY&quot; property
 *     specifies the published busy time information; and the &quot;DTSTAMP&quot;
 *     property specifies the date/time that iCalendar object was created.
 *
 *     The &quot;VFREEBUSY&quot; calendar component cannot be nested within another
 *     calendar component. Multiple &quot;VFREEBUSY&quot; calendar components can be
 *     specified within an iCalendar object. This permits the grouping of
 *     Free/Busy information into logical collections, such as monthly
 *     groups of busy time information.
 *
 *     The &quot;VFREEBUSY&quot; calendar component is intended for use in iCalendar
 *     object methods involving requests for free time, requests for busy
 *     time, requests for both free and busy, and the associated replies.
 *
 *     Free/Busy information is represented with the &quot;FREEBUSY&quot; property.
 *     This property provides a terse representation of time periods. One or
 *     more &quot;FREEBUSY&quot; properties can be specified in the &quot;VFREEBUSY&quot;
 *     calendar component.
 *
 *     When present in a &quot;VFREEBUSY&quot; calendar component, the &quot;DTSTART&quot; and
 *     &quot;DTEND&quot; properties SHOULD be specified prior to any &quot;FREEBUSY&quot;
 *     properties. In a free time request, these properties can be used in
 *     combination with the &quot;DURATION&quot; property to represent a request for a
 *     duration of free time within a specified window of time.
 *
 *     The recurrence properties (&quot;RRULE&quot;, &quot;EXRULE&quot;, &quot;RDATE&quot;, &quot;EXDATE&quot;) are
 *     not permitted within a &quot;VFREEBUSY&quot; calendar component. Any recurring
 *     events are resolved into their individual busy time periods using the
 *     &quot;FREEBUSY&quot; property.
 *
 *     Example: The following is an example of a &quot;VFREEBUSY&quot; calendar
 *     component used to request free or busy time information:
 *
 *       BEGIN:VFREEBUSY
 *       ORGANIZER:MAILTO:jane_doe@host1.com
 *       ATTENDEE:MAILTO:john_public@host2.com
 *       DTSTART:19971015T050000Z
 *       DTEND:19971016T050000Z
 *       DTSTAMP:19970901T083000Z
 *       END:VFREEBUSY
 *
 *     The following is an example of a &quot;VFREEBUSY&quot; calendar component used
 *     to reply to the request with busy time information:
 *
 *       BEGIN:VFREEBUSY
 *       ORGANIZER:MAILTO:jane_doe@host1.com
 *       ATTENDEE:MAILTO:john_public@host2.com
 *       DTSTAMP:19970901T100000Z
 *       FREEBUSY;VALUE=PERIOD:19971015T050000Z/PT8H30M,
 *        19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M
 *       URL:http://host2.com/pub/busy/jpublic-01.ifb
 *       COMMENT:This iCalendar file contains busy time information for
 *         the next three months.
 *       END:VFREEBUSY
 *
 *     The following is an example of a &quot;VFREEBUSY&quot; calendar component used
 *     to publish busy time information.
 *
 *       BEGIN:VFREEBUSY
 *       ORGANIZER:jsmith@host.com
 *       DTSTART:19980313T141711Z
 *       DTEND:19980410T141711Z
 *       FREEBUSY:19980314T233000Z/19980315T003000Z
 *       FREEBUSY:19980316T153000Z/19980316T163000Z
 *       FREEBUSY:19980318T030000Z/19980318T040000Z
 *       URL:http://www.host.com/calendar/busytime/jsmith.ifb
 *       END:VFREEBUSY
 * </pre>
 *
 * Example 1 - Requesting all busy time slots for a given period:
 *
 * <pre><code>
 * // request all busy time between today and 1 week from now..
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * Date start = cal.getTime();
 * cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
 * Date end = cal.getTime();
 *
 * VFreeBusy request = new VFreeBusy(start, end);
 * </code></pre>
 *
 * Example 2 - Publishing all busy time slots for the period requested:
 *
 * <pre><code>
 * VFreeBusy reply = new VFreeBusy(request, calendar.getComponents());
 * </code></pre>
 *
 * Example 3 - Requesting all free time slots for a given period of at least the specified duration:
 *
 * <pre><code>
 * // request all free time between today and 1 week from now of
 * // duration 2 hours or more..
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * Date start = cal.getTime();
 * cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
 * Date end = cal.getTime();
 *
 * VFreeBusy request = new VFreeBusy(start, end, 2 * 60 * 60 * 1000);
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VFreeBusy extends CalendarComponent {

    private static final long serialVersionUID = 1046534053331139832L;

    private transient Log log = LogFactory.getLog(VFreeBusy.class);

    /**
     * Default constructor.
     */
    public VFreeBusy() {
        super(VFREEBUSY);
        getProperties().add(new DtStamp());
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VFreeBusy(final PropertyList properties) {
        super(VFREEBUSY, properties);
    }

    /**
     * Constructs a new VFreeBusy instance with the specified start and end boundaries. This constructor should be used
     * for requesting Free/Busy time for a specified period.
     * @param startDate the starting boundary for the VFreeBusy
     * @param endDate the ending boundary for the VFreeBusy
     */
    public VFreeBusy(final DateTime start, final DateTime end) {
        this();
        // dtstart MUST be specified in UTC..
        getProperties().add(new DtStart(start, true));
        // dtend MUST be specified in UTC..
        getProperties().add(new DtEnd(end, true));
    }

    /**
     * Constructs a new VFreeBusy instance with the specified start and end boundaries. This constructor should be used
     * for requesting Free/Busy time for a specified duration in given period defined by the start date and end date.
     * @param startDate the starting boundary for the VFreeBusy
     * @param endDate the ending boundary for the VFreeBusy
     * @param duration the length of the period being requested
     */
    public VFreeBusy(final DateTime start, final DateTime end,
            final Dur duration) {
        this();
        // dtstart MUST be specified in UTC..
        getProperties().add(new DtStart(start, true));
        // dtend MUST be specified in UTC..
        getProperties().add(new DtEnd(end, true));
        getProperties().add(new Duration(duration));
    }

    /**
     * Constructs a new VFreeBusy instance represeting a reply to the specified VFREEBUSY request according to the
     * specified list of components.
     * @param request a VFREEBUSY request
     * @param components a component list used to initialise busy time
     */
    public VFreeBusy(final VFreeBusy request, final ComponentList components) {
        this();
        DtStart start = (DtStart) request.getProperty(Property.DTSTART);
        DtEnd end = (DtEnd) request.getProperty(Property.DTEND);
        Duration duration = (Duration) request.getProperty(Property.DURATION);
        // dtstart MUST be specified in UTC..
        getProperties().add(new DtStart(start.getDate(), true));
        // dtend MUST be specified in UTC..
        getProperties().add(new DtEnd(end.getDate(), true));
        if (duration != null) {
            getProperties().add(new Duration(duration.getDuration()));
            // Initialise with all free time of at least the specified
            // duration..
            DateTime freeStart = new DateTime(start.getDate());
            DateTime freeEnd = new DateTime(end.getDate());
            FreeBusy fb = createFreeTime(freeStart, freeEnd, duration
                    .getDuration(), components);
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        }
        else {
            // initialise with all busy time for the specified period..
            DateTime busyStart = new DateTime(start.getDate());
            DateTime busyEnd = new DateTime(end.getDate());
            FreeBusy fb = createBusyTime(busyStart, busyEnd, components);
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        }
    }

    /**
     * Create a FREEBUSY property representing the busy time for the specified component list. If the component is not
     * applicable to FREEBUSY time, or if the component is outside the bounds of the start and end dates, null is
     * returned. If no valid busy periods are identified in the component an empty FREEBUSY property is returned (i.e.
     * empty period list).
     * @param component a component to base the FREEBUSY property on
     * @return a FreeBusy instance or null if the component is not applicable
     */
    private FreeBusy createBusyTime(final DateTime start, final DateTime end,
            final ComponentList components) {
        PeriodList periods = getConsumedTime(components, start, end);
        for (Iterator i = periods.iterator(); i.hasNext();) {
            Period period = (Period) i.next();
            // check if period outside bounds..
            if (period.getStart().after(end) || period.getEnd().before(start)) {
                periods.remove(period);
            }
        }
        return new FreeBusy(periods);
    }

    /**
     * Create a FREEBUSY property representing the free time available of the specified duration for the given list of
     * components. component. If the component is not applicable to FREEBUSY time, or if the component is outside the
     * bounds of the start and end dates, null is returned. If no valid busy periods are identified in the component an
     * empty FREEBUSY property is returned (i.e. empty period list).
     * @param start
     * @param end
     * @param duration
     * @param components
     * @return
     */
    private FreeBusy createFreeTime(final DateTime start, final DateTime end,
            final Dur duration, final ComponentList components) {
        FreeBusy fb = new FreeBusy();
        fb.getParameters().add(FbType.FREE);
        PeriodList periods = getConsumedTime(components, start, end);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Busy periods: " + periods);
        }
        DateTime lastPeriodEnd = null;
        // where no time is consumed set the last period end as the range start..
        if (periods.isEmpty()) {
            lastPeriodEnd = new DateTime(start);
        }
        for (Iterator i = periods.iterator(); i.hasNext();) {
            Period period = (Period) i.next();
            // check if period outside bounds..
            if (period.getStart().after(end) || period.getEnd().before(start)) {
                continue;
            }
            // create a dummy last period end if first period starts after the start date
            // (i.e. there is a free time gap between the start and the first period).
            if (lastPeriodEnd == null && period.getStart().after(start)) {
                lastPeriodEnd = new DateTime(start);
            }
            // calculate duration between this period start and last period end..
            if (lastPeriodEnd != null) {
                Duration freeDuration = new Duration(lastPeriodEnd, period
                        .getStart());
                if (freeDuration.getDuration().compareTo(duration) >= 0) {
                    fb.getPeriods().add(
                            new Period(lastPeriodEnd, freeDuration
                                    .getDuration()));
                }
            }
            lastPeriodEnd = period.getEnd();
        }
        // calculate duration between last period end and end ..
        if (lastPeriodEnd != null) {
            Duration freeDuration = new Duration(lastPeriodEnd, end);
            if (freeDuration.getDuration().compareTo(duration) >= 0) {
                fb.getPeriods().add(
                        new Period(lastPeriodEnd, freeDuration.getDuration()));
            }
        }
        return fb;
    }

    /**
     * Creates a list of periods representing the time consumed by the specified list of components.
     * @param components
     * @return
     */
    private PeriodList getConsumedTime(final ComponentList components,
            final DateTime rangeStart, final DateTime rangeEnd) {
        PeriodList periods = new PeriodList();
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            // only events consume time..
            if (component instanceof VEvent) {
                periods.addAll(((VEvent) component).getConsumedTime(rangeStart,
                        rangeEnd));
            }
        }
        return periods.normalise();
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        if (!CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {

            // From "4.8.4.7 Unique Identifier":
            // Conformance: The property MUST be specified in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.getInstance().assertOne(Property.UID,
                    getProperties());

            // From "4.8.7.2 Date/Time Stamp":
            // Conformance: This property MUST be included in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP,
                    getProperties());
        }

        PropertyValidator validator = PropertyValidator.getInstance();

        /*
         * ; the following are optional, ; but MUST NOT occur more than once contact / dtstart / dtend / duration /
         * dtstamp / organizer / uid / url /
         */
        validator.assertOneOrLess(Property.CONTACT, getProperties());
        validator.assertOneOrLess(Property.DTSTART, getProperties());
        validator.assertOneOrLess(Property.DTEND, getProperties());
        validator.assertOneOrLess(Property.DURATION, getProperties());
        validator.assertOneOrLess(Property.DTSTAMP, getProperties());
        validator.assertOneOrLess(Property.ORGANIZER, getProperties());
        validator.assertOneOrLess(Property.UID, getProperties());
        validator.assertOneOrLess(Property.URL, getProperties());

        /*
         * ; the following are optional, ; and MAY occur more than once attendee / comment / freebusy / rstatus / x-prop
         */

        /*
         * The recurrence properties ("RRULE", "EXRULE", "RDATE", "EXDATE") are not permitted within a "VFREEBUSY"
         * calendar component. Any recurring events are resolved into their individual busy time periods using the
         * "FREEBUSY" property.
         */
        validator.assertNone(Property.RRULE, getProperties());
        validator.assertNone(Property.EXRULE, getProperties());
        validator.assertNone(Property.RDATE, getProperties());
        validator.assertNone(Property.EXDATE, getProperties());

        // DtEnd value must be later in time that DtStart..
        DtStart dtStart = (DtStart) getProperty(Property.DTSTART);
        DtEnd dtEnd = (DtEnd) getProperty(Property.DTEND);
        if (dtStart != null && dtEnd != null
                && !dtStart.getDate().before(dtEnd.getDate())) {
            throw new ValidationException("Property [" + Property.DTEND
                    + "] must be later in time than [" + Property.DTSTART + "]");
        }

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * @return
     */
    public final Contact getContact() {
        return (Contact) getProperty(Property.CONTACT);
    }

    /**
     * @return
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }

    /**
     * @return
     */
    public final DtEnd getEndDate() {
        return (DtEnd) getProperty(Property.DTEND);
    }

    /**
     * @return
     */
    public final Duration getDuration() {
        return (Duration) getProperty(Property.DURATION);
    }

    /**
     * @return
     */
    public final DtStamp getDateStamp() {
        return (DtStamp) getProperty(Property.DTSTAMP);
    }

    /**
     * @return
     */
    public final Organizer getOrganizer() {
        return (Organizer) getProperty(Property.ORGANIZER);
    }

    /**
     * @return
     */
    public final Url getUrl() {
        return (Url) getProperty(Property.URL);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
    }
    
    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        
        stream.defaultReadObject();
        log = LogFactory.getLog(VFreeBusy.class);
    }
}
