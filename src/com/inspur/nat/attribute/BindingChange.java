
package com.inspur.nat.attribute;

import com.inspur.nat.util.Utility;
import com.inspur.nat.util.UtilityException;

public class BindingChange extends MessageAttribute {

    public BindingChange() {
        super(MessageAttribute.MessageAttributeType.BindingChange);
    }

    @Override
    public byte[] getBytes() throws UtilityException {
        byte[] result = new byte[4];
        // type
        System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
        // // length
        System.arraycopy(Utility.integerToTwoBytes(0), 0, result, 2, 2);
        return result;
    }

}
