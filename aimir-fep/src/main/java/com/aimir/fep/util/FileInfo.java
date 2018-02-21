package com.aimir.fep.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * File Information
 * 
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="fileInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileInfo", propOrder = {
    "fileDate",
    "fileName",
    "fileSize"
})
public class FileInfo implements java.io.Serializable
{
    private String fileDate = null;
    private String fileName = null;
    private long fileSize = 0;

    public FileInfo() { }

    public FileInfo(String fileDate, String fileName, long fileSize) 
    { 
        this.fileDate = fileDate;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileDate()
    {
        return fileDate;
    }
    public void setFileDate(String fileDate)
    {
        this.fileDate = fileDate;
    }

    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public long getFileSize()
    {
        return fileSize;
    }
    public void setFileSize(long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("file info[");
        sb.append("(fileDate=").append(getFileDate()).append("),");
        sb.append("(fileName=").append(getFileName()).append("),");
        sb.append("(fileSize=").append(getFileSize()).append(')');
        sb.append("]\n");

        return sb.toString();
    }
}
