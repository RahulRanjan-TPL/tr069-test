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

public class ReflectedFrom extends MappedResponseChangedSourceAddressReflectedFrom {

    public ReflectedFrom() {
        super(MessageAttribute.MessageAttributeType.ReflectedFrom);
    }

    public static ReflectedFrom parse(byte[] data) throws MessageAttributeParsingException {
        ReflectedFrom result = new ReflectedFrom();
        MappedResponseChangedSourceAddressReflectedFrom.parse(result, data);
        return result;
    }

}
