/*
 * $Id: PartStat.java,v 1.9 2006/11/05 08:08:45 fortuna Exp $ [18-Apr-2004]
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.util.Strings;

/**
 * Defines a Participation Status parameter.
 * @author benfortuna
 */
public class PartStat extends Parameter {

    private static final long serialVersionUID = -7856347127343842441L;

    private static final String VALUE_NEEDS_ACTION = "NEEDS-ACTION";

    private static final String VALUE_ACCEPTED = "ACCEPTED";

    private static final String VALUE_DECLINED = "DECLINED";

    private static final String VALUE_TENTATIVE = "TENTATIVE";

    private static final String VALUE_DELEGATED = "DELEGATED";

    private static final String VALUE_COMPLETED = "COMPLETED";

    private static final String VALUE_IN_PROCESS = "IN-PROCESS";

    public static final PartStat NEEDS_ACTION = new PartStat(VALUE_NEEDS_ACTION);

    public static final PartStat ACCEPTED = new PartStat(VALUE_ACCEPTED);

    public static final PartStat DECLINED = new PartStat(VALUE_DECLINED);

    public static final PartStat TENTATIVE = new PartStat(VALUE_TENTATIVE);

    public static final PartStat DELEGATED = new PartStat(VALUE_DELEGATED);

    public static final PartStat COMPLETED = new PartStat(VALUE_COMPLETED);

    public static final PartStat IN_PROCESS = new PartStat(VALUE_IN_PROCESS);

    private String value;

    /**
     * @param aValue a string representation of a participation status
     */
    public PartStat(final String aValue) {
        super(PARTSTAT);
        this.value = Strings.unquote(aValue);
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Parameter#getValue()
     */
    public final String getValue() {
        return value;
    }
}
