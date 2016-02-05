/*
 * Copyright (c) 2015 Regents of the University of Minnesota.
 *
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
 */

package edu.umn.biomedicus.rtf.exc;

/**
 *
 */
public class RtfReaderException extends Exception {
    public RtfReaderException() {
        super();
    }

    public RtfReaderException(String message) {
        super(message);
    }

    public RtfReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RtfReaderException(Throwable cause) {
        super(cause);
    }

    protected RtfReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
