// Copyright 2011, Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.api.ads.common.lib.soap.axis;

import com.google.api.ads.common.lib.exception.ServiceException;
import com.google.api.ads.common.lib.soap.RequestInfo;
import com.google.api.ads.common.lib.soap.ResponseInfo;
import com.google.api.ads.common.lib.soap.SoapCall;
import com.google.api.ads.common.lib.soap.SoapCallReturn;
import com.google.api.ads.common.lib.soap.SoapClientHandler;
import com.google.api.ads.common.lib.soap.SoapClientHandlerInterface;
import com.google.api.ads.common.lib.soap.SoapServiceDescriptor;
import com.google.api.ads.common.lib.soap.compatability.AxisCompatible;
import com.google.common.base.Preconditions;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

/**
 * SOAP Client Handler implementation for use with Axis 1.x.
 *
 * @author Adam Rogal
 */
public class AxisHandler extends SoapClientHandler<Stub> {

  /**
   * Sets the endpoint address of the given SOAP client.
   *
   * @param soapClient the SOAP client to set the endpoint address for
   * @param endpointAddress the target endpoint address
   */
  public void setEndpointAddress(Stub soapClient, String endpointAddress) {
    soapClient._setProperty("javax.xml.rpc.service.endpoint.address", endpointAddress);
  }

  /**
   * Returns a SOAP header from the given SOAP client, if it exists.
   *
   * @param soapClient the SOAP client to check for the given header
   * @param headerName the name of the header being looked for
   * @return the header element, if it exists
   */
  public Object getHeader(Stub soapClient, String headerName) {
    SOAPHeaderElement[] soapHeaders = soapClient.getHeaders();
    for (SOAPHeaderElement soapHeader : soapHeaders) {
      if (soapHeader.getName().equals(headerName)) {
        return soapHeader;
      }
    }
    return null;
  }

  /**
   * Clears all of the SOAP headers from the given SOAP client.
   *
   * @param soapClient the client to remove the headers from
   */
  public void clearHeaders(Stub soapClient) {
    soapClient._setProperty(HTTPConstants.REQUEST_HEADERS, new Hashtable<String, String>());
    soapClient.clearHeaders();
  }

  /**
   * @see  SoapClientHandler#setHeader(Object, String, String, Object)
   */
  public void setHeader(Stub soapClient, String namespace, String headerName,
      Object headerValue) {
    try {
      QName qName = new QName(namespace, headerName);
      SOAPHeaderElement soapHeaderElement = new SOAPHeaderElement(qName);
      soapHeaderElement.setObjectValue(headerValue);
      soapHeaderElement.setActor(null);
      soapClient.setHeader(soapHeaderElement);
    } catch (SOAPException e) {
      throw new ServiceException("Could not set header.", e);
    }
  }
  
  /**
   * Updates the child attribute of headerName named childName to childValue.
   *
   * @param soapClient the stub
   * @param parentHeaderName the name of the parent header
   * @param childName the name of the child
   * @param childValue the value for the child
   *
   * @throws NullPointerException if no header exists named parentHeaderName
   */
  public void setHeaderChild(Stub soapClient, String parentHeaderName, String childName,
      Object childValue) {
    SOAPHeaderElement headerElement = (SOAPHeaderElement) getHeader(soapClient, parentHeaderName);
    Object headerObject = Preconditions.checkNotNull(headerElement,
        "Parent header named %s does not exist", parentHeaderName).getObjectValue();
    try {
      BeanUtils.setProperty(headerObject, childName, childValue);
    } catch (IllegalAccessException e) {
      throw new ServiceException("Failed to set header child " + childName, e);
    } catch (InvocationTargetException e) {
      throw new ServiceException("Failed to set header child " + childName, e);
    }
  }

  /**
   * @see SoapClientHandler#putAllHttpHeaders(Object, Map)
   */
  public void putAllHttpHeaders(Stub soapClient, Map<String, String> headersMap) {
    Hashtable<String, String> headers =
        (Hashtable<String, String>) soapClient._getProperty(HTTPConstants.REQUEST_HEADERS);
    if (headers == null) {
      headers = new Hashtable<String, String>();
    }
    headers.putAll(headersMap);
    soapClient._setProperty(HTTPConstants.REQUEST_HEADERS, headers);
  }

