package com.jn.langx.security.messagedigest.provider;

import com.jn.langx.security.messagedigest.LangxMessageDigest;
import com.jn.langx.security.messagedigest.digest.Digest;
import com.jn.langx.security.messagedigest.digest.Xof;
import com.jn.langx.security.messagedigest.digest.impl.SM3Digest;

public class SM3MessageDigest extends LangxMessageDigest {
    public SM3MessageDigest(Digest digest) {
        super((SM3Digest)digest);
    }

    public SM3MessageDigest(Xof digest, int outputSize) {
        super(digest, outputSize);
    }
}
