/*
 * $Id: DateListProperty.java,v 1.8 2006/12/17 11:25:56 fortuna Exp $
 *
 * Created on 11/08/2005
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.property;

import java.text.ParseException;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Strings;

/**
 * Base class for properties with a list of dates as a value.
 * @author Ben Fortuna
 */
public abstract class DateListProperty extends Property {

    private DateList dates;

    private TimeZone timezone;

    /**
     * @param name
     */
    public DateListProperty(final String name) {
        super(name);
        dates = new DateList(Value.DATE_TIME);
    }

    /**
     * @param name
     * @param parameters
     */
    public DateListProperty(final String name, final ParameterList parameters) {
        super(name, parameters);
    }

    /**
     * @param name
     * @param dates
     */
    public DateListProperty(final String name, final DateList dates) {
        super(name);
        this.dates = dates;
    }

    /**
     * @param name
     * @param dates
     */
    public DateListProperty(final String name, final ParameterList parameters,
            final DateList dates) {
        super(name, parameters);
        this.dates = dates;
    }

    /**
     * @return Returns the dates.
     */
    public final DateList getDates() {
        return dates;
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public void setValue(final String aValue) throws ParseException {
        dates = new DateList(aValue, (Value) getParameter(Parameter.VALUE),
                timezone);
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public String getValue() {
        return Strings.valueOf(dates);
    }

    /**
     * Sets the timezone associated with this property.
     * @param timezone a timezone to associate with this property
     */
    public void setTimeZone(final TimeZone timezone) {
        if (dates == null) {
            throw new UnsupportedOperationException(
                    "TimeZone is not applicable to current value");
        }
        this.timezone = timezone;
        if (timezone != null) {
            if (!Value.DATE_TIME.equals(getDates().getType())) {
                throw new UnsupportedOperationException(
                        "TimeZone is not applicable to current value");
            }
            dates.setTimeZone(timezone);
            getParameters().remove(getParameter(Parameter.TZID));
            TzId tzId = new TzId(timezone.getID());
            getParameters().replace(tzId);
        }
        else {
            // use setUtc() to reset timezone..
            setUtc(false);
        }
    }

    /**
     * Resets the timezone associated with the property. If utc is true, any TZID parameters are removed and the Java
     * timezone is updated to UTC time. If utc is false, TZID parameters are removed and the Java timezone is set to the
     * default timezone (i.e. represents a "floating" local time)
     * @param utc
     */
    public final void setUtc(final boolean utc) {
        if (dates == null || !Value.DATE_TIME.equals(dates.getType())) {
            throw new UnsupportedOperationException(
                    "TimeZone is not applicable to current value");
        }
        dates.setUtc(utc);
        getParameters().remove(getParameter(Parameter.TZID));
    }
}
