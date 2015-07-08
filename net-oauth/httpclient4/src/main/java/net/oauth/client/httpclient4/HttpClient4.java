/*
 * Copyright 2008 Sean Sullivan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oauth.client.httpclient4;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.oauth.client.ExcerptInputStream;
import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * Utility methods for an OAuth client based on the <a
 * href="http://hc.apache.org">Apache HttpClient</a>.
 * 
 * @author Sean Sullivan
 */
public class HttpClient4 implements net.oauth.http.HttpClient {

  private HttpHost proxy = null;

  public HttpClient4(HttpHost proxy) {
    init();
    this.proxy = proxy;
  }

  public HttpClient4() {
    init();
  }
  
  private HttpClient client = null;
  
  private synchronized void init() {
    if (client==null) {
      client = new DefaultHttpClient();
    }
  }

  public synchronized HttpResponseMessage execute(HttpMessage request, Map<String, Object> parameters) throws IOException {
    final String method = request.method;
    final String url = request.url.toExternalForm();
    final InputStream body = request.getBody();
    final boolean isDelete = DELETE.equalsIgnoreCase(method);
    final boolean isPost = POST.equalsIgnoreCase(method);
    final boolean isPut = PUT.equalsIgnoreCase(method);
    byte[] excerpt = null;
    HttpRequestBase httpRequest;
    if (isPost || isPut) {
      HttpEntityEnclosingRequestBase entityEnclosingMethod = isPost ? new HttpPost(url) : new HttpPut(url);
      if (body != null) {
        ExcerptInputStream e = new ExcerptInputStream(body);
        excerpt = e.getExcerpt();
        String length = request.removeHeaders(HttpMessage.CONTENT_LENGTH);
        entityEnclosingMethod.setEntity(new InputStreamEntity(e, (length == null) ? -1 : Long.parseLong(length)));
      }
      httpRequest = entityEnclosingMethod;
    } else if (isDelete) {
      httpRequest = new HttpDelete(url);
    } else {
      httpRequest = new HttpGet(url);
    }
    for (Map.Entry<String, String> header : request.headers) {
      httpRequest.addHeader(header.getKey(), header.getValue());
    }
    HttpParams params = httpRequest.getParams();
    for (Map.Entry<String, Object> p : parameters.entrySet()) {
      String name = p.getKey();
      String value = p.getValue().toString();
      if (FOLLOW_REDIRECTS.equals(name)) {
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.parseBoolean(value));
      } else if (READ_TIMEOUT.equals(name)) {
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.parseInt(value));
      } else if (CONNECT_TIMEOUT.equals(name)) {
        params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Integer.parseInt(value));
      }
    }

    params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

    if (proxy != null) {
      client = acceptAllTlsCertificates(client);
      client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    // Flush any previous response entity
    if (lastResponse!=null) {
      EntityUtils.consumeQuietly(lastResponse.getEntity());
    }
    lastResponse = client.execute(httpRequest);
    return new HttpMethodResponse(httpRequest, lastResponse, excerpt, request.getContentCharset());
  }
  
  HttpResponse lastResponse = null;

  private HttpClient acceptAllTlsCertificates(HttpClient client) {
    try {
      X509TrustManager tm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      };
      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(null, new TrustManager[] { tm }, null);
      SSLSocketFactory ssf = new SSLSocketFactory(ctx);
      ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      ClientConnectionManager ccm = client.getConnectionManager();
      SchemeRegistry sr = ccm.getSchemeRegistry();
      sr.register(new Scheme("https", ssf, 443));
      return new DefaultHttpClient(ccm, client.getParams());
    } catch (Exception ex) {
      return null;
    }
  }


}
