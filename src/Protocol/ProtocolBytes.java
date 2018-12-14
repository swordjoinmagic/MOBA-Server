package Protocol;

import Util.BitConverter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 字节流协议
 */
public class ProtocolBytes implements IProtocolBase{

    // 传输的字节流
    private byte[] bytes;

    private int start;

    @Override
    public IProtocolBase Decode(byte[] readbuff, int start, int length) {
        ProtocolBytes protocolBytes = new ProtocolBytes();
        protocolBytes.bytes = new byte[length];
        System.arraycopy(readbuff,start, protocolBytes.bytes,0, length);
        return protocolBytes;
    }

    @Override
    public byte[] Encde() {
        return bytes;
    }

    @Override
    public String GetName() {
        return "";
    }

    @Override
    public String GetDesc() {
        return null;
    }

    // 为字节流协议内容添加字符串的方法
    public void AddString(String str){
        int length = str.length();
        byte[] lenBytes = BitConverter.intToBytes(length);
        byte[] strBytes = str.getBytes();
        byte[] combineBytes = Unpooled.copiedBuffer(lenBytes,strBytes).array();
        if(bytes==null){
            bytes = combineBytes;
        }else {
            bytes =  Unpooled.copiedBuffer(bytes,combineBytes).array();
        }
    }

    // 从协议中获取字符串内容
    public String GetString(){
        if(bytes==null)
            return "";
        if(bytes.length < start + 4)
            return "";

        // 获得协议中字符串的长度
        int strLen = BitConverter.BytesToInt(bytes,start);
        if(bytes.length < start + 4 + strLen)
            return "";

        // 获得协议中具体的字符串的内容
        String str = new String(bytes,start+4,strLen);

        // start指针前移
        this.start = start + 4 + strLen;

        return str;
    }

    public void AddInt(int num){
        byte[] numBytes = BitConverter.intToBytes(num);
        if(bytes==null){
            bytes = numBytes;
        }else {
            bytes = Unpooled.copiedBuffer(bytes,numBytes).array();
        }
    }

    public int GetInt(){
        if(bytes==null)
            return 0;
        if(bytes.length < start+4)
            return 0;

        int result = BitConverter.BytesToInt(bytes,start);
        start = start + 4;
        return result;
    }

    public void AddFloat(float num){
        byte[] numBytes = BitConverter.FloatToBytes(num);
        if(bytes==null)
            bytes = numBytes;
        else
            bytes = Unpooled.copiedBuffer(bytes,numBytes).array();
    }
    public float GetFloat(){
        if(bytes==null)
            return 0;
        if(bytes.length < start + 4)
            return 0;
        float result = BitConverter.BytesToFloat(bytes,start);
        start = start + 4;
        return result;
    }
}
