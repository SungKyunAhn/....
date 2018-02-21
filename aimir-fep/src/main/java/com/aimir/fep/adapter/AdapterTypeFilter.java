package com.aimir.fep.adapter;

import java.io.IOException;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class AdapterTypeFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader mr, MetadataReaderFactory mrf) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

}
