package test;

import Protocol.ProtocolBytes;
import Util.BitConverter;

public class test1 {
    public static void main(String[] args){
        ProtocolBytes protocolBytes = new ProtocolBytes();
        protocolBytes.AddString("hello");
        protocolBytes.AddInt(3);
        byte[] bytes = protocolBytes.Encde();
        for (int i=0;i<bytes.length;i++) {
            System.out.println("bytes["+i+"]"+bytes[i]);
        }
    }
}
