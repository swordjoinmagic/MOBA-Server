package Protocol;

public interface IProtocolBase {

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
