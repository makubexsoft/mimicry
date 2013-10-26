/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.mimicry.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>
 * Some methods in this class were copied from the Spring Framework. In these cases, we have retained all license,
 * copyright and author information.
 * 
 */
public class StringUtils
{
    public static String toFirstLower(String str)
    {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer. Trims tokens and omits empty tokens.
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those
     * characters can be used to separate tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     * <p/>
     * <p>
     * Copied from the Spring Framework while retaining all license, copyright and author information.
     * 
     * @param str
     *            the String to tokenize
     * @param delimiters
     *            the delimiter characters, assembled as String (each of those characters is individually considered as
     *            delimiter).
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see java.lang.String#trim()
     */
    public static String[] tokenizeToStringArray(String str, String delimiters)
    {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those
     * characters can be used to separate tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     * <p/>
     * <p>
     * Copied from the Spring Framework while retaining all license, copyright and author information.
     * 
     * @param str
     *            the String to tokenize
     * @param delimiters
     *            the delimiter characters, assembled as String (each of those characters is individually considered as
     *            delimiter)
     * @param trimTokens
     *            trim the tokens via String's <code>trim</code>
     * @param ignoreEmptyTokens
     *            omit empty tokens from the result array (only applies to tokens that are empty after trimming;
     *            StringTokenizer will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens (<code>null</code> if the input String was <code>null</code>)
     * @see java.util.StringTokenizer
     * @see java.lang.String#trim()
     */
    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
            boolean ignoreEmptyTokens)
    {

        if (str == null)
        {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (trimTokens)
            {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0)
            {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    /**
     * Copy the given Collection into a String array. The Collection must contain String elements only.
     * <p/>
     * <p>
     * Copied from the Spring Framework while retaining all license, copyright and author information.
     * 
     * @param collection
     *            the Collection to copy
     * @return the String array (<code>null</code> if the passed-in Collection was <code>null</code>)
     */
    public static String[] toStringArray(Collection<String> collection)
    {
        if (collection == null)
        {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }
}
