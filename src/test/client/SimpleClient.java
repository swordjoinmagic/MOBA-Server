package test.client;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public class SimpleClient {

    static byte[] readBuff = new byte[2048];
    static int buffCount = 0;
    static int length = 0;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(),8081);

            System.out.println("sockeet创建成功");

            // 发送1000条hello消息，查看粘包现象
            for (int i=0;i<1000;i++) {

                byte[] bytes = "hello".getBytes();

                // 额外分配四个字节，用于表示消息的长度
                ByteBuffer buffer = ByteBuffer.allocate(4+bytes.length);

                buffer.putInt(bytes.length).put(bytes);

                socket.getOutputStream().write(buffer.array());
                socket.getOutputStream().flush();
//                socket.getOutputStream().write("hello\n".getBytes());

            }
//
            while (true){
                int a = socket.getInputStream().read(readBuff,buffCount,2048-buffCount);
                System.out.println(a);
                System.out.println("buffcount:"+buffCount);
                buffCount += a;
                processData();
            }
//            socket.getOutputStream().flush();

//            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void processData(){
        // 如果输入的数据的长度小于4，
        // 那么说明此消息还不足以构成一个数据包
        if(buffCount < 4){
            return;
        }

        // 获得此消息前4个字节，以此获得数据包长度
        byte[] lenBytes = new byte[4];
        for(int i=0;i<4;i++) {
            System.out.println("lenbytes["+i+"]:"+readBuff[i]);
            lenBytes[i] = readBuff[i];
        }
        length = byteArrayToInt(lenBytes);
        System.out.println("length:"+length);

        // 还未读到完整数据包
        if(buffCount<length+4){
            return;
        }

        // 处理消息
        String str = new String(readBuff,4,length, Charset.defaultCharset());
        System.out.println("客户端收到消息:"+str);

        // 清除已处理的消息
        int count = buffCount - length - 4;
        System.arraycopy(readBuff,4+length,readBuff,0,count);
        buffCount = count;

        System.out.println("buffcount:"+buffCount);

        if(buffCount>0)
            processData();
    }

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
