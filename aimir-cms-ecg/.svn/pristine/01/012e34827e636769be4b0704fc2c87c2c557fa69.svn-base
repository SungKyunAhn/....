package com.aimir.aimir_cms_ecg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;

public class CheckCustomerId {
    
    @Test
    public void check() throws Exception {
        BufferedReader src_reader = null;
        BufferedReader tgt_reader = null;
        
        try {
            File src_file = new File("D:\\workspace_3\\aimir-cms-ecg\\src_customerid.txt");
            File tgt_file = new File("D:\\workspace_3\\aimir-cms-ecg\\tgt_customerid.txt");
            
            src_reader = new BufferedReader(new FileReader(src_file));
            tgt_reader = new BufferedReader(new FileReader(tgt_file));
            
            String src_customerid = null;
            String tgt_customerid = null;
            while ((src_customerid = src_reader.readLine()) != null) {
                System.out.println("check [" + src_customerid + "]");
                while ((tgt_customerid = tgt_reader.readLine()) != null) {
                    if (src_customerid == tgt_customerid) {
                        System.out.println("match [" + src_customerid + "]");
                        break;
                    }
                }
            }
        }
        finally {
            if (src_reader != null) src_reader.close();
            if (tgt_reader != null) tgt_reader.close();
        }
    }
}