  /**
   * Set whether SOAP requests should use compression.
   *
   * @param soapClient the client to set compression settings for
   * @param compress whether or not to use compression
   */
  // TODO(arogal): Add info to README that you need to add the
  //    client-config.wsdd to the root of the classpath
  public void setCompression(Stub soapClient, boolean compress) {
    soapClient._setProperty(HTTPConstants.MC_ACCEPT_GZIP, compress);
    soapClient._setProperty(HTTPConstants.MC_GZIP_REQUEST, compress);
  }

  /**
   * Creates a SOAP client using a SOAP service descriptor.
   *
   * @param soapServiceDescriptor the descriptor to use for creating a client
   * @return the SOAP client for this descriptor
   * @throws ServiceException thrown if the SOAP client cannot be created
   */
  public Stub createSoapClient(SoapServiceDescriptor soapServiceDescriptor)
      throws ServiceException {
    try {
      if (soapServiceDescriptor instanceof AxisCompatible) {
        AxisCompatible axisCompatibleService = (AxisCompatible) soapServiceDescriptor;
        Service locator = (Service) axisCompatibleService.getLocatorClass()
            .getConstructor(new Class[0]).newInstance(new Object[0]);
        return (Stub) locator.getClass().getMethod("getPort", Class.class)
            .invoke(locator, soapServiceDescriptor.getInterfaceClass());
      }
      throw new ServiceException("Service [" + soapServiceDescriptor +
          "] not compatible with Axis", null);
    } catch (SecurityException e) {
      throw new ServiceException("Unexpected Exception.", e);
    } catch (NoSuchMethodException e) {
      throw new ServiceException("Unexpected Exception.", e);
    } catch (IllegalArgumentException e) {
      throw new ServiceException("Unexpected Exception.", e);
    } catch (IllegalAccessException e) {
      throw new ServiceException("Unexpected Exception.", e);
    } catch (InvocationTargetException e) {
      throw new ServiceException("Unexpected Exception.", e);
    } catch (ClassNotFoundException e) {
      throw new ServiceException("Unexpected Exception.", e);
    } catch (InstantiationException e) {
      throw new ServiceException("Unexpected Exception.", e);
    }
  }

  /**
   * Invoke a SOAP call.
   *
   * @param soapCall the call to make to a SOAP web service
   * @return information about the SOAP response
   */
  public SoapCallReturn invokeSoapCall(SoapCall<Stub> soapCall) {
    Stub stub = soapCall.getSoapClient();
    SoapCallReturn.Builder builder = new SoapCallReturn.Builder();
    synchronized (stub) {
      Object result = null;
      try {
        result = invoke(soapCall);
      } catch (InvocationTargetException e) {
        builder.withException(e.getTargetException());
      } catch (Exception e) {
        builder.withException(e);
      } finally {
        MessageContext messageContext = stub._getCall().getMessageContext();
        try {
          builder.withRequestInfo(new RequestInfo.Builder().withSoapRequestXml(
              messageContext.getRequestMessage().getSOAPPartAsString())
                  .withMethodName(stub._getCall().getOperationName().getLocalPart())
                  .withServiceName(stub.getPortName().getLocalPart())
                  .withUrl(stub._getCall().getTargetEndpointAddress())
                  .build());
        } catch (AxisFault e) {
          builder.withException(e);
        }
        try {
          builder.withResponseInfo(new ResponseInfo.Builder().withSoapResponseXml(
              messageContext.getResponseMessage().getSOAPPartAsString()).build());
        } catch (AxisFault e) {
         builder.withException(e);
        }
      }

      return builder.withReturnValue(result).build();
    }
  }

  /**
   * @see SoapClientHandlerInterface#getEndpointAddress(Object)
   */
  public String getEndpointAddress(Stub soapClient) {
    return (String) soapClient._getProperty("javax.xml.rpc.service.endpoint.address");
  }

  /**
   * @see SoapClientHandlerInterface#createSoapHeaderElement(QName)
   */
  public javax.xml.soap.SOAPHeaderElement createSoapHeaderElement(QName qName) {
    return new SOAPHeaderElement(qName);
  }
}
