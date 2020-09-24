package me.ste.stevesdbserver.network.packet;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketId {
    int value();
}