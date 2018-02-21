/**
 * Copyright (c) 2015, Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com >.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
   1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
   3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
   
   THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.aimir.fep.iot.domain.resources;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * execReqArgsListType domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "execReqArgsListType", propOrder = {
    "reset",
    "reboot",
    "upload",
    "download",
    "softwareInstall",
    "softwareUpdate",
    "softwareUninstall"
})
public class ExecReqArgsListType {

    protected List<ResetArgsType> reset;
    protected List<RebootArgsType> reboot;
    protected List<UploadArgsType> upload;
    protected List<DownloadArgsType> download;
    protected List<SoftwareInstallArgsType> softwareInstall;
    protected List<SoftwareUpdateArgsType> softwareUpdate;
    protected List<SoftwareUninstallArgsType> softwareUninstall;

    /**
     * Gets the value of the reset property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reset property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReset().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResetArgsType }
     * 
     * 
     */
    public List<ResetArgsType> getReset() {
        if (reset == null) {
            reset = new ArrayList<ResetArgsType>();
        }
        return this.reset;
    }

    /**
     * Gets the value of the reboot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reboot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReboot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RebootArgsType }
     * 
     * 
     */
    public List<RebootArgsType> getReboot() {
        if (reboot == null) {
            reboot = new ArrayList<RebootArgsType>();
        }
        return this.reboot;
    }

    /**
     * Gets the value of the upload property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the upload property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUpload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UploadArgsType }
     * 
     * 
     */
    public List<UploadArgsType> getUpload() {
        if (upload == null) {
            upload = new ArrayList<UploadArgsType>();
        }
        return this.upload;
    }

    /**
     * Gets the value of the download property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the download property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDownload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DownloadArgsType }
     * 
     * 
     */
    public List<DownloadArgsType> getDownload() {
        if (download == null) {
            download = new ArrayList<DownloadArgsType>();
        }
        return this.download;
    }

    /**
     * Gets the value of the softwareInstall property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the softwareInstall property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoftwareInstall().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SoftwareInstallArgsType }
     * 
     * 
     */
    public List<SoftwareInstallArgsType> getSoftwareInstall() {
        if (softwareInstall == null) {
            softwareInstall = new ArrayList<SoftwareInstallArgsType>();
        }
        return this.softwareInstall;
    }

    /**
     * Gets the value of the softwareUpdate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the softwareUpdate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoftwareUpdate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SoftwareUpdateArgsType }
     * 
     * 
     */
    public List<SoftwareUpdateArgsType> getSoftwareUpdate() {
        if (softwareUpdate == null) {
            softwareUpdate = new ArrayList<SoftwareUpdateArgsType>();
        }
        return this.softwareUpdate;
    }

    /**
     * Gets the value of the softwareUninstall property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the softwareUninstall property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoftwareUninstall().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SoftwareUninstallArgsType }
     * 
     * 
     */
    public List<SoftwareUninstallArgsType> getSoftwareUninstall() {
        if (softwareUninstall == null) {
            softwareUninstall = new ArrayList<SoftwareUninstallArgsType>();
        }
        return this.softwareUninstall;
    }

}
