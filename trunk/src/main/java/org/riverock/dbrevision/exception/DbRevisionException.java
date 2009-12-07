/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.riverock.dbrevision.exception;

/**
 * Generic db revision exception
 *
 * @author Sergei Maslyukov
 *         Date: 21.12.2006
 *         Time: 20:43:06
 *         <p/>
 *         $Id$
 */
public class DbRevisionException extends RuntimeException {

    /**
     * Empty constructor
     */
    public DbRevisionException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public DbRevisionException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public DbRevisionException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public DbRevisionException(String s, Throwable cause){
        super(s, cause);
    }
}


