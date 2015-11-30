/**
 *
 */
package com.jdk2010.framework.web.session.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jdk2010.framework.web.session.redis.RedisHttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer {

    public static byte[] serialize(Object obj) throws IOException {
        Output output = null;
        Kryo kryo = new Kryo();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output = new Output(baos);
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } finally {
            if (output != null) {
                output.close();
            }
            kryo = null;
        }
    }

    public static RedisHttpSession deserialize(byte[] bits) throws IOException {
        if (bits == null || bits.length == 0) {
            return null;
        }
        Input ois = null;
        Kryo kryo = new Kryo();
        RedisHttpSession session = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bits);
            ois = new Input(bais);
            session = (RedisHttpSession) kryo.readClassAndObject(ois);
        } finally {
            if (ois != null) {
                ois.close();
            }
            kryo = null;
        }
        return session;
    }

}
