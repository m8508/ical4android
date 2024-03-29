/*
 * Copyright 2005 Open Source Applications Foundation
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
package org.osaf.caldav4j;

/**
 * Thrown when trying to update a resource and the etags do not match
 * 
 * @author bobbyrullo
 *
 */
public class ResourceOutOfDateException extends CalDAV4JException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7814395605221751094L;

	public ResourceOutOfDateException(String message) {
        super(message);
    }
    
    public ResourceOutOfDateException(String message, Throwable cause){
        super(message, cause);
    }
}
