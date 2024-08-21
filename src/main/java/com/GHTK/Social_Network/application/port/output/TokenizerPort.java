package com.GHTK.Social_Network.application.port.output;

public interface TokenizerPort {
    public void encode(String inputText);

    public long[] getIds();

    public long[] getAttentionMask();

    public String[] getTokens();
}