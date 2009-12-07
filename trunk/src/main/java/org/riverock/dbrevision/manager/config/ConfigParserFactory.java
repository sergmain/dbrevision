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

package org.riverock.dbrevision.manager.config;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 16:55:52
 */
public class ConfigParserFactory {
    private static final ConfigParser CONFIG_PARSER = new JaxbConfigParserImpl();

    public static ConfigParser getConfigParser() {
        return CONFIG_PARSER;
    }
}
