package Protocol;

public interface IProtocolBase {
    /**
     * 从字节缓冲区中的第start个字节开始解码长度为Length的字节流，
     * 适用于 字节流、字符串流、protocol、json、xml等各类协议
     * @param readbuff 缓冲区
     * @param start
     * @param length
     * @return
     */
    IProtocolBase Decode(byte[] readbuff,int start,int length);

    /**
     * 编码协议为字节
     * @return
     */
    byte[] Encde();

    /**
     * 协议名，用于消息分发
     * @return
     */
    String GetName();

    /**
     * 协议描述，用于Debug
     * @return
     */
    String GetDesc();
}
