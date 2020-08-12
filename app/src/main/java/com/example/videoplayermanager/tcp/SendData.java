package com.example.videoplayermanager.tcp;

import com.xuhao.didi.core.iocore.interfaces.ISendable;

public class SendData implements ISendable {
    private byte[] data;

    public SendData(byte[] sendData) {
        this.data=sendData;
    }

    @Override
    public byte[] parse() {
        return data;
    }

}
