package com.impl;

import com.interf.EchoService;

/**
 * @author <a href="mailto:linxh@59store.com">linxiaohui</a>
 * @version 1.0 16/12/16
 * @since 1.0
 */
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String ping) {
        return ping != null ? ping + "--> I am ok." : "I am ok.";
    }
}
