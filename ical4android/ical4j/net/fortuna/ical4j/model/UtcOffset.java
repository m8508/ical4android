/*
 * $Id: UtcOffset.java,v 1.8 2007/01/04 04:47:56 fortuna Exp $
 *
 * Created on 26/06/2005
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
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.fortuna.ical4j.util.Dates;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a timezone offset from UTC time.
 *
 * @author Ben Fortuna
 */
public class UtcOffset implements Serializable {

    private static final long serialVersionUID = 5883111996721531728L;
    
    private static final int HOUR_START_INDEX = 1;

    private static final int HOUR_END_INDEX = 3;

    private static final int MINUTE_START_INDEX = 3;

    private static final int MINUTE_END_INDEX = 5;

    private static final int SECOND_START_INDEX = 5;

    private static final int SECOND_END_INDEX = 7;

    private static final NumberFormat HOUR_FORMAT = new DecimalFormat("00");

    private static final NumberFormat MINUTE_FORMAT = new DecimalFormat("00");

    private static final NumberFormat SECOND_FORMAT = new DecimalFormat("00");

    private transient Log log = LogFactory.getLog(UtcOffset.class);

    private long offset;

    /**
     * @param value
     */
    public UtcOffset(final String value) {
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Parsing string [" + value + "]");
        }

        boolean negative = value.startsWith("-");

        offset = 0;
        offset += Integer.parseInt(value.substring(HOUR_START_INDEX,
                HOUR_END_INDEX))
                * Dates.MILLIS_PER_HOUR;
        offset += Integer.parseInt(value.substring(MINUTE_START_INDEX,
                MINUTE_END_INDEX))
                * Dates.MILLIS_PER_MINUTE;
        try {
            offset += Integer.parseInt(value.substring(SECOND_START_INDEX,
                    SECOND_END_INDEX))
                    * Dates.MILLIS_PER_SECOND;
        }
        catch (Exception e) {
            // seconds not supplied..
            log.debug("Seconds not specified", e);
        }
        if (negative) {
            offset = -offset;
        }
    }

    /**
     * @param offset
     */
    public UtcOffset(final long offset) {
        this.offset = (long) Math.floor(offset / (double) Dates.MILLIS_PER_SECOND) * Dates.MILLIS_PER_SECOND;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        long remainder = Math.abs(offset);

        if (offset < 0) {
            b.append('-');
        }
        else {
            b.append('+');
        }
        b.append(HOUR_FORMAT.format(remainder / Dates.MILLIS_PER_HOUR));

        remainder = remainder % Dates.MILLIS_PER_HOUR;
        b.append(MINUTE_FORMAT.format(remainder / Dates.MILLIS_PER_MINUTE));

        remainder = remainder % Dates.MILLIS_PER_MINUTE;
        if (remainder > 0) {
            b.append(SECOND_FORMAT.format(remainder / Dates.MILLIS_PER_SECOND));
        }
        return b.toString();
    }

    /**
     * @return Returns the offset.
     */
    public final long getOffset() {
        return offset;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof UtcOffset) {
            return getOffset() == ((UtcOffset) arg0).getOffset();
        }
        return super.equals(arg0);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(getOffset()).toHashCode();
    }
    
    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        
        stream.defaultReadObject();
        log = LogFactory.getLog(UtcOffset.class);
    }
}
