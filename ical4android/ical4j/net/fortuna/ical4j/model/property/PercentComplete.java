/*
 * $Id: PercentComplete.java,v 1.8 2006/11/05 06:04:42 fortuna Exp $
 * 
 * Created: [Apr 6, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;

/**
 * Defines a PERCENT-COMPLETE iCalendar component property.
 * @author benf
 */
public class PercentComplete extends Property {

    private static final long serialVersionUID = 7788138484983240112L;

    private int percentage;

    /**
     * Default constructor.
     */
    public PercentComplete() {
        super(PERCENT_COMPLETE);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public PercentComplete(final ParameterList aList, final String aValue) {
        super(PERCENT_COMPLETE, aList);
        setValue(aValue);
    }

    /**
     * @param aPercentage an int representation of a percentage
     */
    public PercentComplete(final int aPercentage) {
        super(PERCENT_COMPLETE);
        percentage = aPercentage;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aPercentage an int representation of a percentage
     */
    public PercentComplete(final ParameterList aList, final int aPercentage) {
        super(PERCENT_COMPLETE, aList);
        percentage = aPercentage;
    }

    /**
     * @return Returns the percentage.
     */
    public final int getPercentage() {
        return percentage;
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) {
        percentage = Integer.parseInt(aValue);
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return String.valueOf(getPercentage());
    }

    /**
     * @param percentage The percentage to set.
     */
    public final void setPercentage(final int percentage) {
        this.percentage = percentage;
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
