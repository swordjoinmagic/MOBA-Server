package Protocol;

import Util.BitConverter;
import com.sun.beans.editors.ByteEditor;

/**
 * 字符串协议
 */
public class ProtocolStr implements IProtocolBase{

    // 协议内容
    public String str;


    @Override
    public String GetDesc() {
        return null;
    }

    @Override
    public String GetName() {
        return null;
    }

    @Override
    public byte[] Encde() {
        // 将字符串转换为字节数组
        byte[] bytes = str.getBytes();
        return bytes;
    }

    @Override
    public IProtocolBase Decode(byte[] readbuff, int start, int length) {
        // 将字节流协议转换为字符串
        ProtocolStr protocol = new ProtocolStr();
        protocol.str = new String(readbuff,start,length);
        return protocol;
    }


}
