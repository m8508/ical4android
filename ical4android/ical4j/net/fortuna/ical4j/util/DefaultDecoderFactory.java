/*
 * $Id: DefaultDecoderFactory.java,v 1.1 2006/05/13 09:18:31 fortuna Exp $
 * 
 * Created on 13/05/2006
 * 
 * Copyright (c) 2006, Ben Fortuna All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * o Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * o Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * o Neither the name of Ben Fortuna nor the names of any other contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.util;

import java.io.UnsupportedEncodingException;

import net.fortuna.ical4j.model.parameter.Encoding;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.StringDecoder;

/**
 * @author Ben Fortuna
 */
public class DefaultDecoderFactory extends DecoderFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fortuna.ical4j.util.DecoderFactory#createBinaryDecoder(net.fortuna
	 * .ical4j.model.parameter.Encoding)
	 */
	@Override
	public BinaryDecoder createBinaryDecoder(final Encoding encoding)
			throws UnsupportedEncodingException {

		if (Encoding.QUOTED_PRINTABLE.equals(encoding)) {
			// return new QuotedPrintableCodec();
			return null;// TODO
		} else if (Encoding.BASE64.equals(encoding)) {
			// return new Base64();
			return null;
		}
		throw new UnsupportedEncodingException(
				"Decoder not available for decoding [" + encoding + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fortuna.ical4j.util.DecoderFactory#createStringDecoder(net.fortuna
	 * .ical4j.model.parameter.Encoding)
	 */
	@Override
	public StringDecoder createStringDecoder(final Encoding encoding)
			throws UnsupportedEncodingException {

		if (Encoding.QUOTED_PRINTABLE.equals(encoding)) {
			// return new QuotedPrintableCodec();
			return null;// TODO null
		}
		throw new UnsupportedEncodingException(
				"Decoder not available for decoding [" + encoding + "]");
	}
}
