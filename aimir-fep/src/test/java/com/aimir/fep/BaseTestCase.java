package com.aimir.fep;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

import java.util.*;

import javax.persistence.EntityManager;

/**
 * Base class for running DAO tests.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@ContextConfiguration(locations = {"classpath:config/spring.xml"})
public abstract class BaseTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private EntityManager em;

    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());
    /**
     * ResourceBundle loaded from src/test/resources/${package.name}/ClassName.properties (if exists)
     */
    protected ResourceBundle rb;

    /**
     * Default constructor - populates "rb" variable if properties file exists for the class in
     * src/test/resources.
     */
    public BaseTestCase() {
        // Since a ResourceBundle is not required for each class, just
        // do a simple check to see if one exists
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException mre) {
            //log.debug("No resource bundle found for: " + className);
        }
    }

    /**
     * Utility method to populate a javabean-style object with values
     * from a Properties file
     *
     * @param obj the model object to populate
     * @return Object populated object
     * @throws Exception if BeanUtils fails to copy properly
     */
    protected Object populate(Object obj) throws Exception {
        // loop through all the beans methods and set its properties from its .properties file
        Map<String, String> map = new HashMap<String, String>();

        for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            map.put(key, rb.getString(key));
        }

        BeanUtils.copyProperties(obj, map);

        return obj;
    }

    /**
     * Create a HibernateTemplate from the SessionFactory and call flush() and clear() on it.
     * Designed to be used after "save" methods in tests: http://issues.appfuse.org/browse/APF-178.
     *
     * @throws org.springframework.beans.BeansException
     *          when can't find 'sessionFactory' bean
     */
    protected void flush() throws BeansException {
        em.flush();
        em.clear();
    }

    public static void  main(String args[]) {
    	byte[] testByte = Hex.encode("0000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e40000000000000007dc10b8f40000000000000007dc10b9e4000");
    	System.out.println("length: "+testByte.length);
    	System.out.println(Util.getHexString(testByte));
    }
}