package org.easyubl.truststore;

import java.security.KeyStore;

public interface TruststoreProvider {

    HostnameVerificationPolicy getPolicy();

    KeyStore getTruststore();

}
