
package com.google.api.ads.dfp.jaxws.v201502;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *             Creates new {@link Package} objects.
 *             
 *             For each package, the following fields are required:
 *             <ul>
 *             <li>{@link Package#proposalId}</li>
 *             <li>{@link Package#productPackageId}</li>
 *             <li>{@link Package#name}</li>
 *             </ul>
 *             
 *             @param packageDtos the packages to create
 *             @return the created packages with their IDs filled in
 *           
 * 
 * <p>Java class for createPackages element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="createPackages">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="packageDtos" type="{https://www.google.com/apis/ads/publisher/v201502}Package" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "packageDtos"
})
@XmlRootElement(name = "createPackages")
public class PackageServiceInterfacecreatePackages {

    protected List<Package> packageDtos;

    /**
     * Gets the value of the packageDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the packageDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackageDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Package }
     * 
     * 
     */
    public List<Package> getPackageDtos() {
        if (packageDtos == null) {
            packageDtos = new ArrayList<Package>();
        }
        return this.packageDtos;
    }

}
