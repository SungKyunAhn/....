package com.aimir.web.servlet.view;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.spring.web.servlet.view.JsonView;

/**
 * TreeJsonView.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 27.   v1.0       문동규   TreeGrid 에서 사용하는 View. JSONArray 형태로 리턴시킴.([{"name":"object1"}, {"name":"object2"}])
 *                                    attributeName은 반드시 "result" 로 해야함. ex) mav.addObject("result", result);
 * </pre>
 */
public class TreeJsonView extends JsonView {

    public TreeJsonView() {
        super();
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType(getContentType());
        writeJSON(model, request, response);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void writeJSON(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObj = (JSONObject)createJSON(model, request, response);
        JSON json = null;
        Iterator<String> itr = jsonObj.keys();

        if (itr.hasNext()) {
            json = jsonObj.getJSONArray(itr.next());
        }

        json.write(response.getWriter());
    }
}