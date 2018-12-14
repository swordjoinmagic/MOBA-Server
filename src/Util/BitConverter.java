package Util;

public class BitConverter {
    /**
     * 将整形转化成字节
     * @param num
     * @return
     */
    public static byte[] intToBytes(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (24 - i * 8));
        }
        return b;
    }

    public static int BytesToInt(byte[] b,int start) {
        return   b[3+start] & 0xFF |
                (b[2+start] & 0xFF) << 8 |
                (b[1+start] & 0xFF) << 16 |
                (b[start] & 0xFF) << 24;
    }

    public static byte[] FloatToBytes(float data)
    {
        int intBits = Float.floatToIntBits(data);
        return intToBytes(intBits);
    }

    public static float BytesToFloat(byte[] bytes,int start){
        int i = BytesToInt(bytes,start);
        return Float.intBitsToFloat(i);
    }
}
