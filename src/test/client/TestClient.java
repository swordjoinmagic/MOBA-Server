package test.client;

import Protocol.ProtocolBytes;
import Util.BitConverter;

import java.net.Socket;
import java.nio.ByteBuffer;

public class TestClient {
    public static void main(String[] args){
        try{
            Socket socket = new Socket("127.0.0.1",8081);

//            byte[] bytes = "hello".getBytes();
//            byte[] lenBytes = BitConverter.intToBytes("hello".length());
//            byte[] floatBytes = BitConverter.FloatToBytes(3.1415926f);

//            // 额外分配四个字节，用于表示消息的长度
//            ByteBuffer buffer = ByteBuffer.allocate(4+4+bytes.length+4);

//            buffer.putInt(bytes.length+lenBytes.length+floatBytes.length).put(lenBytes).put(bytes).put(floatBytes);

            ProtocolBytes protocolBytes = new ProtocolBytes();
            protocolBytes.AddString("hello");
            protocolBytes.AddFloat(3.1415926f);

            byte[] bytes = protocolBytes.Encde();
            // 额外分配四个字节，用于表示消息的长度
            ByteBuffer buffer = ByteBuffer.allocate(4+bytes.length);
            buffer.putInt(bytes.length).put(bytes);

            socket.getOutputStream().write(buffer.array());
            socket.getOutputStream().flush();

            socket.close();
        }catch (Exception e){}
    }
}
