/*
 * This file is part of JSTUN. 
 * 
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 * 
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package com.inspur.nat.attribute;

import com.inspur.nat.util.Utility;
import com.inspur.nat.util.UtilityException;

public class ConnectionRequestBinding extends MessageAttribute {
    public ConnectionRequestBinding() {
        super(MessageAttribute.MessageAttributeType.ConnectionReuqestBinding);
    }

    public byte[] getBytes() throws UtilityException {
        byte[] result = new byte[24];
        // type
        System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
        // // length
        System.arraycopy(Utility.integerToTwoBytes(20), 0, result, 2, 2);
        // dslforum.org/TR-111
        result[4] = 0x64;
        result[5] = 0x73;
        result[6] = 0x6c;
        result[7] = 0x66;
        result[8] = 0x6f;
        result[9] = 0x72;
        result[10] = 0x75;
        result[11] = 0x6d;
        result[12] = 0x2e;
        result[13] = 0x6f;
        result[14] = 0x72;
        result[15] = 0x67;
        result[16] = 0x2f;
        result[17] = 0x54;
        result[18] = 0x52;
        result[19] = 0x2d;
        result[20] = 0x31;
        result[21] = 0x31;
        result[22] = 0x31;
        result[23] = 0x20;

        return result;
    }
}
